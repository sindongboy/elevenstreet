package com.skplanet.tas.review.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.rule.HeuristicRule;
import com.skplanet.tas.review.util.ResourceLoader;

public class SpamTest {
	public static void main(String[] args) {
		
		
		System.out.println( (char) 160 );
		System.exit(0);
		
		ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/ranker");
		ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/");
		ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/config");
		ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/resource");
		ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/weight");
		
		
		Predictor pred = new Predictor();

		List<Map> statAl = pred.getStat(false);
		
//		List<Map> statAl = new ArrayList();
//		statAl.add(new HashMap());
//		statAl.add(new HashMap());
		
//		String review = "좋아요좋아요.";
//		String review = "196577406/sn 1234/sn 943407/sn 23974301/sn 2015/sn -/sp 02/sn -/sp 03/sn 18/sn :/sw 41/sn :/sw 28/sn ./sf 0/sn 주/nnb 문제/nng+품/xsn 선택/nng :/sw 아크/nng+바이/nng+스킨/nng 120/sn ml/eng";
		
		
		

		
		
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
		
		
		System.out.println(statAl.get(0));
		System.out.println(statAl.get(1));
		
//		String review = "용기가 작아서 놀랐어요.주먹안에 쏙 들어오네요.여름 한달 쓸 수 있으려나...시원한 젤감이 촉촉하고 끈적임 없이 좋으네요.흡수도 잘 되고...";
//		String review = "우선 다른 곳에 비해 같은 제품을 저렴하게 파시네요!배송도 아주 빨랐구요! 제품 자체의 효과는 좀 더 지켜봐야겠지만 좋은 것 같습니다~~ 일단은 확실히 추천!";
//		String review = "뉴트로지나 비싼거 빼면 괜찮은데뭔가 쓸수록 얼굴이 좀 많이 건조해지는 느낌입니다.자극적인 느낌.";
		
//		String review = "아자 아자 아자";
		
//		String review = "뉴트로지나 첨 사용해보는건데요... 좋다는 상품평들이 많아서 구입하게됐는데요..글쎄 전 별루인것 같아요 거품이 잘나지 않아서 사용하는데 불편하구 그러다보니 많이 여러번 사용하게 되더라구요...";
		
		String review = "잘 받았습니다. 마음에 들어요";
//		String review = "배송도 느리고 생각보다 별루네요.";
		
		String morphResult = "";
		
		Map<String, Object> featureHm = fe.extract(review, morphResult);
		
		System.out.println(featureHm);
		
		boolean isSpam = hr.isSpam(review, new StringBuffer(), statAl, featureHm, 2.0d);
		
		System.out.println(isSpam);
		
		
		
	}
}
