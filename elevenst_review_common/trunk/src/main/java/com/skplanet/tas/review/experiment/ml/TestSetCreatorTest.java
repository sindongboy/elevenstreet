package com.skplanet.tas.review.experiment.ml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.NLPDoc;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.morph.Morph;
import com.skplanet.nlp.morph.Morphs;
import com.skplanet.nlp.usermodeling.topical.util.StopWordDictionary;
import com.skplanet.tas.review.experiment.WordCombiner;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.ml.TrainingSetCreator;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.rule.HeuristicRule;
import com.skplanet.tas.review.util.ResourceLoader;

public class TestSetCreatorTest {
	public static void main(String[] args) {
//		boolean addSemantic = false;
		boolean addSemantic = true;
		
		/*
		 * 0. Prep.
		 */
		Predictor pred = new Predictor();
		FeatureExtractor fe = new FeatureExtractor();
		List<Map> statAl = pred.getStat(false);
		
		/*
		 * 형태소 분석기 준비.
		 */

		//		Map<String, Map> weightHm = null;

		Map<String, double[]> c2vHm = null;
		Map<String, double[]> w2vHm = null;

		StopWordDictionary stopwordDic = null;
		NLPAPI nlpapi = null;

//		Integer w2vN = 500;
				Integer w2vN = 100;

		if( addSemantic ) {
			ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/");
			ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/config");
			ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/resource");
			ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/weight");

			nlpapi = new NLPAPI("nlp_api.properties", Configuration.CLASSPATH_LOAD);

			stopwordDic = new StopWordDictionary();
			WordCombiner wc = new WordCombiner();

			//			weightHm = new HashMap();


			File dicDir = new File(Thread.currentThread().getContextClassLoader().getResource("w2v").getPath());

//			String c2vPath = dicDir.getAbsolutePath() + "/c2v.tsv";
			String c2vPath = dicDir.getAbsolutePath() + "/c2v_" + w2vN + ".tsv";

//			String w2vPath = dicDir.getAbsolutePath() + "/w2v.tsv";
			String w2vPath = dicDir.getAbsolutePath() + "/w2v_" + w2vN + ".tsv";
			
			//			String w2vPath = dicDir.getAbsolutePath() + "/w2v_100.tsv";
			//			String w2vPath = dicDir.getAbsolutePath() + "/w2v_100_all.tsv";




			c2vHm = wc.readSemanticVector(c2vPath);
			w2vHm = wc.readSemanticVector(w2vPath);




			//			File weightDir = new File(Thread.currentThread().getContextClassLoader().getResource("weight").getPath());
			//			File[] wFileList = weightDir.listFiles();	
			//
			//			for(File subWeightPath : wFileList) {
			//
			//				Map<String, Double> subWHm = wc.readWordWeight(subWeightPath.toString());
			//
			//				/*
			//				 * Max Weight 구하기 (for normalization)
			//				 */
			//				Double maxW = 0.0d;
			//				for(String word : subWHm.keySet()) {
			//
			//					double weight = subWHm.get(word);
			//
			//					if( weight > maxW )
			//						maxW = weight;
			//
			//				}
			//
			//				/*
			//				 * 정규화.
			//				 */
			//				for(String word : subWHm.keySet()) {
			//
			//					double weight = subWHm.get(word);
			//					weight /= maxW;
			//
			//					subWHm.put(word, weight);
			//				}
			//
			//				weightHm.put(subWeightPath.getName().replace(".emi", ""), subWHm);
			//
			//				//				System.out.println(subWeightPath.toString());
			//				//				System.out.println(subWeightPath.getName().replace(".emi", ""));
			//				//				System.exit(0);
			//				//				weightHm.put(key, value)
			//			}
		}
		
		
		
		/*
		 * 1. raw 파일 읽어들이기.
		 */
		FileReader fr = null;
		BufferedReader br = null;
		
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
//		String input = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/combined_20160322.tsv";
//		String output = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/test/combined_test_20160322.csv";
		
		String input = "/Users/skplanet/Documents/2015_11ST/data/dic/train.tsv";
		String output = "/Users/skplanet/Documents/2015_11ST/data/dic/test/train_test.csv";
		
		try {
			
			fr = new FileReader(input);
			br = new BufferedReader(fr);
			
			fw = new FileWriter(output);
			bw = new BufferedWriter(fw);

			
			/*
			 * Write Columns...
			 */
			TrainingSetCreator tsc = new TrainingSetCreator();
			
			String colStr = tsc.getColumnRow(fe, addSemantic);
			
			bw.write(colStr.substring(0, colStr.length()-1) + "\n");
//			bw.write("class\n");
			
			br.readLine();
			
			String line = null;
			
			while( (line=br.readLine()) != null ) {
			
				
				
				String[] lineArr = line.split("\t");
				
//				System.out.println(line);
//				
				
				String reviewContNo = lineArr[0];

				String cat2 = lineArr[3];

				String imgYn = lineArr[9];
				String movieYn = lineArr[10];
				String mobileYn = lineArr[11];

				if(mobileYn.trim().equals(""))
					mobileYn = "NA";

//				Integer likeCnt = Integer.parseInt( lineArr[12] );
//				Integer dislikeCnt = Integer.parseInt( lineArr[13] );
//				Integer likeScore = Integer.parseInt( lineArr[14] );


				


				String cont = "";	// 리뷰 내용.

				String aprvImgYn = lineArr[17];

				//				if( lineArr.length == 9 )
				//				if( lineArr.length == 10 )
				cont = lineArr[15];
				

//				if( cont.trim().equals("null") || cont.trim().equals("") ) {	// cont가 없으면 (작성이 안된 것이 아니라, 적재가 안된 에러)
//					//					System.out.println(line);
//					continue;
//				}
				

//				if( cont.trim().equals("null") ) {	// cont가 없으면 (작성이 안된 것이 아니라, 적재가 안된 에러)
//////					System.out.println(line);
//					continue;
//				}
				
				/*
				 * Spam Filtering.
				 */
//				Boolean isSpam = hr.isSpam(cont, new StringBuffer(), statAl, featureHm, 2.0d);
//				Boolean isSpam = hr.isSpam(cont, new StringBuffer(), statAl, featureHm, 1.0d);
				
				
				
				/*
				 * 2. Feature Extraction
				 */
				Map<String, Object> featureHm = fe.extract(cont, "");
				
				

//				/*
//				 * Add Semantic Features
//				 */
//				Set<String> morphHs = GoodWordTest.extractTerm(cont, nlpapi);
//				
//				Double idfSum = 0.0d;
//				for(String morph : morphHs) {
//					Double idf = idfHm.get(morph);
//					
//					idfSum += idf;
//				}
//				
//				featureHm.put("idfSum", idfSum);
				
				
				
				/*
				 * Add Semantic Features
				 */

				if( addSemantic ) {
					//					Map<String, Double> subWHm = weightHm.get(cat2);
					//					double[] c2v = c2vHm.get(cat2);

					//					if( c2v == null ) {
					//									System.out.print(cat2 + " || ");
					//									System.out.println(c2v);
					//									
					//						
					//					}
					//				
					//					if(subWHm == null)
					//						subWHm = new HashMap();

					//					if( c2v != null ) {

					List<NLPDoc> nlpResults = null;
					nlpResults = nlpapi.doAnalyze(cont);

					double[] r2v = new double[w2vN];

					int cnt = 0;
					for( NLPDoc nlpdoc : nlpResults ){

						Morphs morphs = nlpdoc.getMorphs();
						//loop for topicword
						for(int j = 0; j < morphs.getCount(); j++)
						{
							Morph morph = morphs.getMorph(j);

							//							 String w2vStr = subWHm.get( morph.toString() );
							double[] w2v = w2vHm.get( morph.toString() );

							if(w2v != null) {
								for(int k=0; k<r2v.length; k++) {
									r2v[k] += w2v[k]; 
								}
								cnt ++;
							}
						}
					}

					// avg.
					for(int k=0; k<r2v.length; k++) {
						r2v[k] /= cnt; 

						featureHm.put("r2v_" + k, r2v[k]);	
					}
					
					
					/*
					 * C2V Feature 추가. (카테고리 의미)
					 */
					double[] c2v = c2vHm.get(cat2);
					
					if(c2v == null) {
						for(int k=0; k<w2vN; k++) {
							featureHm.put("c2v_" + k, 0.0d);	
						}
					} else {
						for(int k=0; k<c2v.length; k++) { 
							featureHm.put("c2v_" + k, c2v[k]);	
						}
					}
					
					
					//						double semanticScore = CosineSimilarity.cosineSimilarity(c2v, r2v);

					//						featureHm.put("w2vSimScore", semanticScore);
					//					} else {	// 카테고리 벡터가 없는 경우에는 추가 점수 없이 0으로 셋팅.
					//						featureHm.put("w2vSimScore", 0.0d);
					//						
					//						// avg.
					//						for(int k=0; k<500; k++) {
					//							featureHm.put("r2v_" + k, 0.0d);	
					//						}
					//					}
					//					
					//					String topicSb = TopicKeywordExtractor.keywords(cont, stopwordDic, nlpapi);
					//					Double semanticScore = HeuristicRule.calcScore(topicSb, subWHm);
					//					
					////					Double semanticScore = ;
					////					
					//					featureHm.put("emiScore", semanticScore);
				}
				
				String[] subLineArr = new String[5];
				subLineArr[1] = imgYn;
				subLineArr[2] = movieYn;
				subLineArr[3] = mobileYn;
				subLineArr[4] = aprvImgYn;
				StringBuffer instSb = tsc.getInstance(subLineArr, featureHm, cont);
				
				// test code
//				System.out.println(instSb);
				
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
