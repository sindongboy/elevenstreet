package com.skplanet.tas.review.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.skplanet.tas.review.preprocess.FeatureExtractor;

public class ReviewRanker2 {

	public List getDups(String subReviewPath, Map<String, Double> weightHm) {
		HashSet<String> uniqHs = new HashSet();
		HashSet<String> dupHs = new HashSet();

		ArrayList scoreAl = new ArrayList();


		FileReader fr = null;
		BufferedReader br = null;


		try {
			fr = new FileReader(subReviewPath);
			br = new BufferedReader(fr);

			/*
			 * 2. 리뷰 단위로 Loop
			 */
			String line = null;

			StringBuffer reviewSb = new StringBuffer();
			StringBuffer morphSb = new StringBuffer();
			StringBuffer topicSb = new StringBuffer();

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

					//					Double score = score(topicSb, weightHm);

					/*
					 * returnAl.add(uniqHs);
		returnAl.add(dupHs);
		returnAl.add(scoreAl);
					 */


					if(reviewSb.toString().contains("\t")) {
						//						if(reviewSb.toString().split("\t").length > 2) {




						//						if(isAttr) {

						//						if(reviewSb.toString().split("\t").length == 16) {

						//							System.out.println(reviewSb);
						data = reviewSb.toString().replace("수정\t", "");	// 저장.
						
//						System.out.println(data);

					} else {	// 실제 처리.

						List subAl = calcScore(topicSb, weightHm, reviewSb, uniqHs, dupHs, scoreAl, morphSb);

						uniqHs = (HashSet<String>) subAl.get(0);
						dupHs = (HashSet<String>) subAl.get(1);
						scoreAl = (ArrayList) subAl.get(2);


//						System.out.println(reviewSb);

						cnt++;

						if( (cnt % 10000) == 0 ) {
							System.out.println("lineCnt = " + cnt);
						}

					}


					// 초기화.
					reviewSb = new StringBuffer();
					morphSb = new StringBuffer();
					topicSb = new StringBuffer();

					data = null;


				}

				firstLine = false;


			}

			System.out.println("lineCnt = " + cnt);


			// 마지막 리뷰 처리 
			List subAl = calcScore(topicSb, weightHm, reviewSb, uniqHs, dupHs, scoreAl, morphSb);

			uniqHs = (HashSet<String>) subAl.get(0);
			dupHs = (HashSet<String>) subAl.get(1);
			scoreAl = (ArrayList) subAl.get(2);

			reviewSb = new StringBuffer();
			morphSb = new StringBuffer();
			topicSb = new StringBuffer();

			br.close();
			fr.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("uniqHs = " + uniqHs.size());
		System.out.println("dupHs = " + dupHs.size());

		//		return dupHs;

		List returnAl = new ArrayList();
		returnAl.add(dupHs);
		returnAl.add(scoreAl);

