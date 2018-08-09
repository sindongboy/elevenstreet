package com.skplanet.tas.review.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Preprocessor {


	/*
	 * 1. Product별 Max 조회수 뽑기. <ProductId, maxCnt>
	 */
	public Map<String, Integer> getMax(String path) {
		Map<String, Integer> m = new HashMap();
		
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			fr = new FileReader(path);
			br = new BufferedReader(fr);
			
			String line = null;
			
			while( (line=br.readLine()) != null ) {
				
//				System.out.println(line);
				
				String[] lineArr = line.split("\t");
				
				String prdNo = null;
				Integer qty = 0;
				try {
					prdNo = lineArr[0];
					qty = Integer.parseInt( lineArr[7] );
					
					
				} catch(Exception e) {
					e.printStackTrace();
					System.err.println(qty);
				}

				Integer maxQty = m.get(prdNo);
				
				if( maxQty == null || qty > maxQty ) {
					m.put(prdNo, qty);
				}
				
//				if( maxQty == null ) {
//					m.put(prdNo, qty);
//				} else {
//					
//					
//					if(qty > maxQty)
//						maxQty = qty;
//					
//					m.put(prdNo, maxQty);
//				}
				
//				System.out.println("prdNo = " + prdNo + ", qty = " + qty + ", maxQty = " + m.get(prdNo));
				
				
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
		
		return m;
	}
	
	/*
	 * 2. Product별 리뷰 Max 정규화
	 */
	public List<Double> norm(Map<String, Integer> maxHm, String path) {
		List<Double> al = new ArrayList();
		
		

		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			fr = new FileReader(path);
			br = new BufferedReader(fr);
			
			String line = null;
			
			while( (line=br.readLine()) != null ) {
				
//				System.out.println(line);
				
				String[] lineArr = line.split("\t");
				
				String prdNo = null;
				Double qty = 0.0d;
				try {
					prdNo = lineArr[0];
					qty = Double.parseDouble( lineArr[7] );
					
				} catch(Exception e) {
					e.printStackTrace();
					System.err.println(qty);
				}
				
				if(qty > 0.0d) {	// 0보다 클때만.
					Integer maxQty = maxHm.get(prdNo);
					
//					if(maxQty < 1000) {	// 10보다 작으면 신뢰도가 떨어지므로 나쁜 리뷰로 보내버리기?
////						qty = -1.0d;
//						qty = 0.0d;
////						System.out.println("maxQty = " + maxQty);
//					} else {
						qty = qty / maxQty.doubleValue();
//					}
//					System.out.println(qty);
				}
				
				al.add(qty);
					
				
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
		
		return al;
	}
	
	public List<Double> norm(String path) {
		List<Double> al = new ArrayList();
		List<String> prodAl = new ArrayList();
		
		Map<String, List<Double>> hm = new HashMap();	// <PID, [q1,q2,q3, ...]>

		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			fr = new FileReader(path);
			br = new BufferedReader(fr);
			
			String line = null;
			
			while( (line=br.readLine()) != null ) {
				
//				System.out.println(line);
				
				String[] lineArr = line.split("\t");
				
				String prdNo = null;
				Double qty = 0.0d;
				try {
					prdNo = lineArr[0];
					
					try {
						qty = Double.parseDouble( lineArr[7] );
					} catch(Exception e) {
						e.printStackTrace();
						qty = 0.0d;
					}
					
					List<Double> subAl = hm.get(prdNo);
					
					if(subAl == null)
						subAl = new ArrayList();
					
					subAl.add(qty);
					hm.put(prdNo, subAl);
					
				} catch(Exception e) {
					e.printStackTrace();
					System.err.println(qty);
				}
				
				al.add(qty);
				prodAl.add(prdNo);
					
				
			}
			
			
			/*
			 * Product별 평균, 표준편차 계산.
			 */
			Map<String, List<Double>> statHm = new HashMap();
			for(String prodNo : hm.keySet()) {
				
				List<Double> subAl = hm.get(prodNo);
				
				Double avg = mean(subAl);
				
				Double stddev = stddev(subAl, avg);
				
//				subAl = calcZScore(subAl, avg, stddev);
				
				List<Double> subStatAl = new ArrayList();
				subStatAl.add(avg);
				subStatAl.add(stddev);
				
//				System.out.println(avg + ", " + stddev);
				
				statHm.put(prodNo, subStatAl);
				
			}
			
			System.out.println(statHm.size());
			
			/*
			 * Z-score 계산.
			 */
			for(int i=0; i<al.size(); i++) {
				
				Double qty = al.get(i);
				String prdNo = prodAl.get(i);
				
//				System.out.println(prdNo + ", " + qty);
				List<Double> statAl = statHm.get(prdNo);
				
				
				Double avg = statAl.get(0);
				Double stddev = statAl.get(1);
				
				Double z = (qty - avg) / stddev;
				
//				System.out.println(z);
				
				if(z.isInfinite() || z.isNaN())
					z = 0.0d;
				
				al.set(i, z);
				
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
		
		return al;
	}
	
	/*
	 * 3. 평균 조회수 구하기.
	 */
	public Double mean(List<Double> qtyAl) {
		Double avg = 0.0d;
		
		for(Double qty : qtyAl) {
			avg += qty;
		}
		
		avg /= new Double( qtyAl.size() );
		
		return avg;
	}
	
	public Double stddev(List<Double> qtyAl, Double avg) {
		Double stddev = 0.0d;
		
		for(Double qty : qtyAl) {
			stddev += Math.pow((qty - avg), 2);
//			System.out.println(qty + ", " + avg);
//			System.out.println ( Math.pow((qty - avg), 2) ) ;
		}
		
		stddev /= new Double( qtyAl.size() );
		
		stddev = Math.sqrt( stddev );
		
		return stddev;
	}
	
	public List<Double> calcZScore(List<Double> qtyAl, Double avg, Double stddev) {
		
		List<Double> al = new ArrayList();
		
		for(Double qty : qtyAl) {
			Double z = (qty - avg) / stddev;
//			System.out.println(z + " | " + qty);
			al.add(z);
		}
		
		return al;
	}
	
	
	
	/*
	 * 4. 평균 조회수 기반 레이블 부착.
	 */
	public void addLabel(String input, String output, List<Double> qtyAl, Double avgQty, Double stddev) {
		
		
		Set<String> uniqHs = new HashSet();
		Set<String> dupHs = new HashSet();

		FileReader fr = null;
		BufferedReader br = null;
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			fr = new FileReader(input);
			br = new BufferedReader(fr);
			
			fw = new FileWriter(output);
			bw = new BufferedWriter(fw);
			
			String line = null;
			
			int idx = 0;
			while( (line=br.readLine()) != null ) {
				
//				System.out.println(line);
				
				String[] lineArr = line.split("\t");
				
				String prdNo = null;
//				Integer qty = 0;
//				try {
//					prdNo = lineArr[0];
//					qty = Integer.parseInt( lineArr[7] );
//					
//				} catch(Exception e) {
//					e.printStackTrace();
//					System.err.println(qty);
//				}
				
				Double qty = qtyAl.get(idx);
				
				idx++;
				
				
				
				
				/*
				 * Labeling
				 */
				String label = "fine";
				
//				if(qty <= avgQty)
				if(qty <= 0.0d)
					label = "spam";
				
////				if( label.equals("spam") ) {
//				if( label.equals("fine") ) {
////				if( score.equals(0.0d) ) {
////				if( score.equals(1.0d) ) {
//				
////				if( qty > 0.4 ) {
////				if( score <= 0.0 ) {
//					
////				if(qty > avgQty + (3 * stddev) ) {
////				if(qty < avgQty - (2 * stddev) ) {
////				if(qty < 0.0 ) {
//					System.out.println(qty + ", " + avgQty + ", " + lineArr[lineArr.length-3]);
//				}

				
				bw.write(line + "\t" + qty + "\t" + label + "\n");
				
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
	
	public int count(String str, String findStr) {
		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){

		    lastIndex = str.indexOf(findStr,lastIndex);

		    if(lastIndex != -1){
		        count ++;
		        lastIndex += findStr.length();
		    }
		}
		
		return count;
	}
}
