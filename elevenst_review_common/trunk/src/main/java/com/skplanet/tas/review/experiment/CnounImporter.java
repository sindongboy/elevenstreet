package com.skplanet.tas.review.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class CnounImporter {
	public void importCnouns(String input, String output) {
		FileReader fr = null;
		BufferedReader br = null;
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		
		
		try {
			
			fr = new FileReader(input);
			br = new BufferedReader(fr);
			
			fw = new FileWriter(output);
			bw = new BufferedWriter(fw);
			
			
			HashSet<String> cnounHs = new HashSet();
			
			String line = null;
			
			while( (line=br.readLine()) != null ) {
				
				String[] lineArr = line.split("\t");
				
				if( lineArr.length == 3 ) {
					
					String[] subArr = lineArr[2].split(",");
					
//					System.out.println(line);
					
//					System.out.println(lineArr[0] + " || " + lineArr[2]);
					
					for( int i=0; i<subArr.length; i++)  {
						String cnoun = subArr[i].trim().replace(" ", "");
						
//						System.out.println( cnoun );
						
						if(!cnoun.contains("?") && !cnoun.equals("") && cnoun.length() > 1)
							cnounHs.add( cnoun );
						
					}
					System.out.println();
					
//					System.out.println(lineArr[2]);
					
				}
			}
			
			
			for(String cnoun : cnounHs) {
				bw.write(cnoun + "\n");
			}
			
			
			bw.close();
			fw.close();
			
			br.close();
			fr.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void importCnouns2(String input, String output) {
		FileReader fr = null;
		BufferedReader br = null;
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		
		
		try {
			
			fr = new FileReader(input);
			br = new BufferedReader(fr);
			
			fw = new FileWriter(output);
			bw = new BufferedWriter(fw);
			
			
			br.readLine();	// Column 스킵.
			
			HashSet<String> cnounHs = new HashSet();
			
			String line = null;
			
			while( (line=br.readLine()) != null ) {
				
				String[] lineArr = line.split("\t");
				

//				System.out.println(line);
				
				/*
				 * 복합여 여부 마킹 체크.
				 */
				if( lineArr.length == 4 ) {
					if( lineArr[3].trim().equals("1") ) {
						cnounHs.add( lineArr[0].trim().replace("_", "") );
					}
				}
				
				if( lineArr.length == 5 ) {
					
					
					/*
					 * 추정 복합어 처리
					 */
					String[] subArr = lineArr[4].split(",");
					
//					System.out.println(line);
					
//					System.out.println(lineArr[0] + " || " + lineArr[2]);
					
					for( int i=0; i<subArr.length; i++)  {
						String cnoun = subArr[i].trim().replace(" ", "").replace("_", "");
						
//						System.out.println( cnoun );
						
						if(!cnoun.contains("?") && !cnoun.equals("") && cnoun.length() > 1)
							cnounHs.add( cnoun );
					}
					System.out.println();
					
//					System.out.println(lineArr[2]);
					
				}
			}
			
			
			for(String cnoun : cnounHs) {
				bw.write(cnoun + "\n");
			}
			
			
			bw.close();
			fw.close();
			
			br.close();
			fr.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