		return returnAl;
	}

	public List calcScore(StringBuffer topicSb, Map<String, Double> weightHm, StringBuffer reviewSb, Set uniqHs, Set dupHs, List scoreAl, StringBuffer morphSb) {


		//		System.out.println("maxW = " + maxW);
		//		System.exit(0);

		/*
		 * 3. Scoring (EMI 누적) 및 출력.
		 */
		String topicStr = topicSb.toString();
		String[] topicArr = topicStr.split(" ");

		Double score = 0.0d;

		HashSet<String> uniqTopicHs = new HashSet();

		HashSet<String> uniqSelHs = new HashSet();

		//		int wordCnt = 0;

		//					Double topicWordLen = 0.0d;

		//		Double std = 0.0d;

		Set<String> topicHs = new HashSet();
		//		for(String topic : topicArr) {
		//			topicHs.add(topic);
		//		}
		String cnoun = null;
		for(String topic : topicArr) {

			//			System.out.println("[before] " + topic);

			if(topic.startsWith("cnoun_")) {
				cnoun = topic.replace("cnoun_", "");
			} else if(cnoun != null) {
				if(cnoun.startsWith(topic)) {
					continue;
				} else if(cnoun.endsWith(topic)) {
					cnoun = null;
					continue;
				}
			}

			//			System.out.println("\t[after] " + topic);

			topicHs.add(topic);
		}


		//		for(String topic : topicArr) {
		for(String topic : topicHs) {

			if( weightHm.containsKey(topic) ) {	// Weighting 단어이면.
				double weight = weightHm.get(topic);

				//				System.out.println("before = " + weight);
				//				weight /= maxW;	// Normalize.
				//				System.out.println(weight);
				//							if( weight < 0.001)
				//								continue;

				score += weight;
				//				wordCnt++;
				uniqSelHs.add(topic);
			}

			//						topicWordLen += topic.length();

			uniqTopicHs.add(topic);
		}


//		Double repeatPenalty = 0.0d;
//		// 패널티 부여 (반복 어휘에 대하여)
//		if( topicArr.length > 0) {
//
//
//			/*
//			 * 의미 있는 단어가 몇 개나 나왔는지 카운트.
//			 */
//			//			score *= new Double(wordCnt) / new Double(topicArr.length);
//			score *= new Double(uniqSelHs.size()) / new Double(uniqHs.size());
//
//			// 평균 취하기 (테스트)
//			//			score /= new Double(wordCnt);
//
//			// 표준편차 구하기.
//			//			Double std = 0.0d;
//			//			
//			//			for(String topic : topicArr) {
//			//				
//			//				if(weightHm.get(topic) == null)
//			//					continue;
//			//				
//			////				try {
//			////					System.out.println( topic );
//			////					System.out.println( weightHm.get(topic) );
//			////					System.out.println( Math.pow( ( weightHm.get(topic) - score ), 2) );
//			////				} catch(Exception e) {
//			////					e.printStackTrace();
//			////					System.exit(0);
//			////				}
//			//				std += Math.pow( ( weightHm.get(topic) - score ), 2);
//			//			}
//			//			
//			//			std /= new Double( wordCnt );
//			//			std = Math.sqrt( std );
//			//			
//			//			if( std.equals(0.0d) ) {
//			////			if( std > 0.0d ) {
//			//				System.out.println(std + ", " + score + ", " + reviewSb + " || " + morphSb);
//			//			}
//			//			
//			//			if( std.equals(0.0d) ) {	// 중요 단어가 한 단어 밖에 없는 경우? (표준 편차 안구해도 알 수 있지 않나?)
//			//			if( wordCnt <= 1 ) {
//			if( uniqSelHs.size() <= 1 ) {
//				//				System.out.println( score + ", " + reviewSb + " || " + morphSb);
//				score = 0.0d;
//			}
//
//			//			if(reviewSb.toString().contains("대만족이용^^")) {
//			//				System.out.println( score + ", " + reviewSb + " || " + morphSb + " || " + topicSb);
//			//				System.out.println(topicHs);
//			//				System.exit(0);
//			//			}
//
//			//			score *= (1/std);	// 표준편차가 적을 수록 좋음.
//
//			repeatPenalty = new Double(uniqTopicHs.size()) / new Double(topicArr.length);
//			//						Double repeatPenalty = 1 / (new Double(topicArr.length) - new Double(uniqTopicHs.size()));	// 너무 강함..
//			score *= repeatPenalty;
//
//			//			score += repeatPenalty;
//
//
//			//						System.out.println(reviewSb);
//
//			//						if( morphSb.toString().contains("/njm") || morphSb.toString().contains("/sw") )
//
//
//
//			//						System.exit(0);
//			//						if( repeatPenalty < 0.5)
//			//						System.out.println(repeatPenalty + ", " + reviewSb);
//
//
//
//		}
//
//		/*
//		 * 패널티 뷰여 (자모가 많이 등장할 경우)
//		 */
//		//		if( morphSb.toString().contains("/njm")  ) {
//		//			String[] njmArr = morphSb.toString().replace(".", "").split("/njm");
//		//			String[] mArr = morphSb.toString().split(" ");
//		//
//		//			int njmCnt = njmArr.length;	// 자모로만 된 형태소 개수.
//		//			int mCnt = mArr.length;	// 전체 형태소 개수.
//
//		int njmCnt = count(morphSb.toString(), "/njm");	// 자모.
//		int spCnt = count(morphSb.toString(), "/sw");	// 특수 문자.
//		int mCnt = count(morphSb.toString(), " ");
//		int vCnt = count(morphSb.toString(), "/v");	// 동사 개수.
//		vCnt += count(morphSb.toString(), "/x");	// 동사 개수.
//		//			if( mArr.length > 10 )
//
//		Double njmScore = (new Double(njmCnt) + new Double(spCnt)) / new Double(mCnt);
//		Double vScore = new Double(vCnt) / new Double(mCnt);	// 광고성 글들 걸러내기 (상품 홍보 문구..)
//
//
//
//		//			if( njmScore > 0.5 && score > 0.3) {
//		////				System.out.println(score + " || " + morphSb);
//		//				System.out.println(njmCnt + ", " + spCnt +  ", " + mCnt + ", " + njmScore + " || " + score + ", " + (score * (1-njmScore)) + " || " + reviewSb + " || " + morphSb);
//		//			}
//
//		score *= (1.0d - njmScore);
//		score *= vScore;
//
//
//		//			score += (1.0d - njmScore);
//
//
//		//			if(vScore.equals(0.0d)) {
//
//
//		//			}
//
//
//
//		//		}
//
//		/*
//		 * 가산점 부여 (Sentiment가 많이 나왔으면.
//		 */
//
//		//			int sentCnt = count(topicSb.toString(), " S");
//		//			
//		////			System.out.println(sentCnt + " || " + score  + " || " + reviewSb + " || " + topicSb);
//		//			
//		////			score *= (new Double(sentCnt) / new Double(topicArr.length));	// 강하게.
//		//			score += (new Double(sentCnt) / new Double(topicArr.length));	// 약하게 가산점 부여.
//
//
//		//		score /= 4;	// 4가지 척도를 합쳤으므로.

		int reviewLen = reviewSb.length();

		String str = reviewLen + "_" + score;

		if( !uniqHs.contains(str) )	// 한 번도 나온적 없다면.
			uniqHs.add(str);
		else {	// 한 번 이상 나왔다면.
			dupHs.add(str);
			//						System.out.println(reviewSb);
		}

		/*
		 *  Score 저장
		 */


		//		if(reviewSb.toString().contains("쌍용자동차")) {
		//			System.out.println("vScore = " + vScore);
		//			System.out.println("njmScore = " + njmScore);
		////			System.out.println("critical = " + new Double(wordCnt) / new Double(topicArr.length));
		//			System.out.println("critical = " + new Double(uniqSelHs.size()) / new Double(uniqHs.size()));
		//			System.out.println("repeat = " + repeatPenalty);
		//			System.out.println("score = " + score);
		//			System.out.println(morphSb);
		//			System.out.println(reviewSb);
		//			System.exit(0);
		//		}

		scoreAl.add(score);


		List returnAl = new ArrayList();

		returnAl.add(uniqHs);
		returnAl.add(dupHs);
		returnAl.add(scoreAl);


		return returnAl;
	}

	public int count(String str, String findStr) {
		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){

			lastIndex = str.indexOf(findStr,lastIndex);

			if(lastIndex != -1){
				count ++;
				//		    	count += findStr.length();	// char 수를 더하기.
				lastIndex += findStr.length();
			}
		}

		return count;
	}


	public void score(String weightPath, String reviewPath, String outputPath, int nCats, String type) {

		FeatureExtractor fe = new FeatureExtractor();
		
		WordCombiner wc = new WordCombiner();

		DecimalFormat df = new DecimalFormat("0.########");

		for(int i=1; i<=nCats; i++) {
			String subWeightPath = weightPath + "/" + i + "." + type;	// type = {emi, tfidf}
			//			String subReviewPath = reviewPath + "/" + i + ".tsv.sim";

			String idx = "0";


			//			if(file.contains("skintoner")) {
			//				idx = "1";
			//			} else if(file.contains("sunblock")) {
			//				idx = "2";
			//			} else if(file.contains("sunbalm")) {
			//				idx = "3";
			//			} else if(file.contains("bbcc")) {
			//				idx = "4";
			//			} else if(file.contains("foundation")) {
			//				idx = "5";
			//			}

			String cat = "";
			switch(i) {
			case 1 : cat = "skintoner"; break;
			case 2 : cat = "sunblock"; break;
			case 3 : cat = "sunbalm"; break;
			case 4 : cat = "bbcc"; break;
			case 5 : cat = "foundation"; break;
			}

			String subReviewPath = reviewPath + "/" + cat + ".scat.sim";

			/*
			 * 1. EMI 로딩.
			 */
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


			/* 
			 * 중복성 리뷰 추정 목록 추출.
			 */
			//			Set<String> dupHs = getDups(subReviewPath, weightHm);
			List scoreDupAl = getDups(subReviewPath, weightHm);

			Set<String> dupHs = (Set<String>) scoreDupAl.get(0); 
			ArrayList scoreAl = (ArrayList) scoreDupAl.get(1);


			FileReader fr = null;
			BufferedReader br = null;

			FileWriter fw = null;
			BufferedWriter bw = null;
			
			FileWriter fw2 = null;
			BufferedWriter bw2 = null;
			
			FileWriter fw3 = null;
			BufferedWriter bw3 = null;
			
			FileWriter fw4 = null;
			BufferedWriter bw4 = null;

			try {
				fr = new FileReader(subReviewPath);
				br = new BufferedReader(fr);

				fw = new FileWriter(outputPath + "/" + i + "." + type + ".score");
				bw = new BufferedWriter(fw);
				
				fw2 = new FileWriter(outputPath + "/" + i + ".csv");
				bw2 = new BufferedWriter(fw2);

				fw3 = new FileWriter(outputPath + "/test/" + i + ".csv");
				bw3 = new BufferedWriter(fw3);
				
				fw4 = new FileWriter(outputPath + "/test-txt/" + i + ".csv");
				bw4 = new BufferedWriter(fw4);
				
				Set<String> uniqHs = new HashSet();

				/*
				 * 2. 리뷰 단위로 Loop
				 */
				String line = null;

				StringBuffer reviewSb = new StringBuffer();
				StringBuffer morphSb = new StringBuffer();
				StringBuffer topicSb = new StringBuffer();

				String data = null;
				
				int cnt = 0;
				boolean firstLine = true;
				
				
				/*
				 * 0: PID
				 * **1: skinType
				 * **2: ageRank
				 * **3: reviewType
				 * **4: writeType
				 * **5: imgYn
				 * 6: ordNo
				 * 7: inqQty
				 * 8: ctgr2
				 * 9: ctgr1
				 * **10: buyGrd
				 * 11: userId
				 * **12: movieYn
				 * 13: ordPrdSeq
				 * **14: mobileYn
				 * 15:slctPrdOptNm
				 */
//				bw2.write("f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18\n");
//				bw2.write("skinType, ageRank, reviewType, writeType, imgYn, buyGrd, movieYn, mobileYn"
				
				String columnStr = "ageRank, reviewType, writeType, imgYn, buyGrd, movieYn, mobileYn"
						+ ", txtScore"
						+ ", len, wordCnt, avgWordLen, spRatio, jamoRatio, charRepeatness, wordRepeatness"
						+ ", engRatio, numRatio, verbRatio";
				
				bw2.write(columnStr + ", class\n");
				
				bw3.write(columnStr + "\n");
				
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
							
//							System.out.println(data);

						} else {	// 실제 처리.
						
							uniqHs = writeScore(scoreAl, cnt, reviewSb, dupHs, bw, df, uniqHs);
							
							Double score = (Double) scoreAl.get(cnt);
							
							if(score.isInfinite() || score.isNaN())
								score = 0.0d;
							
							
							/*
							 * 조회수를 이용한 태깅.
							 */
							String[] dataArr = null;
							
							StringBuffer structFeatures = new StringBuffer();
							
							try {
								dataArr = data.split("\t", 16);
							}catch(Exception e) {
								e.printStackTrace();
								System.err.println(data);
							}
							/*
							 * 0: PID
							 * 1: skinType
							 * **2: ageRank
							 * **3: reviewType
							 * **4: writeType
							 * **5: imgYn
							 * 6: ordNo
							 * 7: inqQty
							 * 8: ctgr2
							 * 9: ctgr1
							 * **10: buyGrd
							 * 11: userId
							 * **12: movieYn
							 * 13: ordPrdSeq
							 * **14: mobileYn
							 * 15:slctPrdOptNm
							 */
							for(int k=0; k<dataArr.length; k++) {
								String feature = dataArr[k];
								
								if(k != 0
										&& k != 1
										&& k != 6
										&& k != 7
										&& k != 8
										&& k != 9
										&& k != 11
										&& k != 13
										&& k != 15
										) {
									structFeatures.append(feature);
									structFeatures.append(",");
								}
							}
							
							
							
							/**
							 * Text Features (without POS Tagger)
							 */
							String review = reviewSb.toString();
							String morphResult = morphSb.toString();
							
//							System.out.println(review);
//							System.out.println(morphResult);
							
							Map<String, Object> featureHm = fe.extract(review, morphResult );
							
							// 길이
							Double len = (Double) featureHm.get("len");
							
							// 어휘수
							Double wordCnt = (Double) featureHm.get("wordCnt");
							
							// 평균 어휘 길이
							Double avgWordLen = (Double) featureHm.get("avgWordLen");
							
							/*
							 * 공백 제거한 상태에서의 비율.
							 */
							// 특수문자 비율
							// 언어파괴정도 (자,모만 존재하는 문자 비율)
							Double spRatio = (Double) featureHm.get("spRatio");
							Double jamoRatio = (Double) featureHm.get("jamoRatio");
							
							
							// 문자 반복성 
							
							Double charRepeatness = (Double) featureHm.get("charRepeatness"); 
							
							// 어휘 다양성
							Double wordRepeatness = (Double) featureHm.get("wordRepeatness");
							
							/////////////
							
							// 영어 포함 비율.
							Double engRatio = (Double) featureHm.get("engRatio");
							
							// 숫자 포함 비율.
							Double numRatio = (Double) featureHm.get("numRatio");
							
							// Language Model
//							Double perplexity = (Double) featureHm.get("perplexity");
							
							// 동사 비율.
							Double verbRatio = (Double) featureHm.get("verbRatio");
							
							
							String inqQty = dataArr[7];
							
							Integer qty = 0;
							
							try {
								qty = Integer.parseInt(inqQty);
							} catch(Exception e) {
								e.printStackTrace();
							}
							
							String spamYn = "NA";
							
							if(qty > 100) {
								spamYn = "HAM";
//								System.out.println(reviewSb);
							}
							
							if(qty.equals(0)) {
//							if(qty < 20) {
								spamYn = "SPAM";
//								System.out.println(reviewSb);
							}
							
							
							/*
							 * Log 정규화.
							 */
							
//							len = Math.log(len);
//							wordCnt = Math.log(wordCnt);
//							avgWordLen = Math.log(avgWordLen);
//							spRatio = Math.log(spRatio);
							
							String featureVal = len + "," + wordCnt + "," + avgWordLen + "," +  spRatio + "," + jamoRatio + "," + charRepeatness + "," + wordRepeatness + "," + engRatio + "," + numRatio + "," + verbRatio;
							
							
							if( !spamYn.equals("NA") )
								bw2.write(structFeatures.toString() 
										+ score
										+ "," 
										+ featureVal										
										+ "," + spamYn + "\n");
								
							else {
								bw3.write(structFeatures.toString() 
										+ score
										+ "," 
										+ featureVal 						
										+ "\n");
								
								bw4.write(qty + ", " + dataArr[5]  + ", " + review + "\n");
							}
							cnt++;

							if( (cnt % 10000) == 0 ) {
								System.out.println("lineCnt = " + cnt);
							}
						}
						// 초기화.
						reviewSb = new StringBuffer();
						topicSb = new StringBuffer();
						morphSb = new StringBuffer();

					}

					firstLine = false;


				}

				System.out.println("lineCnt = " + cnt);


				uniqHs = writeScore(scoreAl, cnt, reviewSb, dupHs, bw, df, uniqHs);

				// 초기화.
				reviewSb = new StringBuffer();
				topicSb = new StringBuffer();
				morphSb = new StringBuffer();


				br.close();
				fr.close();

				bw.close();
				fw.close();
				
				bw2.close();
				fw2.close();
				
				bw3.close();
				fw3.close();
				
				bw4.close();
				fw4.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}



		}

	}

	private Set<String> writeScore(List scoreAl, int cnt, StringBuffer reviewSb, Set<String> dupHs, BufferedWriter bw, DecimalFormat df, Set<String> uniqHs) {
		// 마지막 리뷰 처리
		Double score = (Double) scoreAl.get(cnt);

		/*
		 * 중복 문장의 score에 -를 붙이기.
		 */
		String originReview = reviewSb.toString();

		int reviewLen = reviewSb.length();
		String str = reviewLen + "_" + score;

		try {

			//			if( dupHs.contains(str) )
			//				bw.write(df.format(-score) + "\t" + originReview + "\n");
			//			else
			//				bw.write(df.format(score) + "\t" + originReview + "\n");


			if( !uniqHs.contains(str) ) {	// 중복되는 것은 한 번만 출력하도록 하기.

				if( score.isInfinite() || score.isNaN() )
					score = 0.0d;

				bw.write(df.format(score) + "\t" + originReview + "\n");

				uniqHs.add(str);
			}



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return uniqHs;

	}

}
