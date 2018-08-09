package com.skplanet.tas.review.experiment.ml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.usermodeling.topical.TopicKeywordExtractor;
import com.skplanet.nlp.usermodeling.topical.util.StopWordDictionary;
import com.skplanet.tas.review.experiment.WordCombiner;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.ml.TrainingSetCreator;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.rule.HeuristicRule;
import com.skplanet.tas.review.util.ResourceLoader;

public class CatTestSetCreatorTest {
	public static void main(String[] args) {
		
//		boolean addSemantic = true;
		boolean addSemantic = false;
		
		/*
		 * 0. Prep.
		 */
		Predictor pred = new Predictor();
		FeatureExtractor fe = new FeatureExtractor();
		List<Map> statAl = pred.getStat(false);
		TrainingSetCreator tsc = new TrainingSetCreator();
		
		/*
		 * 형태소 분석기 준비.
		 */
		Map<String, Map> weightHm = null;
		StopWordDictionary stopwordDic = null;
		NLPAPI nlpapi = null;
		
		if( addSemantic ) {
			ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/");
			ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/config");
			ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/resource");
			ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/weight");

			nlpapi = new NLPAPI("nlp_api.properties", Configuration.CLASSPATH_LOAD);

			stopwordDic = new StopWordDictionary();
			WordCombiner wc = new WordCombiner();

			weightHm = new HashMap();

			File weightDir = new File(Thread.currentThread().getContextClassLoader().getResource("weight").getPath());
			File[] wFileList = weightDir.listFiles();	

			for(File subWeightPath : wFileList) {

				Map<String, Double> subWHm = wc.readWordWeight(subWeightPath.toString());

				/*
				 * Max Weight 구하기 (for normalization)
				 */
				Double maxW = 0.0d;
				for(String word : subWHm.keySet()) {

					double weight = subWHm.get(word);

					if( weight > maxW )
						maxW = weight;

				}

				/*
				 * 정규화.
				 */
				for(String word : subWHm.keySet()) {

					double weight = subWHm.get(word);
					weight /= maxW;

					subWHm.put(word, weight);
				}

				weightHm.put(subWeightPath.getName().replace(".emi", ""), subWHm);

				//				System.out.println(subWeightPath.toString());
				//				System.out.println(subWeightPath.getName().replace(".emi", ""));
				//				System.exit(0);
				//				weightHm.put(key, value)
			}
		}


		////////////

		
		
		
		
		/*
		 * 1. raw 파일 읽어들이기.
		 */
		FileReader fr = null;
		BufferedReader br = null;
		
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
//		String input = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/combined_20160322.tsv";
//		String output = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/test/combined_test_20160322.csv";
		
//		String input = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/review_combined_beauty_20160323.tsv";
		String input = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/review_combined_beauty_20160407.tsv";
		String output = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/test/review_combined_test_beauty_20160407.csv";
		
		try {
			
			fr = new FileReader(input);
			br = new BufferedReader(fr);
			
			fw = new FileWriter(output);
			bw = new BufferedWriter(fw);

			
			/*
			 * Write Columns...
			 */
			String colStr = tsc.getColumnRow(fe, addSemantic);
			
			bw.write(colStr.substring(0, colStr.length()-1) + "\n");
//			StringBuffer colSb = 
//			
//			bw.write(colSb.toString().substring(0, colSb.length()-1) + "\n");
//			bw.write("class\n");
			
			br.readLine();
			
			String line = null;
			
			while( (line=br.readLine()) != null ) {
			
				
				
				String[] lineArr = line.split("\t");
				
//				System.out.println(line);
//				
				int idx = lineArr.length - 1;
				
				
//				String reviewContNo = lineArr[idx-5];
//				String imgYn = lineArr[idx-4];
//				String movieYn = lineArr[idx-3];
//				String mobileYn = lineArr[idx-2];
				
				String cat1 = lineArr[2];
				String cat2 = lineArr[3];
				String cat3 = lineArr[4];
				String cat4 = lineArr[5];
				
				String reviewContNo = lineArr[8];
				String imgYn = lineArr[9];
				String movieYn = lineArr[10];
				String mobileYn = lineArr[11];
				
				if(mobileYn.trim().equals(""))
					mobileYn = "NA";
				
				String cont = "";	// 리뷰 내용.

				if( lineArr.length >= 13 )
					cont = lineArr[12];
				
//				System.out.println(cont);
				
//				String reviewContNo = lineArr[4];
//				String imgYn = lineArr[5];
//				String movieYn = lineArr[6];
//				String mobileYn = lineArr[7];
//				
//				if(mobileYn.trim().equals(""))
//					mobileYn = "NA";
//				
//				String cont = "";	// 리뷰 내용.
//
//				if( lineArr.length >= 9 )
//					cont = lineArr[8];
				

//				if( cont.trim().equals("null") ) {	// cont가 없으면 (작성이 안된 것이 아니라, 적재가 안된 에러)
//////					System.out.println(line);
//					continue;
//				}
				
				/*
				 * Spam Filtering.
				 */
//				Boolean isSpam = hr.isSpam(cont, new StringBuffer(), statAl, featureHm, 2.0d);
//				Boolean isSpam = hr.isSpam(cont, new StringBuffer(), statAl, featureHm, 1.0d);
				
				
//				StringBuffer instSb = new StringBuffer();
////				instSb.append(reviewContNo + ",");
//				instSb.append(imgYn + ",");
//				instSb.append(movieYn + ",");
//				instSb.append(mobileYn + ",");
//				
				
				/*
				 * 2. Feature Extraction
				 */
				Map<String, Object> featureHm = fe.extract(cont, "");



				/*
				 * Add Semantic Features
				 */
				if( addSemantic ) {
					Map<String, Double> subWHm = weightHm.get(cat2);

					//				System.out.print(cat2 + " || ");
					//				System.out.println(subWHm.size());
					//				
					if(subWHm == null)
						subWHm = new HashMap();

					String topicSb = TopicKeywordExtractor.keywords(cont, stopwordDic, nlpapi);
					Double semanticScore = HeuristicRule.calcScore(topicSb, subWHm);
					featureHm.put("semanticScore", semanticScore);
				}
//				Set<String> morphHs = GoodWordTest.extractTerm(cont, nlpapi);
//				
//				Double idfSum = 0.0d;
//				for(String morph : morphHs) {
//					Double idf = idfHm.get(morph);
//					
//					if( idf != null)
//						idfSum += idf;
//				}
//				
//				if( idfSum < 0 ) {
//					idfSum = Math.log( (idfSum * -1.0d) + 1.0d);
//					idfSum *= -1.0d;
//				} else {
//					idfSum = Math.log( idfSum + 1.0d);
//				}
//				
//				featureHm.put("idfSum", idfSum );
				

				String[] subLineArr = new String[4];
				subLineArr[1] = imgYn;
				subLineArr[2] = movieYn;
				subLineArr[3] = mobileYn;
				StringBuffer instSb = tsc.getInstance(subLineArr, featureHm, cont);
				
				
				bw.write(instSb.toString().substring(0, instSb.length()-1) + "\n");

			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			
			try {
				br.close();
				fr.close();
				
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
