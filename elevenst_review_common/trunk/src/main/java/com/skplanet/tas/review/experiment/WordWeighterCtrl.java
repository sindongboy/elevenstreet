package com.skplanet.tas.review.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.StringTokenizer;

public class WordWeighterCtrl {
	public static void main(String[] args) {


		/*
		 * 1. Product 별 Max 조회수를 찾아서 정규화
		 * 2. 정규화된 조회수를 이용하여 단어 Weight 구하기
		 * 3. 해당 Weight를 이용하여 리뷰 랭킹 수행. (사람들이 관심을 많이 가지는 글에서 나온 단어를 다수 포함한 글은 좋은 글일 것이다.)
		 * 
		 * => 혹은 해당 Score를 하나의 필드로 넣어서, 사진 유무 등의 필드와 함께 분류 수행.
		 */

		WordCombiner wc = new WordCombiner();

		DecimalFormat df = new DecimalFormat("0.########");

		String basePath = "/Users/skplanet/Documents/2015_11ST/data/160118/data-no-dup-result";

		File dir = new File(basePath);

		String[] dirList = dir.list();

		//		for(int i=1; i<=nCats; i++) {

		Map<String, Integer> wordHm = new HashMap();	// word가 몇 개의 cat에서 등장하였는지 카운트.

		Map<String, Double> maxWHm = new HashMap();	// {Cat, maxW}
		
		for(String file : dirList) {

			if(file.endsWith(".sh"))
				continue;
			
			String filePath = basePath + "/" + file;

			System.out.println(filePath);

			FileReader fr = null;
			BufferedReader br = null;

			FileWriter fw = null;
			BufferedWriter bw = null;

			try {
				fr = new FileReader(filePath);
				br = new BufferedReader(fr);

				//				fw = new FileWriter(outputPath + "/" + i + "." + type + ".score");
				fw = new FileWriter("/Users/skplanet/Documents/2015_11ST/data/160118/weight/" + file);
				bw = new BufferedWriter(fw);

				/*
				 * 2. 리뷰 단위로 Loop
				 */
				String line = null;

				StringBuffer reviewSb = new StringBuffer();
				StringBuffer topicSb = new StringBuffer();

				String data = null;

				Map<String, Integer> weightHm = new HashMap();	// 단어의 조회수 누적
				Map<String, Integer> cntHm = new HashMap();	// 단어가 몇 개의 리뷰에서 등장하였는지
				Map<String, Set> pidHm = new HashMap();	// 단어가 몇 개의 pid에서 나왔는지.
				
				Double maxW = 0.0d;	// 최대 조회수.

				int cnt = 0;
				boolean firstLine = true;

				boolean isAttr = true;

				while( (line=br.readLine()) != null ) {

					if(line.startsWith("O")) {	// 원문.
						line = line.replace("O:", "");
						reviewSb.append(line + ". ");
					}


					if(line.startsWith("T")) {	// 토픽 분석 결과.
						line = line.replace("T:", "");
						topicSb.append(line + " ");
					}

					//					if(line.startsWith("I:B") && !firstLine) {
					if(line.startsWith("I:B") && !firstLine) {

						if(reviewSb.toString().contains("\t")) {
						//						if(reviewSb.toString().split("\t").length > 2) {




						//						if(isAttr) {

//						if(reviewSb.toString().split("\t").length == 16) {

							//							System.out.println(reviewSb);
							data = reviewSb.toString().replace("수정\t", "");	// 저장.	

							isAttr = false;

						} else {	// 실제 처리.




							cnt++;

							String[] dataArr = null;
							
							try {
								dataArr = data.split("\t", 16);
							}catch(Exception e) {
								e.printStackTrace();
								System.err.println(data);
							}
							
//							if(dataArr == null)
//								continue;

							//							if(dataArr.length < 16)
							//								System.out.println(data);

							
//							System.out.println(data);
							
							String pid = dataArr[0];

							String inqQty = dataArr[7];

							String userId = dataArr[11];
							
							String imgYn = dataArr[5];
							String movieYn = dataArr[12];
							
//							System.out.println(imgYn + ", " + movieYn);
							
							if(imgYn.equals("Y") || movieYn.equals("Y"))	// 텍스트만 보았을 때, 관심을 끈 어휘 찾기.
								continue;
							
							String review = reviewSb.toString();
							review = review.replace("제품을 사용하기 시작했을때, 피부상태 및 사용 동기 등을 써주세요..", "");
							review = review.replace("만족한 효과 및 기능", "");
							review = review.replace("사용제품 :", "");
							review = review.replace("내용", "");
							review = review.replace("  ", "");
							
							
//							if( Integer.parseInt( inqQty ) > 300 )	// good
//							if( Integer.parseInt( inqQty ) == 0 )	// bad
//								System.out.println( review );
							

							//							System.out.println(pid + ", " + inqQty + ", " + userId);
							//							
							//							if( Integer.parseInt( inqQty ) > 1000 )	// good
							//							if( Integer.parseInt( inqQty ) == 0 )	// bad
							//								System.out.println(reviewSb);
							//							System.out.println();

							String[] topicArr = topicSb.toString().split(" ");

							
							Set<String> topicHs = new HashSet();
							String cnoun = null;
							
//							System.out.println();
//							if(topicSb.toString().contains("cnoun_"))
							for(String topic : topicArr) {
								
//								System.out.println("[before] " + topic);
								
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
								
								
//								System.out.println("\t[after] " + topic);
								
								topicHs.add(topic);
							}
							
//							for(String topic : topicArr) {
							for(String topic : topicHs) {

								//								if(topic.length() < 2 || !topic.contains("_"))	// 한글자짜리는 불용어로 취급.
								if(topic.length() < 2)	// 한글자짜리는 불용어로 취급.
									continue;

								Integer w = weightHm.get(topic);

								if(w == null)
									w = 0;
								
								Integer subCnt = cntHm.get(topic);

								if(subCnt == null)
									subCnt = 0;
								
								Set subHs = pidHm.get(topic);

								if(subHs == null)
									subHs = new HashSet();
								
								subHs.add(pid);
								pidHm.put(topic, subHs);
								
								//								System.out.println("inqQty = " + inqQty);

								if(inqQty.equals("null") || inqQty.equals("N"))
									inqQty = "0";

								//								if(inqQty.equals("0"))
								//									inqQty = "-1000";
								//								
								//								try {
								//									w += Integer.parseInt( inqQty );
								//								} catch(Exception e) {
								//									e.printStackTrace();
								//									System.out.println(data);
								//									System.out.println("inqQty = " + inqQty);
								////									System.out.println(topicSb);
								//									
								//								}

//								if(inqQty.equals("0")) {
//									inqQty = "-1";
//
//									w--;
//
//								} else {
//
//									w ++;
//
//								}
								
								Integer qty = 0;
								
								
								try {
									qty = Integer.parseInt(inqQty);
								} catch(Exception e) {
									e.printStackTrace();
								}
								
								if(qty > 100)
									w += qty;
								
//								if(topic.equals("오오오오오조아오아어이이이"))
//									System.out.println(cnt + ", " + reviewSb.toString());



								//								try {
								//									w ++;
								//								} catch(Exception e) {
								//									e.printStackTrace();
								//									System.out.println(data);
								//									System.out.println("inqQty = " + inqQty);
								//									//									System.out.println(topicSb);
								//
								//								}

								weightHm.put(topic, w);
								
								/*
								 * Prod.별 최대치 저장. 
								 */
								
//								Integer maxW = maxWHm.get(pid);
//								
//								if(maxW == null)
//									maxW = w;
//								else if(w > maxW)
//									maxW = w;
//								
//								maxWHm.put(pid, maxW);
								
								
								/////////////////////////
								
								subCnt++;
								cntHm.put(topic, subCnt);
								
								
							}


							if( (cnt % 10000) == 0 ) {
								System.out.println("lineCnt = " + cnt);
							}

							data = null;

							isAttr = true;
						}

						//						writeScore(scoreAl, cnt, reviewSb, dupHs, bw, df);

						// 초기화.
						reviewSb = new StringBuffer();
						topicSb = new StringBuffer();

					}

					firstLine = false;


				}
				

				System.out.println("lineCnt = " + cnt);


				//				writeScore(scoreAl, cnt, reviewSb, dupHs, bw, df);

				// 초기화.
				reviewSb = new StringBuffer();
				topicSb = new StringBuffer();


				br.close();
				fr.close();



				/*
				 * Print
				 */

				for(String topic : weightHm.keySet()) {
					//					if( weightHm.get(topic) > 100000 )
					//					if( weightHm.get(topic) > 1000 )
					//					if( weightHm.get(topic) < 0 )
					//						System.out.println(topic + ", " + weightHm.get(topic));

					Integer weight = weightHm.get(topic);	// 출현 리뷰 조회 수 누적.
					Integer subCnt = cntHm.get(topic);	// 출현 리뷰 수 
					
					Integer pidCnt = pidHm.get(topic).size();	// 출현 PID 수
					
					
					Double w = weight.doubleValue();
					
					

//					bw.write(topic + "\t" + (subCnt.doubleValue() * weight.doubleValue() * pidCnt.doubleValue()) + "\n");
//					bw.write(topic + "\t" + Math.log10(subCnt.doubleValue() * weight.doubleValue() * pidCnt.doubleValue()) + "\n");
//					bw.write(topic + "\t" + Math.log10(subCnt.doubleValue()) + "\n");
//					bw.write(topic + "\t" + weight.doubleValue() + "\n");
					
					bw.write(topic + "\t" + weight.doubleValue() + "\n");

					//					if( weightHm.get(topic) < 1000 )
					//						System.out.println(topic + ", " + weightHm.get(topic));


					if(w > maxW)
						maxW = w;
					
					
					
					Integer wordCnt = wordHm.get(topic);

					if(wordCnt == null)
						wordCnt = 0;

					wordCnt ++;

					wordHm.put(topic, wordCnt);


				}

				//				System.exit(0);


				bw.close();
				fw.close();

				
				// 카테고리별 최대 조회 수, 저장.
				maxWHm.put(file, maxW);


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}


		/*
		 * 카테고리별 중요단어 다시 추출.
		 */
		
		
		basePath = "/Users/skplanet/Documents/2015_11ST/data/160118/weight";

		dir = new File(basePath);

		dirList = dir.list();

		for(String file : dirList) {
			
			if(file.endsWith(".sh") || file.endsWith(".sorted"))
				continue;

			String filePath = basePath + "/" + file;

			System.out.println(filePath);

			FileReader fr = null;
			BufferedReader br = null;

			FileWriter fw = null;
			BufferedWriter bw = null;

			try {
				fr = new FileReader(filePath);
				br = new BufferedReader(fr);
				
				
				Double maxW = maxWHm.get(file);	// 카테고리별 최대 조회수 가져오기.
				
				String idx = "0";
				
				
				if(file.contains("skintoner")) {
					idx = "1";
				} else if(file.contains("sunblock")) {
					idx = "2";
				} else if(file.contains("sunbalm")) {
					idx = "3";
				} else if(file.contains("bbcc")) {
					idx = "4";
				} else if(file.contains("foundation")) {
					idx = "5";
				}
				
				
				String newFile = idx + ".emi";
				
				
				Map<String, Double> emiHm = wc.readWordWeight("/Users/skplanet/Documents/2015_11ST/data/160105/emi/integration/" + newFile);	// EMI 가져오기.
				
				// Max EMI 찾기.
				Double maxEmi = 0.0d;
				for(String word : emiHm.keySet()) {
					
					Double emi = emiHm.get(word);
					
					if( emi > maxEmi ) 
						maxEmi = emi;
				}

				System.out.println("/Users/skplanet/Documents/2015_11ST/data/160105/emi/integration/" + newFile);
				System.out.println(emiHm.size());

				//				fw = new FileWriter(outputPath + "/" + i + "." + type + ".score");
				fw = new FileWriter("/Users/skplanet/Documents/2015_11ST/data/160118/weightCat/" + newFile);
				bw = new BufferedWriter(fw);

				/*
				 * 2. 리뷰 단위로 Loop
				 */
				String line = null;

				int cnt = 0;

				
				while( (line=br.readLine()) != null ) {
					
					String[] lineArr = line.split("\t"); 
					
					String topic = lineArr[0].trim();
					Double weight = Double.parseDouble( lineArr[1].trim() );
					
//					if(maxW.equals(weight))
//						System.out.println(maxW + ", " + weight);
					
					weight /= maxW;
//					System.out.println(weight);
					
//					if(weight > 1.0d) {
//						System.out.println(file + ", " + topic + ", " + weight + ", " + maxW);
//					}
					
					Integer catCnt = wordHm.get(topic);
					
//					weight = weight * (1 - (catCnt.doubleValue() / 5.0d));
//					weight = weight * (1 - (catCnt.doubleValue() / 6.0d));	// 너무 강하게 적용되는 느낌이라 카테고리 하나 더 있다고 가정.
	
					/*
					 * EMI와 결합.
					 */
					Double emi = emiHm.get(topic);
					
					if(emi == null)
						emi = 0.0d;
					
					emi /= maxEmi.doubleValue();	// max 정규화.
					
					weight = weight * emi;
					
					
					
//					weight = weight + emi;
					
//					weight = weight * 0.1 + emi * 0.9;
					
					
//					System.out.println(emi);
					
					
//					weight = weight * Math.log(5.0d/catCnt.doubleValue());
					
//					if(weight > 3.0d)
						bw.write(topic + "\t" + weight + "\n");
					
					cnt++;

					if( (cnt % 10000) == 0 ) {
						System.out.println("lineCnt = " + cnt);
					}
					
				}

				System.out.println("lineCnt = " + cnt);


				br.close();
				fr.close();

				bw.close();
				fw.close();


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
