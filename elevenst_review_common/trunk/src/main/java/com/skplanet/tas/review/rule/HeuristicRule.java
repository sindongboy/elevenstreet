package com.skplanet.tas.review.rule;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.skplanet.tas.review.util.HDFSLoader;


public class HeuristicRule {
	Set<String> autoHs;

	public HeuristicRule(boolean isHdfs) {
		autoHs = new HashSet();



		InputStream is = HDFSLoader.getInputStream(isHdfs, "fake.txt");


		//		System.out.println( dicUrl );

		//		File file = new File( dicPath );

		try {
//			FileReader fr = new FileReader(file);

//			InputStreamReader isr = new InputStreamReader(dicUrl.openStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String line = null;

			while( (line=br.readLine()) != null ) {
				//				System.out.println(line);

				String review = line.replace(" ", "").trim();
				review = review.replaceAll("[^a-zA-Z가-힣]+", "");	// 특수문자 제거.
//								System.out.println(review);

				autoHs.add(review);

			}


			br.close();
			is.close();
//			fr.close();
			//			isr.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public Boolean isAutoWritten(String txt) {

		//		if(txt.equals("제 마음에 쏙 드네요... 또 구매할께요^^")) {
		//			System.out.println(txt + ", " + txt.replace(" ", "").trim() + ", " + autoHs.contains(txt.replace(" ", "").trim()) + ", ");
		//			System.out.println(autoHs);
		//		}

		String refined = txt.replace(" ", "").trim().replaceAll("[^a-zA-Z가-힣]+", "");

		//		System.out.println(refined);

		// 자동완성문구이면 스팸.
		if( refined.equals("") || autoHs.contains( refined ) )
			return true;
		else
			return false;
	}


	public Boolean isSpam(String txt, StringBuffer morphSb, List<Map> statAl, Map<String, Object> featureHm, Double a) {

		//		if( txt.startsWith("무슈제이 퓨리파잉") ) 
		//			System.out.println(morphSb);

		// 동사가 하나도 없으면 스팸.
		if( morphSb.length() > 0 && !morphSb.toString().contains("/v") ) {
			return true;
		}

		if( isAutoWritten(txt) )
			return true;

		Map<String, Double> avgHm = statAl.get(0);
		Map<String, Double> stdHm = statAl.get(1);

		Boolean isAnomaly = false;

		//		Double a = 2.0d;

		for( String col : avgHm.keySet() ) {

			Double avg = avgHm.get(col);
			Double std = stdHm.get(col);


			Double val = (Double) featureHm.get(col.trim());

			//			if(val == null)
			//				System.out.println(val + col + featureHm);

			// 의미를 지니는 문자가 안나오면...
			//			if( col.trim().equals("txtScore") ) {
			//				if( val.equals(0.0d) )
			//					isAnomaly = true;
			//			}

			if( col.trim().equals("len") 
					|| col.trim().equals("wordCnt") 
					|| col.trim().equals("txtScore") 
					||  col.trim().equals("verbRatio")

					) {

				if( !col.trim().equals("txtScore") ) {	// 의미적인 부분 제외.
					Double t = avg - (a * std);	// Threshold for Anomaly Detection

					if( col.trim().equals("len") ) {
						if( val <= t ) {
							isAnomaly = true;
							break;	
							//								aCnt++;
						}
					} 


					//					if( col.trim().equals("verbRatio") ) {
					////						System.out.println( val + ", " +  t );
					//						if( val <= t ) {
					////						if( val <= 0.1 ) {
					////						if( val <= (avg - std) ) {	// 1시그마로..
					//							isAnomaly = true;
					//							
					////							System.out.println(val);
					////							System.out.println(txt);
					////							System.out.println(morphSb);
					////							System.out.println();
					//							
					//							break;	
					////								aCnt++;
					//						}
					//					}

					//					else if( col.trim().equals("wordCnt") ) {	// 띄어쓰기를 안했으나, 의미있는 리뷰들이 있음.
					//						if( val <= 1.0d ) {
					//							isAnomaly = true;
					//							break;	
					////								aCnt++;
					//						}
					//					}


				}

			} 
			else {
				Double t = avg + (a * std);	// Threshold for Anomaly Detection
				Double t2 = avg + (3.0d * std);	// Threshold for Anomaly Detection

				//				if( val >= t && (
				//						col.trim().equals("perplexity")	// ok
				//						) ) {
				////					System.out.println(val);
				//				}

				//				System.out.println( col + ", " + val);

				if( val >= t2 && (
						col.trim().equals("wordRepeatness")
						)
						) {
					isAnomaly = true;
				}

				if( val >= t && (
						//						col.trim().equals("wordRepeatness")
						col.trim().equals("notKoreanRatio")	// ok

						//						|| col.trim().equals("charRepeatness")	// 약간 정상스러운 것들도 있음...
						//						col.trim().equals("engRatio")	// ok (아래 것으로 대체)

						//						
						////						col.trim().equals("spRatio")	// 조금 위험.. 내용은 괜찮은 경우가 꽤 있음 (가독성은 저하)
						//						
						|| col.trim().equals("avgWordLen")	// 조금 위험.. (모바일서 띄어쓰기 없이 쓴 경우가 있는 듯.. / 가독성은 떨어짐.)
						)
						) {
					//					System.out.println( col + ", " + t + ", " + val );
					isAnomaly = true;
					//					break;
					//					aCnt++;
				}

				/*
				 * 본문의 반 이상이 이상 징후를 보이면.
				 */
				//				if( val >= 0.5 && (
				////						
				//						
				//						col.trim().equals("numRatio")	// 
				////						col.trim().equals("jamoRatio")	// 위험 (글자수를 맞추려고 ㅋ를 남발한 경우가 있음 / 보류) 
				//						)
				//						) {
				////					System.out.println( col + ", " + t + ", " + val );
				//					isAnomaly = true;
				//					break;
				////					aCnt++;
				//				} else if( val >= 0.5 &&
				//						col.trim().equals("wordRepeatness")	// 위험해 보임? (Char에서 잡히니 보류)
				//						) {
				//					isAnomaly = true;
				//					break;
				//					
				//				}

			}


		}

		return isAnomaly;
	}


	//	public Boolean isSpam(String txt, StringBuffer morphSb, List<Map> statAl, Map<String, Object> featureHm, Double a) {
	//
	////		if( txt.startsWith("무슈제이 퓨리파잉") ) 
	////			System.out.println(morphSb);
	//		
	//		// 동사가 하나도 없으면 스팸.
	//		if( morphSb.length() > 0 && !morphSb.toString().contains("/v") ) {
	//			return true;
	//		}
	//		
	//		if( isAutoWritten(txt) )
	//			return true;
	//		
	//		Map<String, Double> avgHm = statAl.get(0);
	//		Map<String, Double> stdHm = statAl.get(1);
	//		
	//		Boolean isAnomaly = false;
	//		
	////		Double a = 2.0d;
	//		
	//		for( String col : avgHm.keySet() ) {
	//			
	//			Double avg = avgHm.get(col);
	//			Double std = stdHm.get(col);
	//
	//
	//			Double val = (Double) featureHm.get(col.trim());
	//			
	////			if(val == null)
	////				System.out.println(val + col + featureHm);
	//
	//			// 의미를 지니는 문자가 안나오면...
	////			if( col.trim().equals("txtScore") ) {
	////				if( val.equals(0.0d) )
	////					isAnomaly = true;
	////			}
	//
	//			if( col.trim().equals("len") || col.trim().equals("wordCnt") || col.trim().equals("txtScore") ||  col.trim().equals("verbRatio") ) {
	//				
	//				if( !col.trim().equals("txtScore") ) {	// 의미적인 부분 제외.
	//					Double t = avg - (a * std);	// Threshold for Anomaly Detection
	//
	//					if( col.trim().equals("len") ) {
	//						if( val <= t ) {
	//							isAnomaly = true;
	//							break;	
	////								aCnt++;
	//						}
	//					} 
	//					
	//					
	////					if( col.trim().equals("verbRatio") ) {
	//////						System.out.println( val + ", " +  t );
	////						if( val <= t ) {
	//////						if( val <= 0.1 ) {
	//////						if( val <= (avg - std) ) {	// 1시그마로..
	////							isAnomaly = true;
	////							
	//////							System.out.println(val);
	//////							System.out.println(txt);
	//////							System.out.println(morphSb);
	//////							System.out.println();
	////							
	////							break;	
	//////								aCnt++;
	////						}
	////					}
	//					
	////					else if( col.trim().equals("wordCnt") ) {	// 띄어쓰기를 안했으나, 의미있는 리뷰들이 있음.
	////						if( val <= 1.0d ) {
	////							isAnomaly = true;
	////							break;	
	//////								aCnt++;
	////						}
	////					}
	//					
	//
	//				}
	//
	//			} 
	//			else {
	//				Double t = avg + (a * std);	// Threshold for Anomaly Detection
	////				Double t2 = avg + (3.0d * std);	// Threshold for Anomaly Detection
	//				
	////				if( val >= t && (
	////						col.trim().equals("perplexity")	// ok
	////						) ) {
	//////					System.out.println(val);
	////				}
	//				
	//				if( val >= t && (
	//						col.trim().equals("charRepeatness")	// ok
	//						|| col.trim().equals("engRatio")	// ok
	//						|| col.trim().equals("notKoreanRatio")
	//						
	////						col.trim().equals("spRatio")	// 조금 위험.. 내용은 괜찮은 경우가 꽤 있음 (가독성은 저하)
	//						
	//						|| col.trim().equals("avgWordLen")	// 조금 위험.. (모바일서 띄어쓰기 없이 쓴 경우가 있는 듯.. / 가독성은 떨어짐.)
	//						)
	//						) {
	////					System.out.println( col + ", " + t + ", " + val );
	//					isAnomaly = true;
	////					break;
	////					aCnt++;
	//				}
	//				
	//				/*
	//				 * 본문의 반 이상이 이상 징후를 보이면.
	//				 */
	//				if( val >= 0.5 && (
	////						
	//						
	//						col.trim().equals("numRatio")	// 
	////						col.trim().equals("jamoRatio")	// 위험 (글자수를 맞추려고 ㅋ를 남발한 경우가 있음 / 보류) 
	//						)
	//						) {
	////					System.out.println( col + ", " + t + ", " + val );
	//					isAnomaly = true;
	//					break;
	////					aCnt++;
	//				} else if( val >= 0.5 &&
	//						col.trim().equals("wordRepeatness")	// 위험해 보임? (Char에서 잡히니 보류)
	//						) {
	//					isAnomaly = true;
	//					break;
	//					
	//				}
	//				
	//			}
	//			
	//			
	//		}
	//		
	//		return isAnomaly;
	//	}

	public Double calcScore(String review, StringBuffer topicSb, List<Map> statAl, Map<String, Object> featureHm, Map<String, Double> weightHm) {
		Double score = 0.0d;
		/*
		 * Score 누적.
		 */

		String testStr = "좋아요좋아요";
		boolean isTest = false;

		if( isTest && review.contains(testStr) ) {
			System.out.println(review);
			System.out.println(statAl.get(0));
			System.out.println(statAl.get(1));
			System.out.println(featureHm);
		}

		String[] topicArr = topicSb.toString().split(" ");

		Set<String> topicHs = new HashSet();
		for(String topic : topicArr) {
			topicHs.add(topic.trim());
		}

		for(String topic : topicHs) {
			if( weightHm.containsKey( topic ) ) {
				double emi = weightHm.get(topic);
				score += emi;
				//										bw.write(metaSb + "\t" + review + "\t" + score + "\t" + attr + "\t" + sent + "\n");

				if( isTest && review.contains(testStr) ) {
					System.out.println(topic + ", " + emi);
				}

				//				System.out.println(topic + ", " + emi);
			}
		}

		if( isTest && review.contains(testStr) ) {
			System.out.println("score = " + score);
			System.out.println();
		}


		/*
		 * penalty
		 */
		score *= 1.0d - (Double) featureHm.get("spRatio");
		//		System.out.println(score);
		score *= 1.0d - (Double) featureHm.get("jamoRatio");
		//		System.out.println(score);
		score *= 1.0d - (Double) featureHm.get("charRepeatness");
		//		System.out.println(score);
		score *= 1.0d - (Double) featureHm.get("wordRepeatness");
		//		System.out.println(score);

		//		score *= (Double) featureHm.get("verbRatio");

		return score;
	}
	
	public static Double calcScore(String topicStr, Map<String, Double> weightHm) {
		Double score = 0.0d;
		/*
		 * Score 누적.
		 */

		String[] topicArr = topicStr.split(" ");

		Set<String> topicHs = new HashSet();
		for(String topic : topicArr) {
			topicHs.add(topic.trim());
		}

		for(String topic : topicHs) {
			if( weightHm.containsKey( topic ) ) {
				double emi = weightHm.get(topic);
				score += emi;
				//										bw.write(metaSb + "\t" + review + "\t" + score + "\t" + attr + "\t" + sent + "\n");

				//				System.out.println(topic + ", " + emi);
			}
		}

		return score;
	}
}
