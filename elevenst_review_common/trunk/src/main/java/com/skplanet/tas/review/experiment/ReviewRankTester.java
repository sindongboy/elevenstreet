//package com.skplanet.tas.review.experiment;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.lang.reflect.Method;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//
//import com.skplanet.nlp.NLPAPI;
//import com.skplanet.nlp.config.Configuration;
//import com.skplanet.nlp.usermodeling.topical.util.MovieEntityDictionary;
//import com.skplanet.nlp.usermodeling.topical.util.StopWordDictionary;
//import com.skplanet.nlp.usermodeling.topical.util.TermExtractor;
//import com.skplanet.nlp.usermodeling.topical.util.TermExtractor.TextAnalysisInfoForSimilarWord;
//import com.skplanet.tas.review.ml.Predictor;
//import com.skplanet.tas.review.preprocess.FeatureExtractor;
//import com.skplanet.tas.review.rule.HeuristicRule;
//
//public class ReviewRankTester {
//	
//	
//	public static void loadClasses(String path) {
//
//		File f = new File(path);
//		URL u = null;
//		try {
//			u = f.toURL();
//		} catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
//		Class urlClass = URLClassLoader.class;
//		Method method = null;
//		try {
//			method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		method.setAccessible(true);
//		try {
//			method.invoke(urlClassLoader, new Object[]{u});
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	}
//	
//	public static void main(String[] args) {
//		Map<String, Map> weightHm = new HashMap();
//		
//		WordCombiner wc = new WordCombiner();
//
//
//		/*
//		 * Preparing Ranking
//		 */
//		File weightDir = new File(Thread.currentThread().getContextClassLoader().getResource("weight").getPath());
//		File[] wFileList = weightDir.listFiles();	
//
//		for(File subWeightPath : wFileList) {
//
//			Map<String, Double> subWHm = wc.readWordWeight(subWeightPath.toString());
//
//			/*
//			 * Max Weight 구하기 (for normalization)
//			 */
//			Double maxW = 0.0d;
//			for(String word : subWHm.keySet()) {
//
//				double weight = subWHm.get(word);
//
//				if( weight > maxW )
//					maxW = weight;
//
//			}
//
//			/*
//			 * 정규화.
//			 */
//			for(String word : subWHm.keySet()) {
//
//				double weight = subWHm.get(word);
//				weight /= maxW;
//
//				subWHm.put(word, weight);
//			}
//
//			weightHm.put(subWeightPath.getName().replace(".emi", ""), subWHm);
//
//			//				System.out.println(subWeightPath.toString());
//			//				System.out.println(subWeightPath.getName().replace(".emi", ""));
//			//				System.exit(0);
//			//				weightHm.put(key, value)
//		}
//
//
//		/*
//		 * Load NLP Resources...
//		 */
//		String nlpPath = null;
//		String sentSysDicPath = null;
//		String sentDicPath = null;
//
////		Properties prop = new Properties();
//
////		try {
////			prop.load( Thread.currentThread().getContextClassLoader().getResourceAsStream("path.properties") );
////			nlpPath = prop.getProperty("nlpPath");
//			nlpPath = "/Users/skplanet/Documents/workspace/11ST_NLP/";
//			sentSysDicPath = nlpPath + "/sys/"; 
//			sentDicPath = nlpPath + "/sentiments/";
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//
//		loadClasses(nlpPath);
//
//		loadClasses(nlpPath + "/config");
//		loadClasses(nlpPath + "/resource");
//
////		loadClasses(sentSysDicPath);
////		loadClasses(sentDicPath);
//
//
//		String movieDicPath = Thread.currentThread().getContextClassLoader().getResource("MovieEntity.dic").getPath();
//		String stopwordDicPath = Thread.currentThread().getContextClassLoader().getResource("StopWord.dic").getPath();
//
//		MovieEntityDictionary movieDic = new MovieEntityDictionary();
//		try {
//			movieDic.loadDictionary(movieDicPath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		StopWordDictionary stopwordDic = new StopWordDictionary();
//		try {
//			stopwordDic.loadDictionary(stopwordDicPath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		//		nlpapi = new NLPAPI("nlp_api.properties");
//
//
//		
//		String filePath = "/Users/skplanet/Documents/2015_11ST/data/160323_request/deal_evl.txt";
//		String outputPath = "/Users/skplanet/Documents/2015_11ST/data/160323_request/160323_deal_evl_output.txt";
//		Predictor pred = new Predictor();
//		FeatureExtractor fe = new FeatureExtractor();
//		HeuristicRule hr = new HeuristicRule(false);
//		List<Map> statAl = pred.getStat(false);
//		
//		FileInputStream fis = null;
//		InputStreamReader isr = null;
//		BufferedReader br = null;
//		
//		FileWriter fw = null;
//		BufferedWriter bw = null;
//		
//		try {
////			fr = new FileReader(filePath);
//			fis = new FileInputStream(filePath);
//			isr = new InputStreamReader(fis, "ms949");
//			br = new BufferedReader(isr);
//			
//			fw = new FileWriter(outputPath);
//			bw = new BufferedWriter(fw);
//			
//			String line = null;
//			
//			double spamCnt = 0.0d;
//			double hamCnt = 0.0d;
//			
//			while( (line=br.readLine()) != null ) {
//				
//				
//				String[] lineArr = line.split("\t");
//				
//				if(lineArr.length == 0)
//					continue;
//				
//				String txt = lineArr[0];
//				
//				Map<String, Object> featureHm = fe.extract(txt, "");
//
//				Boolean isSpam = hr.isSpam(txt, new StringBuffer(), statAl, featureHm, 2.0d);
//				
////				System.out.println();
//				
////				if(isSpam) {
////					System.out.println(txt + "\t" + isSpam);
////					spamCnt++;
////				} else {
//////					System.out.println(txt + "\t" + isSpam);
////					
////					hamCnt++;
////				}
//				
//				Double score = 0.0d;
//
//				if(isSpam) {
//					score = -1.0d;
//				} else {
//					Map<String, Double> subWHm = weightHm.get("1234");
//
//					
//					StringBuffer str = new StringBuffer();
//
//					TermExtractor te = new TermExtractor();
//					ArrayList<TextAnalysisInfoForSimilarWord> analyzedSentenceList =  te.extractTermForSimilarWord(txt, stopwordDic, nlpapi, sentimentAnalyzer, nlp, false);
//
//
//					ArrayList<String> termAl = new ArrayList();
//
//					for( TextAnalysisInfoForSimilarWord sinfo : analyzedSentenceList){
//
//						for( String topicWord : sinfo.topicWordList){
//							String term = topicWord;
//
//							if(!termAl.contains(term))
//								termAl.add(term);
//
//						}
//
//					}
//
//					for(String term : termAl) {
//						str.append(term);
//						str.append(" ");
//					}
//					
////					System.out.println(txt);
////					System.out.println(str);
//					score = hr.calcScore(txt, str, statAl, featureHm, subWHm);
//			
//				}
//				
//				bw.write(score + "\t" + txt + "\n");
//				
//				if( score > 0.0d ) {
//					hamCnt++;
////					System.out.println(score + "\t" +  txt);
//				} else {
//					spamCnt++;
//				}
//				
//			}
//			
//			
//			System.out.println(spamCnt + ", " + hamCnt);
//			
//			System.out.println(spamCnt / (spamCnt + hamCnt));
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				br.close();
////				fr.close();
//				isr.close();
//				fis.close();
//				
//				bw.close();
//				fw.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		
//		
//	}
//}
