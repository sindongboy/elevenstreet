package com.skplanet.tas.review.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class FileMerger {
	public static void main(String[] args) {

		String basePath = "/Users/skplanet/Documents/2015_11ST/data/160111/data-no-dup-result";
		
		File dir = new File(basePath);
		
		String[] dirList = dir.list();

//		for(int i=1; i<=nCats; i++) {
		
		for(String file : dirList) {
		
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
				fw = new FileWriter("/Users/skplanet/Documents/2015_11ST/data/160111/merge/" + file);
				bw = new BufferedWriter(fw);

				/*
				 * 2. 리뷰 단위로 Loop
				 */
				String line = null;

				StringBuffer reviewSb = new StringBuffer();
				StringBuffer topicSb = new StringBuffer();
				StringBuffer morphSb = new StringBuffer();
				
				String data = null;

				Map<String, Integer> weightHm = new HashMap();
				
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
					
					if(line.startsWith("M")) {	// 토픽 분석 결과.
						line = line.replace("M:", "");
						morphSb.append(line + " ");
					}
					
					
					if(line.startsWith("I:B") && !firstLine) {
						
						
						if(reviewSb.toString().split("\t").length == 16) {
						
							data = reviewSb.toString().replace("수정\t", "");	// 저장.	
						
							isAttr = false;
							
							morphSb = new StringBuffer();
							topicSb = new StringBuffer();
							
						} else {	// 실제 처리.

							
							cnt++;

							
							if( (cnt % 10000) == 0 ) {
								System.out.println("lineCnt = " + cnt);
							}
							

							// write
							bw.write(data + "\t");
							bw.write(reviewSb + "\t");
							bw.write(topicSb + "\t");
							bw.write(morphSb + "\n");
							
							data = null;

							isAttr = true;
						}
						

						// 초기화.
						reviewSb = new StringBuffer();
						topicSb = new StringBuffer();
						morphSb = new StringBuffer();
						
					}

					firstLine = false;


				}

				System.out.println("lineCnt = " + cnt);

				
				// write
				bw.write(data + "\t");
				bw.write(reviewSb + "\t");
				bw.write(topicSb + "\t");
				bw.write(morphSb + "\n");
				
				// 초기화.
				reviewSb = new StringBuffer();
				topicSb = new StringBuffer();
				morphSb = new StringBuffer();


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
