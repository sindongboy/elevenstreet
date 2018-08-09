package com.skplanet.tas.review.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
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

public class WordCombiner {
	public void combine(String commentPath, String reviewPath, String outputPath, int nCats, String type) {
		
		DecimalFormat df = new DecimalFormat("#.########");
		
		for(int i=1; i<=nCats; i++) {
			String comment = commentPath + "/" + i + "." + type;	// type = {emi, tfidf}
			String review = reviewPath + "/" + i + "." + type;	// type = {emi, tfidf}
			
			Map<String, Double> commentHm = readWordWeight(comment);
			Map<String, Double> reviewHm = readWordWeight(review);
			
			Map<String, Double> combinedHm = combine(commentHm, reviewHm);
			
//			System.out.println( combinedHm.size() );
			
			FileWriter fw = null;
			BufferedWriter bw = null;
			
			try {
				fw = new FileWriter(outputPath + "/" + i + "." + type + ".combined");
				bw = new BufferedWriter(fw);
				
				for(String word : combinedHm.keySet()) {
					
					Double w = combinedHm.get(word);
					
					bw.write(word + "\t" + df.format(w) + "\n");
					
				}
				
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
	}
	
	public void combineWithCommon(String commentPath, String reviewPath, String outputPath, String cnounErrorPath, int nCats, String type) {
		
		DecimalFormat df = new DecimalFormat("#.########");
		
//		Map<String, Double> commonHm = new HashMap();	// 공통 어휘가 담길 공간.
		
		
		
		/*
		 * 복합어 오류 입력.
		 */
		HashSet<String> errorHs = new HashSet();
		for(int i=1; i<=nCats; i++) {
			
			FileReader fr = null;
			BufferedReader br = null;
			
			try {
				fr = new FileReader(cnounErrorPath + "/" + i + "." + type + ".combined.sorted.tsv");
				br = new BufferedReader(fr);
				
				String line = null;
				
				while( (line=br.readLine()) != null ) {
					
					String[] lineArr = line.split("\t");
					
					String word = lineArr[0];
					String flag = lineArr[3];
					
					if( !flag.trim().equals("") ) {
//						System.out.println(word + ", " + flag);
						
						errorHs.add(word);
					}
				}
				
				
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
		
		
		
		ArrayList<Map> catAl = new ArrayList();	// 카테고리별 어휘가 담길 공간.
		
		for(int i=1; i<=nCats; i++) {
			String comment = commentPath + "/" + i + "." + type;	// type = {emi, tfidf}
			String review = reviewPath + "/" + i + "." + type;	// type = {emi, tfidf}
			
			Map<String, Double> commentHm = readWordWeight(comment);
			Map<String, Double> reviewHm = readWordWeight(review);
			
			Map<String, Double> combinedHm = combine(commentHm, reviewHm);
			
			catAl.add(combinedHm);
			
//			System.out.println( combinedHm.size() );
			
		}
		
		
		/*
		 * 공통 어휘와 카테고리별 어휘를 구분.
		 */
		HashSet<String> commonHs = new HashSet();	// 공통 어휘.
		HashSet<String> nonCommonHs = new HashSet();	// 비공통 어휘.
//		HashSet<String> allHs = new HashSet();	// 하나로 통합.
		Map<String, Double> allHm = new HashMap();
		
		HashSet<String> cnounHs = new HashSet();
		
		int cnt = 0;
		for(int i=0; i<catAl.size(); i++) {
			Map<String, Double> combinedHm = catAl.get(i);
			// 5곳에 모두 존재하여야 공통 어휘.
			
			
			for(String word : combinedHm.keySet()) {
				Double w = combinedHm.get(word);
				
				if( allHm.containsKey(word) ) {
					w += allHm.get(word);
					
					w /= 2;	// 평균...
				}
				
				allHm.put(word, w);
				
				if( word.startsWith("cnoun_") )
					cnounHs.add(word);
				
				cnt++;
				
				// 비공통 어휘 목록에 존재하지 않으면, 일단 넣기.
				if( !nonCommonHs.contains(word) )
					commonHs.add(word);
			}

			// 현재 카테고리에 존재하지 않으면 제거.
			List<String> rmAl = new ArrayList();
			for(String word : commonHs) {
				if( !combinedHm.containsKey(word) ) {
//					commonHs.remove(word);
					rmAl.add(word);
					nonCommonHs.add(word);
				}
			}
			
			for(String word : rmAl) {
				commonHs.remove(word);
			}
			
		}
		
		System.out.println("commonHs = " + commonHs.size());
		System.out.println("nonCommonHs = " + nonCommonHs.size());
		System.out.println("allHm = " + allHm.size());
//		System.out.println("allHs = " + allHs.size());
		System.out.println("errorHs = " + errorHs.size());
		System.out.println(cnt);
		
		
		/*
		 * 복합어에서 불용어 제거.
		 */
		for(String word : errorHs) {
			cnounHs.remove(word);
		}
		
		/*
		 * 통합 어휘 출력.
		 */
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			fw = new FileWriter(outputPath + "/all." + type + ".combined");
			bw = new BufferedWriter(fw);
			
			for(String word : allHm.keySet()) {
				
				
				boolean isPartOfCnoun = false;
				
				for(String cnoun : cnounHs) {
					if( cnoun.contains(word) ) {
						isPartOfCnoun = true;
//						System.out.println(cnoun + " // " + word);
						break;
					}
						
				}

				if( !isPartOfCnoun ) {
					Double w = allHm.get(word);
					

					
//					if( !word.startsWith("cnoun_") || !word.startsWith("sa"))
					bw.write(word + "\t" + df.format(w) + "\n");
				}
			}
			
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*
		 * 카테고리별 어휘 출력 (if문 확인)
		 */
		for(int i=0; i<catAl.size(); i++) {
			
			Map<String, Double> combinedHm = catAl.get(i);

			fw = null;
			bw = null;
			
			try {
				fw = new FileWriter(outputPath + "/" + (i+1) + "." + type + ".combined");
				bw = new BufferedWriter(fw);
				
				for(String word : combinedHm.keySet()) {
					
					if( !commonHs.contains(word) ) {
						Double w = combinedHm.get(word);
						
						bw.write(word + "\t" + df.format(w) + "\n");
					}
				}
				
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private Map<String, Double> combine(Map<String, Double> map1, Map<String, Double> map2) {
		Map<String, Double> map = new HashMap();
		
		// map1 기준 통합.
		for(String word : map1.keySet()) {
			Double w = map1.get(word);
			
			if( map2.containsKey(word) ) {
				Double subW = map2.get(word);
				
				w = (w + subW) / 2.0d;	// 양쪽에 다 있으면 평균을 이용. 
			}
			
			if( !w.equals(0.0d) )
				map.put(word, w);
		}
		
		// map2 기준 통합 (map2만 가지고 있는 것들 병합)
		for(String word : map2.keySet()) {

			if( !map.containsKey(word) ) {	// map에 포함되지 않은 단어이면..
				Double w = map2.get(word);
				
				if( !w.equals(0.0d) )
					map.put(word, w); 
			}
			
			
		}
		
		return map;
	}
	
	public Map<String, Double> readWordWeight(String filePath) {
		Map<String, Double> hm = new HashMap();
		
		FileReader fr = null;
		BufferedReader br = null;
		
		Set<String> stopwordHs = new HashSet();
//		stopwordHs.add("cnoun_싸이닉");
//		stopwordHs.add("토너");
//		stopwordHs.add("null");
//		stopwordHs.add("cnoun_위치하젤");
//		stopwordHs.add("cnoun_클린앤클리어");
//		stopwordHs.add("올데이파인");
//		stopwordHs.add("포어");
//		stopwordHs.add("데이");
//		stopwordHs.add("cnoun_스와니코코");
//		stopwordHs.add("cnoun_올데이파인포어토너");
		
		
		String line = null;
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			
			while( (line=br.readLine()) != null  ) {
				String[] lineArr = line.split("\t");
				
				String word = lineArr[0];
				Double weight = Double.parseDouble( lineArr[1] );
				
//				if( weight <= 3.0d )
////				if( weight <= 1000.0d )
//					continue;
				
//				if(!weight.equals(0.0d))
//					weight = Math.log10(weight);
				
				
				
				if( !stopwordHs.contains(word) )
					hm.put(word, weight);
				
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return hm;
	}
	
	public Map<String, double[]> readSemanticVector(String filePath) {
		Map<String, double[]> hm = new HashMap();
		
		FileReader fr = null;
		BufferedReader br = null;
		
		String line = null;
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			
			while( (line=br.readLine()) != null  ) {
				String[] lineArr = line.split("\t");
				
				String key = lineArr[0];
//				Double weight = Double.parseDouble( lineArr[1] );
				
				String val = lineArr[1].trim();
				
				String[] valArr = val.split(" ");
				
				double[] newValArr = new double[valArr.length];
				for(int i=0; i<valArr.length; i++) {
					newValArr[i] = Double.parseDouble(valArr[i]);
				}
				
				hm.put(key, newValArr);
				
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return hm;
	}
}
