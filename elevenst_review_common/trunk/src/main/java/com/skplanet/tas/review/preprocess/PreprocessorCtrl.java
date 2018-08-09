package com.skplanet.tas.review.preprocess;

import java.io.File;
import java.util.List;
import java.util.Map;

public class PreprocessorCtrl {
	public static void main(String[] args) {
		
		Preprocessor p = new Preprocessor();
		
		String basePath = "/Users/skplanet/Documents/2015_11ST/data/160111/merge";
		
		File dir = new File(basePath);
		
		String[] dirList = dir.list();

		for(String file : dirList) {
		
			String filePath = basePath + "/" + file;

			System.out.println(filePath);
			/*
			 * 1. Product별 Max 조회수 뽑기.
			 */
//			Map<String, Integer> maxHm = p.getMax(filePath);
			
			/*
			 * 2. Product별 리뷰 Max 정규화
			 */
//			List<Double> normAl = p.norm(maxHm, filePath);
			List<Double> normAl = p.norm(filePath);
			
			/*
			 * 3. 평균 조회수 구하기.
			 */
			Double avg = p.mean(normAl);
			
			Double stddev = p.stddev(normAl, avg);
//			
			System.out.println(avg + ", " + stddev);
			
////			
////			normAl = p.calcZScore(normAl, avg, stddev);
			
//			System.exit(0);
			
			/*
			 * 4. 평균 조회수 기반 레이블 부착.
			 */
			p.addLabel(filePath, "/Users/skplanet/Documents/2015_11ST/data/160111/labeled/" + file, normAl, avg, stddev);
			
		}
		
		
	}
}
