package com.skplanet.tas.review.experiment;

import java.util.HashMap;
import java.util.Map;

public class ReviewRankerCtrl {
	public static void main(String[] args) {
		ReviewRanker rr = new ReviewRanker();
		
		int nCats = 5;
		
//		String path = "/Users/skplanet/Documents/2015_11ST/data/151228";
//		
//		rr.score(path + "/comment_emi/emi_result", path + "/11st_comment", path + "/11st_comment" + "_score", nCats, "emi");
//		rr.score(path + "/review_emi/emi_result", path + "/11st_review", path + "/11st_review" + "_score", nCats, "emi");
		
		
//		String path = "/Users/skplanet/Documents/2015_11ST/data/160105";
//		
////		rr.score(path + "/emi/integration", path + "/text/comment-result", path + "/11st_comment" + "_score", nCats, "emi");
////		rr.score(path + "/emi/integration", path + "/text/review-result", path + "/11st_review" + "_score", nCats, "emi");
//		
//		rr.score(path + "/emi/integration", path + "/text/comment-result", path + "/11st_comment" + "_score2", nCats, "emi");
//		rr.score(path + "/emi/integration", path + "/text/review-result", path + "/11st_review" + "_score2", nCats, "emi");

		
		String path = "/Users/skplanet/Documents/2015_11ST/data/160105";
		
		
		rr.score("/Users/skplanet/Documents/2015_11ST/data/160118/weightCat", path + "/text/comment-result", path + "/11st_comment" + "_score2", nCats, "emi");
		rr.score("/Users/skplanet/Documents/2015_11ST/data/160118/weightCat", path + "/text/review-result", path + "/11st_review" + "_score2", nCats, "emi");

		
	}
}
