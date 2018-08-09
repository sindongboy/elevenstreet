package review;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;

import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.usermodeling.topical.util.MovieEntityDictionary;
import com.skplanet.nlp.usermodeling.topical.util.StopWordDictionary;
import com.skplanet.tas.review.experiment.WordCombiner;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.ml.TrainingSetCreator;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.rule.HeuristicRule;

@Service
public class ReviewScoringService {
	private static final Logger LOG = Logger.getLogger(ReviewScoringService.class);

	MovieEntityDictionary movieDic;
	StopWordDictionary stopwordDic;
	NLPAPI nlpapi;
	String sep;	// 구분자.

	List<Map> statAl;
	HeuristicRule hr;
	FeatureExtractor fe;

	Predictor pred;

	Classifier cls;
	Instances header;

	String colStr;

	TrainingSetCreator tsc;

	public void loadClasses(String path) {

		File f = new File(path);
		URL u = null;
		try {
			u = f.toURL();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			LOG.error( "[Exception] ", e1 );
		}
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class urlClass = URLClassLoader.class;
		Method method = null;
		try {
			method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			LOG.error( "[Exception] ", e1 );
		}
		method.setAccessible(true);
		try {
			method.invoke(urlClassLoader, new Object[]{u});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			LOG.error( "[Exception] ", e1 );
		}
	}


	public void loadClassifier(String path) {
		try {

			LOG.info(path);

			// read model and header
			Vector v = (Vector) SerializationHelper.read(path);
			cls = (Classifier) v.get(0);
			header = (Instances) v.get(1);

		} catch(Exception e) {
			LOG.error( "[Exception] ", e );
		}
	}


	public ReviewScoringService() {



		Properties prop = new Properties();

		/*
		 * Load Resources...
		 */
		String resourcePath = null;
		String clsPath = null;
		try {
			prop.load( Thread.currentThread().getContextClassLoader().getResourceAsStream("path.properties") );
			resourcePath = prop.getProperty("nlpPath");
			clsPath = resourcePath + "ranker/review-ranker-1.1.model"; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error( "[Exception] ", e );
		}

		loadClasses(resourcePath);
		loadClasses(resourcePath + "ranker/");

		hr = new HeuristicRule(false);
		fe = new FeatureExtractor();


		/*
		 * Load Classifier...
		 */

		loadClassifier(clsPath);

		tsc = new TrainingSetCreator();

		colStr = tsc.getColumnRow(fe, false);
		colStr = colStr.substring(0, colStr.length()-1) + "\n";




		/*
		 * Preparing Spam Filtering.
		 */


		pred = new Predictor();

		statAl = pred.getStat(false);



		sep = "^";

	}


	@Async
	public void getScoreAsync(
			String date,
			String contNo,
			String mCatID,
			String imgYn,
			String movieYn,
			String mobileYn,
			String txt,
			HttpServletRequest request,
			String callback
			){

		String aprvImgYn = "N";	// 실시간에서는 검수 결과가 들어올 수 없으므로 그냥 박아두기.
		

		if( date == null || date.equals("")) {
			date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		} else {
			date = date.trim();
		}

		Map<String, Object> featureHm = fe.extract(txt, "");

		Boolean isSpam = hr.isSpam(txt, new StringBuffer(), statAl, featureHm, 2.0d);


		Double score = 0.0d;

		if(isSpam) {
			score = -1.0d;
		} else {

			String[] lineArr = new String[5];
			lineArr[0] = null;
			lineArr[1] = imgYn;	// imgYn
			lineArr[2] = movieYn;	// movieYn
			lineArr[3] = mobileYn;	// mobileYn
			lineArr[4] = aprvImgYn;

			StringBuffer instSb = tsc.getInstance(lineArr, featureHm, txt);


			String inst = instSb.toString().substring(0, instSb.length()-1) + "\n";

			score = predict(colStr, inst, cls, header, statAl, fe, hr);
		
		}


		/*
		 * Callback URL 호출
		 */

		String uri = null;

		Properties prop = new Properties();

		StringBuffer sb = new StringBuffer();

		if( !callback.toLowerCase().startsWith("http://") ) {	// http로 시작하지 않으면.
			sb.append("http://");
		}
		
		sb.append( callback );
		sb.append( "?contNo=" );
		sb.append( contNo );
		sb.append( "&score=" );
		sb.append( score );

		uri = sb.toString();


		try {
			URL obj = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "EUC-KR"));	// 11번가 서버 인코딩에 맞춤.


			JSONParser parser = new JSONParser();
			JSONObject result = (JSONObject) parser.parse(in);



			if( result.get("resultCode").equals("fail") ) {
				LOG.error( "[ERROR] Callback Exception");
			}


			LOG.info( result );
			LOG.info( result.get("resultCode") );
			
			in.close();
		} catch(Exception e) {
			LOG.error( "[Exception] ", e );
		}


	}

	public double predict(String columnStr, String data, Classifier cls, Instances header, List<Map> statAl, FeatureExtractor fe, HeuristicRule hr) {

		data = columnStr + data;

		// 2-3. Load Data
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(data.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			LOG.error( "[Exception] ", e2 );
		}

		CSVLoader loader = new CSVLoader();
		Instances dataSet = null;
		
		try {
			loader.setSource(is);
			dataSet = loader.getDataSet();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error( "[Exception] ", e );
		}



		// 2-5. Save into DB
		int commitCnt = 0;

		String predicted = null;
		Double score = null;

		for(int i=0; i<dataSet.numInstances(); i++) {

			Instance curr = dataSet.instance(i);



			/*
			 * Prepared Classification
			 */
			// create an instance for the classifier that fits the training data
			// Instances object returned here might differ slightly from the one
			// used during training the classifier, e.g., different order of
			// nominal values, different number of attributes.
			Instance inst = new Instance(header.numAttributes());
			inst.setDataset(header);
			for (int n = 0; n < header.numAttributes(); n++) {
				Attribute att = dataSet.attribute(header.attribute(n).name());
				// original attribute is also present in the current dataset
				if (att != null) {
					if (att.isNominal()) {
						// is this label also in the original data?
						// Note:
						// "numValues() > 0" is only used to avoid problems with nominal 
						// attributes that have 0 labels, which can easily happen with
						// data loaded from a database
						if ((header.attribute(n).numValues() > 0) && (att.numValues() > 0)) {
							String label = curr.stringValue(att);
							int index = header.attribute(n).indexOfValue(label);
							if (index != -1)
								inst.setValue(n, index);
						}
					}
					else if (att.isNumeric()) {
						inst.setValue(n, curr.value(att));
					}
					else {
						throw new IllegalStateException("Unhandled attribute type!");
					}
				}
			}


			try {


				/*
				 * 커버가 안되면, ML 적용.
				 */

				int predClass = (int) cls.classifyInstance( inst );	// 0혹은 1.

				predicted = header.classAttribute().value(predClass);

				score = cls.distributionForInstance( inst )[predClass];

				if( predicted.equals("bad") )
					score = 1.0d - score;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error( "[Exception] ", e );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOG.error( "[Exception] ", e );
				LOG.error(curr);
			}

			commitCnt++;

			if( (commitCnt % 10000) == 0 ) {
				LOG.info(commitCnt);
			}

		}

		// init
		data = "";

		return score;
	}


}
