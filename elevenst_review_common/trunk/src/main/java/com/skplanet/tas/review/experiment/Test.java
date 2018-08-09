package com.skplanet.tas.review.experiment;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.rule.HeuristicRule;

public class Test {
	public static void main(String[] args) {
		
		
		
//		String str = "2015-08-29 09:00:08.0";
//		
//		System.out.println(str.substring(0, str.length()-2));
		
		
		String str = "제품/nng 마음/nng+에/jkb 들/vv+ㅂ니다/ef ./sf. 피부/nng+에/jkb+도/jks 잘/mag+맞/vv+고/ec 좋/va+네요/ef ./sf. 제/mm 피부/nng+에/jkb 보습/nng 효과/nng+와/jc 암튼/mag 가격/nng+대비/nng 만족/nng 만족/nng 하/vv+ㅂ니다/ef ./sf. ";
		
//		String[] strArr = str.split("[/+\\s]");
		
		str = str.replace("+", " ");
		
		String[] strArr = str.split(" ");
		
//		for(String subStr : strArr) {
//			System.out.println(subStr);
//		}
		
		

		for(int i=0; i<strArr.length; i++) {
			System.out.println(strArr[i]);
		}
		
		System.exit(0);
		
		
		Predictor pred = new Predictor();

		List<Map> statAl = pred.getStat(false);
		
//		String review = "좋아요좋아요.";
//		String review = "196577406/sn 1234/sn 943407/sn 23974301/sn 2015/sn -/sp 02/sn -/sp 03/sn 18/sn :/sw 41/sn :/sw 28/sn ./sf 0/sn 주/nnb 문제/nng+품/xsn 선택/nng :/sw 아크/nng+바이/nng+스킨/nng 120/sn ml/eng";
		String review = "";
		String morphResult = "";
		
		

		if( !review.contains(" /v") ) {
			System.out.println(review);	
		}
		
		System.exit(0);
		
		
//		System.out.println(review);
//		
//		System.out.println(review.replaceAll("[가-힣0-9A-Za-z ]", ""));
//		
//		// 특수문자 개수 (공백 제외).
//		System.out.println(review.replaceAll("[ㄱ-ㅎㅏ-ㅢ가-힣0-9A-Za-z ]", ""));
//		System.out.println(review.replaceAll("[ㄱ-ㅎㅏ-ㅢ가-힣0-9A-Za-z ]", "").length());
//
//		// 자,모 개수 (공백 제외).
//		System.out.println(review.replaceAll("[ㄱ-ㅎㅏ-ㅢ]", ""));
//		System.out.println(review.length() - review.replaceAll("[ㄱ-ㅎㅏ-ㅢ]", "").length());
		
		
		HeuristicRule hr = new HeuristicRule(false);
		FeatureExtractor fe = new FeatureExtractor();
		
		Map<String, Object> featureHm = fe.extract(review, morphResult);
		System.out.println(statAl.get(0));
		System.out.println(statAl.get(1));
		System.out.println(featureHm);
		
		boolean isSpam = hr.isSpam(review, new StringBuffer(), statAl, featureHm, 2.0d);
		
		System.out.println(isSpam);
		
		
		
	}
}
