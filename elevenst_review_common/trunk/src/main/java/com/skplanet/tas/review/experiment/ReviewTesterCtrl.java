package com.skplanet.tas.review.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.rule.HeuristicRule;

public class ReviewTesterCtrl {
	public static void main(String[] args) {

		ReviewRanker2 rr = new ReviewRanker2();
		WordCombiner wc = new WordCombiner();

		HeuristicRule hr = new HeuristicRule(false);
		FeatureExtractor fe = new FeatureExtractor();

		Predictor pred = new Predictor();

		List<Map> statAl = pred.getStat(false);


		//		String filePath = "/Users/skplanet/Documents/2015_11ST/data/160118/data-no-dup-result";
		String filePath = "/Users/skplanet/Documents/2015_11ST/data/160224_beauty/mcat-result";

		File dir = new File(filePath);

		String[] files = dir.list();

		FileReader fr = null;
		BufferedReader br = null;

		FileWriter fw = null;
		BufferedWriter bw = null;

		FileWriter fw2 = null;
		BufferedWriter bw2 = null;

		Map<String, Integer> wordHm = new HashMap();

		Map<String, Integer> biGramHm = new HashMap();
		Map<String, Integer> triGramHm = new HashMap();


		HashSet<String> uniqHs = new HashSet();
		HashSet<String> dupHs = new HashSet();

		ArrayList scoreAl = new ArrayList();

		int cntSum = 0;

		String outputFile = "/Users/skplanet/Documents/2015_11ST/data/160224_beauty/11st/format1.txt"; 
		String outputFile2 = "/Users/skplanet/Documents/2015_11ST/data/160224_beauty/11st/format2.txt";

		try {
			fw = new FileWriter(outputFile);
			bw = new BufferedWriter(fw);

			fw2 = new FileWriter(outputFile2);
			bw2 = new BufferedWriter(fw2);
			
			
			// Write Column
			bw.write("전시카테고리(대/중/소/세)\t상품번호\t옵션번호\t구매후기&상품리뷰\t리뷰Score\t추출 키워드_Attribute\tSentiment\n");
			bw2.write("키워드(Attribute/Sentiment)\tAttribute = Att Sentiment = Senti\t키워드 Score\t전시카테고리(대/중/소/세)\t상품번호\t옵션번호\t구매후기&상품리뷰\t리뷰 Score\n");
			

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for(String file : files) {



			try {
				fr = new FileReader(filePath + "/" + file);

				br = new BufferedReader(fr);


				/*
				 * Preparing weighting
				 */
				String subWeightPath = "/Users/skplanet/Documents/2015_11ST/data/160224_beauty/senti/" + file.replace(".tsv.sim", ".emi.senti");
				Map<String, Double> weightHm = wc.readWordWeight(subWeightPath);

				/*
				 * Max Weight 구하기 (for normalization)
				 */
				double maxW = 0.0d;
				for(String word : weightHm.keySet()) {

					double weight = weightHm.get(word);

					if( weight > maxW )
						maxW = weight;

				}

				/*
				 * 정규화.
				 */
				for(String word : weightHm.keySet()) {

					double weight = weightHm.get(word);
					weight /= maxW;

					weightHm.put(word, weight);

				}

				System.out.println(filePath + "/" + file);

				/*
				 * 2. 리뷰 단위로 Loop
				 */
				String line = null;

				StringBuffer reviewSb = new StringBuffer();
				StringBuffer topicSb = new StringBuffer();
				StringBuffer morphSb = new StringBuffer();

				String data = null;

				int cnt = 0;
				boolean firstLine = true;

				while( (line=br.readLine()) != null ) {

					if(line.startsWith("O")) {	// 원문.
						line = line.replace("O:", "");
						reviewSb.append(line + ". ");
					}

					if(line.startsWith("M")) {	// 원문.
						line = line.replace("M:", "");
						morphSb.append(line + ". ");
					}

					if(line.startsWith("T")) {	// 토픽 분석 결과.
						line = line.replace("T:", "");
						topicSb.append(line + " ");
					}

					if(line.startsWith("I:B") && !firstLine) {

						if(reviewSb.toString().contains("\t")) {
							//						if(reviewSb.toString().split("\t").length > 2) {




							//						if(isAttr) {

							//						if(reviewSb.toString().split("\t").length == 16) {

							//							System.out.println(reviewSb);
							data = reviewSb.toString().replace("수정\t", "");	// 저장.

							//													System.out.println(data);

						} else {	// 실제 처리.


							//							System.out.println(reviewSb);
							//							System.out.println(topicSb);

							/*
							 * 스팸여부 검사 (스팸이면 스킵.)
							 */
							String review = reviewSb.toString();
							String morphResult = morphSb.toString();

							Map<String, Object> featureHm = fe.extract(review, morphResult);


							Boolean isSpam = hr.isSpam(review, new StringBuffer(), statAl, featureHm, 2.0d);

							Double score = 0.0d;

							StringBuffer attr = new StringBuffer();
							StringBuffer sent = new StringBuffer();


							/*
							 * meta 순서 맞추기.
							 */
							StringBuffer metaSb = new StringBuffer();

							String[] metaArr = data.split("\t");

							//								System.out.println(data);
							//								System.out.println(metaArr.length);

							String dispCat = null;

							if(metaArr[2].trim().equals("null") || metaArr[2].trim().equals(""))
								dispCat = metaArr[1];

							else
								dispCat = metaArr[2];

							metaSb.append(dispCat + "\t");

							metaSb.append(metaArr[3] + "\t");
							metaSb.append(metaArr[5]);

							
							if(isSpam) {
								score = -1.0d;
							} else {	// Spam이 아닌 것으로 나오면, emi 값 누적하여 score 구하기.
								//								List subAl = rr.calcScore(topicSb, weightHm, reviewSb, uniqHs, dupHs, scoreAl, morphSb);

								//								uniqHs = (HashSet<String>) subAl.get(0);
								//								dupHs = (HashSet<String>) subAl.get(1);
								//								scoreAl = (ArrayList) subAl.get(2);
								//								
								//								score = (Double) scoreAl.get(scoreAl.size()-1);




								String[] topicArr = topicSb.toString().split(" ");

								boolean isFirst = true;
								for(String topic : topicArr) {
									if( weightHm.containsKey( topic ) ) {
										double emi = weightHm.get(topic);
										score += emi;

										String sortStr = "Att";
										if( topic.startsWith("S") && topic.contains("_") ) {
											sent.append(topic);
											sent.append(" ");

											sortStr = "Senti";
										} else {
											attr.append(topic);
											attr.append(" ");
										}

										if(isFirst) {
//											review = "";
//											metaArr[5] = "";
											bw2.write(topic + "\t" + sortStr + "\t" + emi + "\t" + dispCat + "\t" + metaArr[3]  + "\t" + metaArr[5]
													+ "\t" + review + "\t" + score
													+ "\n");
										} else {
										
											bw2.write(topic + "\t" + sortStr + "\t" + emi + "\t" + dispCat + "\t" + metaArr[3]  + "\t" + ""
													+ "\t" + "" + "\t" + score
													+ "\n");
										}
										
										isFirst = false;

										//										bw.write(metaSb + "\t" + review + "\t" + score + "\t" + attr + "\t" + sent + "\n");

									}
								}
							}


							//							System.out.println(data + "\t" + review + "\t" + score + " || " + attr + " || " + sent);
							bw.write(metaSb + "\t" + review + "\t" + score + "\t" + attr + "\t" + sent + "\n");


							cnt++;
							cntSum++;

							if( (cnt % 10000) == 0 ) {
								System.out.println("lineCnt = " + cnt);
							}
						}



						// 초기화.
						reviewSb = new StringBuffer();
						topicSb = new StringBuffer();

					}

					firstLine = false;


				}

				System.out.println("lineCnt = " + cnt);

				// 초기화.
				reviewSb = new StringBuffer();
				topicSb = new StringBuffer();




				br.close();
				fr.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		try {

			/*
			 * 결과 출력.
			 */


			bw.close();
			fw.close();

			bw2.close();
			fw2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
