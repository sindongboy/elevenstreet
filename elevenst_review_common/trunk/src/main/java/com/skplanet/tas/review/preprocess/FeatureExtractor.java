package com.skplanet.tas.review.preprocess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.skplanet.hnlp.languageModel.LanguageModelAPI;
import com.skplanet.nlp.usermodeling.topical.util.TermExtractor;
import com.skplanet.nlp.usermodeling.topical.util.TermExtractor.TextAnalysisInfoForSimilarWord;
import com.skplanet.tas.review.rule.HeuristicRule;

public class FeatureExtractor {
	private static final char[] CHOSUNG = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ',
		'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };
	private static final char[] JUNGSUNG = { 'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ',
		'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ',
	'ㅣ' };
//	private static final char[] JONGSUNG = { ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ',
//		'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
//		'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };
	private static final char[] JONGSUNG = { 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ',
		'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
		'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };
	
	static File file;
//	LanguageModelAPI lmAPI;
	
	private Set<Character> hangul;
	
	public FeatureExtractor() {
		hangul = new HashSet();
		
		
		for(char ch : CHOSUNG) {
			hangul.add(ch);	
		}
		
		for(char ch : JUNGSUNG) {
			hangul.add(ch);	
		}
		
		for(char ch : JONGSUNG) {
			hangul.add(ch);	
		}
		
//		//Get file from resources folder
//		ClassLoader classLoader = getClass().getClassLoader();
//		file = new File(classLoader.getResource("review.3gram.lm").getFile());
//		
//		lmAPI = new LanguageModelAPI();
//		lmAPI.load(file.getAbsolutePath());
		
		
	}

	public Map<String, Object> extract(String txt, String morphResult) {

		// Preprocessing
		
		txt = txt.replaceAll("\\s+", " ").trim();	// 반복되는 공백 제거.
		txt = txt.replaceAll(" +", "").trim();	// 반복되는 특수문자 제거.
		
//		txt = txt.replace("제품을 사용하기 시작했을때, 피부상태 및 사용 동기 등을 써주세요..", "");
//		txt = txt.replace("만족한 효과 및 기능", "");
//		txt = txt.replace("사용제품 :", "");
//		txt = txt.replace("내용", "");
		
//		System.out.println(txt);
//		System.exit(0);
		
		
		//		review = review.replace("  ", "");


		Map<String, Object> hm = new HashMap();

		// 특수문자가 나오면 "특수문자 + 스페이스"로 변경?

		//		String[] wordArr = txt.split(" ");	// 특수문자들 다 포함하여 Split할 수 있는 방법은?


		char[] txtArr = txt.toCharArray();

		ArrayList<String> wordAl = new ArrayList();

		StringBuffer wordSb = new StringBuffer();
		StringBuffer spSb = new StringBuffer();

		for(char ch : txtArr) {
			if( (ch >= 33 && ch <= 47)
					|| (ch >= 58 && ch <= 64)
					|| (ch >= 91 && ch <= 96)
					|| (ch >= 123 && ch <= 126)
					) {
				//				System.out.println(ch);

//				if( wordSb.length() > 0 ) {
//					System.out.println(wordSb);
//					wordAl.add(wordSb.toString());
//					wordSb = new StringBuffer();
//				}
				spSb.append(ch);
			} else {

//				if( spSb.length() > 0 ) {
//					wordAl.add(spSb.toString());
//					spSb = new StringBuffer();
//				}
				
//				if( ch != ' ')	// 공백 문자가 아닐 때만.
					wordSb.append(ch);
//				else {
//					
//				}
					
			}
		}

		if( wordSb.length() > 0 ) {
			String[] subArr = wordSb.toString().split(" ");
			for(String subStr : subArr) {
				if(!subStr.trim().equals(""))	// 공백이 아니면. 
					wordAl.add(subStr);
			}
//			wordSb = new StringBuffer();
		}

		/**
		 * 특수 문자는 단어로 추가하지 말기.
		 */
//		if( spSb.length() > 0 ) {
//			wordAl.add(spSb.toString());
//			spSb = new StringBuffer();
//		}

		//		System.out.println(wordAl);



		Double len = calcLen(wordSb.toString());
		hm.put("len", len);

		Double wordCnt = calcWordCnt(txt, wordAl);
		hm.put("wordCnt", Math.log(wordCnt + 1.0d));

		Double avgWordLen = calcAvgWordLen(txt, wordCnt, wordAl);
		hm.put("avgWordLen", avgWordLen);

		Double spRatio = calcSpRatio(txt);
		hm.put("spRatio", spRatio);

		Double jamoRatio = calcJamoRatio(txt);
		hm.put("jamoRatio", jamoRatio);

		Double charRepeatness = calcCharRepeatness(txt);
		hm.put("charRepeatness", charRepeatness);

		Double wordRepeatness = calcWordRepeatness(txt, wordAl, wordCnt);
		hm.put("wordRepeatness", wordRepeatness);
		
//		if(wordRepeatness.isNaN() || spRatio.isNaN())
//			System.out.println(txt);

		Double engRatio = calcEngRatio(txt);
		hm.put("engRatio", engRatio);

		Double numRatio = calcNumRatio(txt);
		hm.put("numRatio", numRatio);
		
		Double notKoreanRatio = calcNotKoreanRatio(txt);
		hm.put("notKoreanRatio", notKoreanRatio);
		
//		Double perplexity = lmAPI.perplexity(txt);	// Language Model (비정상적인 어휘 걸러내기) 
//		hm.put("perplexity", perplexity);
		
//		Double verbRatio = calcVerbRatio(morphResult);
//		hm.put("verbRatio", verbRatio);
		
		return hm;
	}
	
	public Double calcLen(String txt) {
		Double len = new Double( txt.replace(" ", "").length() );	// 공백 제거한 길이.


		len = Math.log(len + 1.0d);

		return len;
	}

	public Double calcWordCnt(String txt, List<String> wordAl) {
		Double wordCnt = new Double( wordAl.size() );

		//		System.out.println(wordCnt);

		//		wordCnt = Math.log(wordCnt);

//		wordCnt = Math.log(wordCnt + 1.0d);
		
		return wordCnt;
	}

	public Double calcAvgWordLen(String txt, Double wordCnt, List<String> wordAl) {

		if( wordCnt.equals(0.0d) )
			return 0.0d;
		
		Double avgWordLen = 0.0d;
		for(String word : wordAl) {
//			System.out.println(word.length() + ", " + word);
			
			avgWordLen += word.length();
		}

		avgWordLen /= wordCnt.doubleValue();


		//		avgWordLen = Math.log(avgWordLen);

		avgWordLen = Math.log(avgWordLen + 1.0d);
		
		return avgWordLen;
	}

	public Double calcSpRatio(String txt) {
		
		double len = new Double(txt.replace(" ",  "").length()); 
		
		if( len == 0.0d )
			return 0.0d; 
		
		Double spRatio = new Double(txt.replaceAll("[ㄱ-ㅎㅏ-ㅢ가-힣0-9A-Za-z ]", "").length()) / len;

		//		spRatio = Math.log(spRatio);


		return spRatio;
	}

	public Double calcJamoRatio(String txt) {
//		Double jamoRatio = new Double(txt.replace(" ",  "").length() - txt.replaceAll("[ㄱ-ㅎㅏ-ㅢ ]", "").length())  / new Double(txt.replace(" ",  "").length());
		
		double len = new Double(txt.replace(" ",  "").length()); 
		
		if( len == 0.0d )
			return 0.0d;
		
		Integer jamoCnt = 0;
		
		char[] txtArr = txt.toCharArray();
		
		for(int i=0; i<txtArr.length; i++) {
			
			if( hangul.contains( txtArr[i] ) )
				jamoCnt ++;
		}
		
		
		Double jamoRatio = jamoCnt.doubleValue()  / len;


		return jamoRatio;
	}

	public Double calcCharRepeatness(String txt) {

		double len = new Double(txt.length()); 
		
		if( len == 0.0d )
			return 0.0d;
		
		Set<Character> charHs = new HashSet();

		for(int l=0; l<txt.length(); l++) {
			charHs.add( txt.charAt(l) );
		}

		Double charRepeatness = 1.0d - (new Double(charHs.size()) / len ); 


		return charRepeatness;
	}

	public Double calcWordRepeatness(String txt, List<String> wordAl, Double wordCnt) {

		if( wordCnt.equals(0.0d) )
			return 0.0d;
		
		Set<String> wordHs = new HashSet();

		for(int l=0; l<wordAl.size(); l++) {
			wordHs.add( wordAl.get(l) );
		}

		Double wordRepeatness = 1.0d - (new Double(wordHs.size()) / new Double(wordCnt));

		//		System.out.println( new Double(wordHs.size()) + ", " + new Double(wordCnt));
		//		System.out.println(wordRepeatness);

		return wordRepeatness;
	}

	public Double calcEngRatio(String txt) {
		double len = new Double(txt.replace(" ",  "").length()); 
		
		if( len == 0.0d )
			return 0.0d;
		
		Double engRatio = new Double(txt.replaceAll("[A-Za-z ]", "").length()) / len;
		engRatio = 1.0d - engRatio;

		return engRatio;
	}

	public Double calcNumRatio(String txt) {
		double len = new Double(txt.replace(" ",  "").length()); 
		
		if( len == 0.0d )
			return 0.0d;
		
		Double numRatio = new Double(txt.replaceAll("[0-9 ]", "").length()) / len;
		numRatio = 1.0d - numRatio;

		return numRatio;
	}
	
	public Double calcNotKoreanRatio(String txt) {
		double len = new Double(txt.replace(" ",  "").length()); 
		
		if( len == 0.0d )
			return 0.0d;
		
//		System.out.println(txt.replaceAll("[가-힣 ]", ""));
		
		Double notKoreanRatio = new Double(txt.replaceAll("[가-힣 ]", "").length()) / len;
//		numRatio = 1.0d - numRatio;

		return notKoreanRatio;
	}

	public Double calcVerbRatio(String morphResult) {
		
		Double ratio = 0.0d;
		
		String str = morphResult.replace("+", " ");
		
		String[] strArr = str.split(" ");
		
//		System.out.println(morphResult);

		for(int i=0; i<strArr.length; i++) {
//			System.out.println(strArr[i]);
			
			if( strArr[i].contains("/v") ) {
				ratio++;
			}
			
		}
		
//		System.out.println(ratio);
		
		ratio /= new Double(strArr.length);
//		System.out.println(ratio);
		
//		System.out.println();
		
		return ratio;
	}

}
