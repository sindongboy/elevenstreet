package com.skplanet.tas.review.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ThemeKeywordImporter {
	public static void main(String[] args) {
		
		/*
		 * 0: (Input) Theme File
		 * 1: (Input) Prev. Cnoun Dic.
		 * 2: (Output) New Cnoun Dic.
		 */
		
		String theme = args[0];
		String prevCnoun = args[1];
		String newCnoun = args[2];
		
		Set<String> cnounHs = new HashSet();
		
		try {
			/*
			 * 1. Prev. Cnoun 적재.
			 */
			BufferedReader br = new BufferedReader(new FileReader(prevCnoun));
			
			String line = null;
			
			while( (line = br.readLine()) != null ) {
//				System.out.println(line);
				cnounHs.add( line.trim() );
			}
			
			System.out.println("[Before] " + cnounHs.size());
			br.close();
			
			/*
			 * 2. Theme File 파싱.
			 */
			br = new BufferedReader(new FileReader(theme));
			
			line = null;
			
			br.readLine();
			
			while( (line = br.readLine()) != null ) {
//				System.out.println("==== " + line);
				
				String[] lineArr = line.split("\t");
				
				for(int i=1; i<lineArr.length; i+=2) {
					
//					System.out.println( lineArr[i].trim() );
					
					String keyword = lineArr[i].replaceAll("[!@#$%^&*]", "").trim();
					
					if( keyword.length() == 0 )
						continue;
					
					cnounHs.add( keyword );
					
//					System.out.println(keyword);
				}
				
//				System.out.println();
				
				
			}
			System.out.println("[After] " + cnounHs.size());
			br.close();
			
			/*
			 * 3. 신규 Cnoun 파일에 추가.
			 */
			BufferedWriter bw = new BufferedWriter(new FileWriter(newCnoun));
			for(String cnoun : cnounHs) {
//				System.out.println(cnoun);
				
				StringBuffer sb = new StringBuffer();
				
				sb.append(cnoun);
				sb.append("\n");
				
				bw.write(sb.toString());
				
				
			}
			
			bw.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
