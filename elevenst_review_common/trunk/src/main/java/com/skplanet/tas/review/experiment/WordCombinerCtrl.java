package com.skplanet.tas.review.experiment;

public class WordCombinerCtrl {
	public static void main(String[] args) {
		WordCombiner wc = new WordCombiner();
		
		int nCats = 5;
		
		wc.combineWithCommon("/Users/skplanet/Documents/2015_11ST/data/151228/comment_emi/emi_result", "/Users/skplanet/Documents/2015_11ST/data/151228/review_emi/emi_result", "/Users/skplanet/Documents/2015_11ST/data/151228/combined_emi", "/Users/skplanet/Downloads", nCats, "emi");
//		wc.combineWithCommon("/Users/skplanet/Documents/2015_11ST/data/151228/comment_emi/tfidf_result", "/Users/skplanet/Documents/2015_11ST/data/151228/review_emi/tfidf_result", "/Users/skplanet/Documents/2015_11ST/data/151228/combined_tfidf", nCats, "tfidf");
	}
}
