package review;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
import com.skplanet.nlp.usermodeling.topical.util.TermExtractor;
import com.skplanet.nlp.usermodeling.topical.util.TermExtractor.TextAnalysisInfoForSimilarWord;
import com.skplanet.tas.review.experiment.WordCombiner;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.ml.TrainingSetCreator;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.rule.HeuristicRule;

@EnableAsync
@RestController
@RequestMapping("/review")
public class ReviewScoringController {
	private static final Logger LOG = Logger.getLogger(ReviewScoringController.class);
	
	@Autowired
    ReviewScoringService reviewScoringService;

	@RequestMapping(value="/score", produces="application/json; charset=utf8")
	public Map<String, Object> getScore(
			@RequestParam(value="date", required=false) String date,
			@RequestParam(value="contNo", required=false) String contNo,
			@RequestParam(value="mCatID", required=true) String mCatID,

			@RequestParam(value="imgYn", required=true) String imgYn,
			@RequestParam(value="movieYn", required=true) String movieYn,
			@RequestParam(value="mobileYn", required=true) String mobileYn,

			@RequestParam(value="txt", required=true) String txt,
			
			@RequestParam(value="callback", required=true) String callback,
			HttpServletRequest request
			){
		
		
		Map<String, Object> hm = new HashMap();
		
		/*
		 * 비동기 호출.
		 */
		reviewScoringService.getScoreAsync(date, contNo, mCatID, imgYn, movieYn, mobileYn, txt, request, callback);
		
		
		
		hm.put("status", "success");
		
		return hm;
	}
	
	
	@RequestMapping(value="/callbackTest", produces="application/json; charset=utf8")
	public Map<String, Object> getCallbackTest(
				@RequestParam(value="contNo", defaultValue="", required=true) String contNo,
				@RequestParam(value="score", defaultValue="", required=true) String score
			) {
		
		Map<String, Object> hm = new HashMap();
		
		if( score.equals("") )
			hm.put("resultCode", "false");
		else
			hm.put("resultCode", "success");
		
		System.out.println( "contNo = " + contNo );
		System.out.println( "score = " + score );
		
		return hm;
	}
			
}
