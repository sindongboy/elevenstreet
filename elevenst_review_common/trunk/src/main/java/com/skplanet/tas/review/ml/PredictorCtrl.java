package com.skplanet.tas.review.ml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.rule.HeuristicRule;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;

public class PredictorCtrl {
	
	private Set<String> ordNoHs;
	
	public PredictorCtrl() {
		 ordNoHs = new HashSet();
	}
	

	public void predict(String columnStr, String data, Classifier cls, BufferedWriter bw, Instances header, List<Map> statAl, Predictor pred, FeatureExtractor fe, HeuristicRule hr, List<String> rawAl) {

		

//		Map<String, Double> avgHm = statAl.get(0);
//		Map<String, Double> stdHm = statAl.get(1);


		//		System.out.println(columnStr);

		data = columnStr + data;



//		System.out.println(data);
		
		// 2-3. Load Data
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(data.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		CSVLoader loader = new CSVLoader();
		Instances dataSet = null;
		//					Instances prdDataSet = null;

		try {
			loader.setSource(is);
			dataSet = loader.getDataSet();

			//			dataSet = fs.getFilteredData(dataSet);	// feature selection
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


//		ModelLearner ml = new ModelLearner();
//		dataSet = ml.removeMid(dataSet);

		// 2-4. Classify the data
		//		try {
		//			dataSet = ml.classify(cls, dataSet);	// Score 필드 추가된 반환값.
		//		} catch (Exception e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		}


		// 2-5. Save into DB
		int commitCnt = 0;


		for(int i=0; i<dataSet.numInstances(); i++) {
			//			Instance inst = dataSet.instance(i);


			Instance curr = dataSet.instance(i);

			/*
			 * 원문 정보 읽어오기.
			 */
			String raw = rawAl.get(i);
			
			
			String[] rawArr = raw.split("\t");

//			String prdNo = rawArr[0];
//			String contNo = rawArr[4];
//			String ordNo = rawArr[3];
//			String cont = rawArr[8];
			
//			String prdNo = rawArr[0];
//			
//			
//			String cat1 = rawArr[2];
//			String cat2 = rawArr[3];
//			String cat3 = rawArr[4];
//			String cat4 = rawArr[5];
//			
//			String ordNo = rawArr[7];
//			
//			String reviewContNo = rawArr[8];
//			String imgYn = rawArr[9];
//			String movieYn = rawArr[10];
//			String mobileYn = rawArr[11];
//			
//			if(mobileYn.trim().equals(""))
//				mobileYn = "NA";
//			
//			String cont = "";	// 리뷰 내용.
//
//			if( rawArr.length >= 13 )
//				cont = rawArr[12];
			
			
			String reviewContNo = rawArr[0];

			String cat2 = rawArr[3];

			String imgYn = rawArr[9];
			String movieYn = rawArr[10];
			String mobileYn = rawArr[11];

			if(mobileYn.trim().equals(""))
				mobileYn = "NA";

			String cont = "";	// 리뷰 내용.

			String aprvImgYn = rawArr[17];

			//				if( lineArr.length == 9 )
			//				if( lineArr.length == 10 )
			cont = rawArr[15];
			
			String prdNo = rawArr[0];
			String ordNo = rawArr[7];
			
			

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
				 * HR
				 */
				
				Map<String, Object> featureHm = new HashMap();
				
				featureHm.put("len", inst.value(dataSet.attribute("len")));

				featureHm.put("wordCnt", inst.value(dataSet.attribute("wordCnt")));

				featureHm.put("avgWordLen", inst.value(dataSet.attribute("avgWordLen")));

				featureHm.put("spRatio", inst.value(dataSet.attribute("spRatio")));

				featureHm.put("jamoRatio", inst.value(dataSet.attribute("jamoRatio")));

				featureHm.put("charRepeatness", inst.value(dataSet.attribute("charRepeatness")));

				featureHm.put("wordRepeatness", inst.value(dataSet.attribute("wordRepeatness")));

				featureHm.put("engRatio", inst.value(dataSet.attribute("engRatio")));

				featureHm.put("numRatio", inst.value(dataSet.attribute("numRatio")));
				
				featureHm.put("notKoreanRatio", inst.value(dataSet.attribute("notKoreanRatio")));
				
				Boolean isSpam = hr.isSpam(cont, new StringBuffer(), statAl, featureHm, 2.0d);
//				Boolean isSpam = hr.isSpam("백종범 신동훈 유저모델링입니다.", new StringBuffer(), statAl, featureHm, 1.0d);
				
				
				String predicted = null;
				Double score = null;

				if( isSpam ) {
					
//					System.out.println(cont);
					
					predicted = "spam";
					score = -1.0d;

				} else {


					/*
					 * 커버가 안되면, ML 적용.
					 */

					if(cls == null) {
						
						System.out.println(header);
						System.exit(0);
					}
					
					int predClass = (int) cls.classifyInstance( inst );	// 0혹은 1.

					predicted = header.classAttribute().value(predClass);


					//				System.out.println(predClass + ", " + predicted);

					score = cls.distributionForInstance( inst )[predClass];



					if( predicted.equals("bad") )
						score = 1.0d - score;
					
//				score = cls.classifyInstance( inst );
//					
//					score = cls.classifyInstance( inst );	// 형식 점수.
					
					
					
//					if( inst.value(dataSet.attribute("idfSum")) > 0 ) {
//						score *= inst.value(dataSet.attribute("idfSum"));	
//					} else {
//						score = 0.0d;
//					}
					
//					score = inst.value(dataSet.attribute("idfSum"));
					
					

					/*
					 * ordNo 기반 필터링 (최초 1회만 출력)
					 */
					
					StringBuffer ordNoSb = new StringBuffer();
					ordNoSb.append(prdNo);
					ordNoSb.append("_");
//					ordNoSb.append(score);
					ordNoSb.append(ordNo);
					ordNoSb.append("_");
					ordNoSb.append(cont.trim().length());
					
//					if(cont.equals("배송 GOOD! 사은품도 GOOD!"))
//						System.out.println(ordNoSb + ", " + ordNoHs.contains(ordNoSb.toString()) );
					
					if( ordNoHs.contains(ordNoSb.toString()) ) {
						
						try {
							bw.write("-2.0\n");	// -2.0으로 표시.
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						continue;
					}
					
					ordNoHs.add(ordNoSb.toString());
					
					
//					score = inst.value(dataSet.attribute("idfSum"));
//					
//					if( score < 0 ) {
//						score = Math.log( (score * -1.0d) + 1.0d);
//						score *= -1.0d;
//					} else {
//						score = Math.log( score + 1.0d);
//					}
					
				}

//				bw.write(score + ", " + predicted + "\n");
				
//				bw.write(score + "\t" + predicted + "\n");
				bw.write(score + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(curr);
//				System.out.println(inst);
			}

			commitCnt++;

			if( (commitCnt % 10000) == 0 ) {
				System.out.println(commitCnt);
			}

		}

		// init
		data = "";

	}

	public static void main(String[] args) {

		if( args.length < 3 ) {
			System.out.println("Please input the exact arguments: [serializedModel] [rawData] [testSet] [exportFilePath]");
			System.exit(0);
		}
		
		Predictor pred = new Predictor();
		PredictorCtrl pc = new PredictorCtrl();

		FeatureExtractor fe = new FeatureExtractor();
		HeuristicRule hr = new HeuristicRule(false);
		
		
		/*
		 * 1. Serialize the prediction model
		 */
		Classifier cls = null;
		Instances header = null;
		try {

			//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(args[0]));
			//			
			//			cls = (Classifier) ois.readObject();

			// read model and header
			Vector v = (Vector) SerializationHelper.read(args[0]);
			
//			final ObjectInputStream in = new ObjectInputStream(
//			        new BufferedInputStream(new FileInputStream(args[0])));

			    
			
			cls = (Classifier) v.get(0);
			header = (Instances) v.get(1);


			//			RandomForest rf = (RandomForest) ois.readObject();
			//			cls = rf;

			//			cls = (RandomForest) ois.readObject();

			//			ois.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

		/*
		 * 2. Loading the test data & predicting
		 */

		/*
		 * Get Avg, Std. for each column (test / Standard)
		 */
//		List<Map> statAl = pred.getStat(new File("/Users/skplanet/Documents/2015_11ST/data/160118/11st_review_score//total.csv"));
		List<Map> statAl = pred.getStat(false);
//		System.out.println(statAl.get(0));
//		System.out.println(statAl.get(1));
		
		String rawDir = args[1];
		String outDir = args[3];
		File dir = new File(args[2]);

		File[] fileList = dir.listFiles();

		for(File file : fileList) {

			if( file.isDirectory() || !file.getName().endsWith(".csv"))
				continue;


			try {

//				/*
//				 * Get Avg, Std. for each column
//				 */
//				List<Map> statAl = pred.getStat(file);


				//				System.exit(0);

				FileReader fr = new FileReader( file );
				BufferedReader br = new BufferedReader(fr); 

				FileReader fr2 = new FileReader( rawDir + "/" + file.getName().replace("_test", "").replace(".csv", ".tsv") );
//				FileReader fr2 = new FileReader( rawDir + "/" + file.getName().replace("_test", "") );
				BufferedReader br2 = new BufferedReader(fr2); 

								
				
				System.out.println( file.getName() );


				FileWriter fw = new FileWriter( outDir + "/" + file.getName() );
				BufferedWriter bw = new BufferedWriter(fw);

				String line = null;
				String rawLine = null;


				String columnStr = br.readLine() + "\n";	// 2-1. Get the Column line

				StringBuffer dataSb = new StringBuffer();
				List<String> rawAl = new ArrayList();
				br2.readLine();	// skip Column
				

				int lineCnt = 0;

				System.out.println("Start!");


				// 2-2. Get the lines

				while( ( line=br.readLine() ) != null ) {
//					dataSb += line + "\n";
					dataSb.append(line);
					dataSb.append("\n");
					
					rawLine = br2.readLine();	// 원문 읽기.

					rawAl.add(rawLine);
					
					lineCnt++;

					if( (lineCnt % 10) == 0 ) {

						pc.predict(columnStr, dataSb.toString(), cls, bw, header, statAl, pred, fe, hr, rawAl);

						// Init.
						dataSb = new StringBuffer();
						rawAl = new ArrayList();
					}

					if( (lineCnt % 10000) == 0 ) {
						System.out.println("readLine = " + lineCnt);
					}
				}

				System.out.println("readLine = " + lineCnt);

				if(dataSb.length() != 0)
					pc.predict(columnStr, dataSb.toString(), cls, bw, header, statAl, pred, fe, hr, rawAl);

				br.close();
				fr.close();
				
				br2.close();
				fr2.close();

				bw.close();
				fw.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}


	}
}
