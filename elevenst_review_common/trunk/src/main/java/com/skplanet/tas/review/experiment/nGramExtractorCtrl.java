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
import java.util.Map;

public class nGramExtractorCtrl {
	public static void main(String[] args) {
		String filePath = "/Users/skplanet/Documents/2015_11ST/data/160118/data-no-dup-result";

		File dir = new File(filePath);

		String[] files = dir.list();

		FileReader fr = null;
		BufferedReader br = null;

		FileWriter fw = null;
		BufferedWriter bw = null;
		
		FileWriter fw2 = null;
		BufferedWriter bw2 = null;
		
		

		for(String file : files) {

			Map<String, Integer> wordHm = new HashMap();
			
			Map<String, Integer> biGramHm = new HashMap();
			Map<String, Integer> triGramHm = new HashMap();
			
			String outputFile = "/Users/skplanet/Documents/2015_11ST/data/160118/n-gram/" + file; 

			try {
				fr = new FileReader(filePath + "/" + file);

				br = new BufferedReader(fr);

				fw = new FileWriter(outputFile);
				bw = new BufferedWriter(fw);


				System.out.println(filePath + "/" + file);

				/*
				 * 2. 리뷰 단위로 Loop
				 */
				String line = null;

				StringBuffer reviewSb = new StringBuffer();
				StringBuffer topicSb = new StringBuffer();

				String data = null;

				int cnt = 0;
				boolean firstLine = true;
				
				while( (line=br.readLine()) != null ) {

					if(line.startsWith("O")) {	// 원문.
						line = line.replace("O:", "");
						reviewSb.append(line + ". ");
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
							
							String[] topicArr = topicSb.toString().split(" ");
							
							String cnoun = "";
							
//							String word1 = "";
//							String word2 = "";
//							String word3 = "";
							
							ArrayList<String> al = new ArrayList();
							HashSet<String> wordHs = new HashSet();
							
							for(String topic : topicArr) {
								
								if( topic.trim().equals("") || (topic.startsWith("S") && topic.contains("_")) )
									continue;
								
								if( cnoun.length() > 0 ) {	// 복합어만 출력하고, 구성하는 단어들은 스킵.
									if(cnoun.startsWith(topic)) {
//										cnoun = cnoun.replace(topic, "");
//										System.out.println("before = " + cnoun);
//										System.out.println(topic);
										cnoun = cnoun.substring(topic.length(), cnoun.length());
//										System.out.println("after = " + cnoun);
									}
									
									continue;
								}
								
								if( topic.startsWith("cnoun_") ) {
									cnoun = topic.replace("cnoun_", "");
									
									topic = cnoun;
								}
								
								wordHs.add(topic);
								al.add(topic);
								
								/*
								 * N-gram
								 */
								
								if( al.size() > 3 )
									al.remove(0);
								
								Integer instNum = al.size();
								
								// bi-gram
								if( al.size() >= 2 ) {
									String word1 = al.get( instNum - 1 );
									String word2 = al.get( instNum - 2 );
									
									String bigram = word2 + "_" + word1;
									
									Integer subCnt = biGramHm.get(bigram);
									
									if( subCnt == null )
										subCnt = 0;
									
									subCnt ++;
									
									
									biGramHm.put(bigram, subCnt);
								}
								
								
								// tri-gram
								if( al.size() == 3 ) {
									String word1 = al.get( instNum - 1 );
									String word2 = al.get( instNum - 2 );
									String word3 = al.get( instNum - 3 );
									
									String trigram = word3 + "_" + word2 + "_" + word1;
									
									Integer subCnt = triGramHm.get(trigram);
									
									if( subCnt == null )
										subCnt = 0;
									
									subCnt ++;
									
									
									triGramHm.put(trigram, subCnt);
								}
								
								 
								
//								System.out.println(topic);
								
								
								
							}
//							System.out.println();
							
//							if( topicSb.toString().contains("cnoun_") )
//								System.exit(0);
						
							// 출현 단어 저장 (문서 단위)
							for( String word : wordHs ) { 
								Integer subCnt = wordHm.get(word);
								
								if(subCnt == null)
									subCnt = 0;
								
								subCnt ++;
								
								wordHm.put(word, subCnt);
							}
							
							cnt++;

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
				
				/*
				 * Bigram 확인.
				 */
//				for(String bigram : biGramHm.keySet()) {
//					Integer subCnt = biGramHm.get(bigram);
//					
////					if( subCnt > 1000) {
////					if( subCnt > 10 && subCnt < 50) {
////						System.out.println(bigram + ", " + subCnt);
////					}
//					
////					if( subCnt > 1) {
//					if( subCnt > 20) {
//						
//						String[] bigramArr = bigram.split("_");
//						
//						if( !bigramArr[0].equals(bigramArr[1]) ) {
//						
//							Double a = wordHm.get( bigramArr[0] ).doubleValue() / new Double(cnt);
//							Double b = wordHm.get( bigramArr[1] ).doubleValue() / new Double(cnt);
//							Double ab = subCnt.doubleValue() / new Double(cnt);
//							
//							Double pmi = Math.log(ab / (a*b));
////							pmi /= Math.log(ab);	// Normalize.. (문제있음.)
//							
//	//						bw.write(bigram + "\t" + subCnt + "\n");
//							bw.write(bigram + "\t" + pmi + "\t" + subCnt + "\n");
//						}
//					}
//					
//				}
				
				for(String trigram : triGramHm.keySet()) {
					Integer subCnt = triGramHm.get(trigram);
					
//					if( subCnt > 100) {
//						System.out.println(trigram + ", " + subCnt);
//					}
					
//					if( subCnt > 1) {
					if( subCnt > 10) {
//						bw.write(trigram + "\t" + subCnt + "\n");
						String[] trigramArr = trigram.split("_");
						
						if( !trigramArr[0].equals(trigramArr[1]) || !trigramArr[1].equals(trigramArr[2]) ) {
						
							try {
								Double a = wordHm.get( trigramArr[0] ).doubleValue() / new Double(cnt);
								Double b = wordHm.get( trigramArr[1] ).doubleValue() / new Double(cnt);
								Double c = wordHm.get( trigramArr[2] ).doubleValue() / new Double(cnt);
								Double abc = subCnt.doubleValue() / new Double(cnt);
								
								Double pmi = Math.log(abc / (a*b*c));	// 수식적으로 문제 없는지 확인 필요.
	//							pmi /= Math.log(ab);	// Normalize.. (문제있음.)
								
		//						bw.write(bigram + "\t" + subCnt + "\n");
								bw.write(trigram + "\t" + pmi + "\t" + subCnt + "\n");
							} catch(Exception e) {
								e.printStackTrace();
								System.out.println(trigramArr[0] + ", " +  wordHm.get( trigramArr[0] ));
							}
						}
					}
				}
				

				br.close();
				fr.close();
				
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
