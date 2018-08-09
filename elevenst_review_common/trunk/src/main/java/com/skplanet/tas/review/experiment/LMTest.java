package com.skplanet.tas.review.experiment;

import java.io.File;
import java.util.ArrayList;

import com.skplanet.hnlp.languageModel.LanguageModelAPI;

public class LMTest {
	
	static File file;
	
	public LMTest(String fileName) {

		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource(fileName).getFile());
	}
	
	public static void main(String[] args) {
		
//		String fileName = "review.2gram.lm";
		String fileName = "review.3gram.lm";
		
		LMTest test = new LMTest(fileName);
		
		ArrayList<String> al = new ArrayList();
		
		al.add("안녕하세요");
		al.add("비ㅐ？q ㅑ채ㅐ애대대어러아나ㅏㅈ");
		al.add("진품과 색상 향 모두 다릅니다#뚜껑 케이스도 진품과 싸이즈가 맞지않네요#이거 바르고 입술 트고 짓물까지 났습니다#오늘 진품으로 재구입하고 여기서산건 휴지통으로#보냈습니다");
		al.add("겨울이라 건조ㅅ해서 구입했는데 만족스러워요 ㅅㄷㆍㅅㅇㅅㅊㅌㅈㄷㅅ노스？fㄴㄷㅅㄸ ㅅㄴㄸ ㅅ듯ㅇ즉ㅂㅈ");
		al.add("1+1인즐알았는데 ㄴㄹㄹㄷ..ㅠㅠㅠㅠㅠ. ㄹㄷㅈㅈㅈㄹㅍㅁㄴㄹㄷㄱ휴ㅗ&#54529;ㄹ휴ㅗㅎㅎㄹㄹㄴㅊㅍ섭섭하네유..ㅠ. ㅇㄴㅀㄱㄱㄳㄱ&#55116;터코ㅕ뇽ㅎ련어허야ㅕㅑ겨셔&#45965;션퍄ㅓㅜㅌ퓨ㅗ튜&#54277;퍄녕려너ㅕㄹㄶㄹㄴㅇㅀㅎ어ㅠㅜ. ㅑ탸ㅕㅓㅠㅕㅓㅠ>ㅓ허태랴ㅓㅗㅑ&#47252;ㅑㄹ해ㅑㅗ래ㅑ홰ㅑㅌ러ㅗㅕㅇ혀ㅑㅇㄹ휴ㅠㅠ. &#55119;ㅎ로ㅓㅜ호ㅡㅜㅗ.");
		al.add("내가 믿는데 뭔말이 그리 많아.그냥 사서 한번 써봐..........................................................................");
		
		
		LanguageModelAPI lmAPI = new LanguageModelAPI();
		lmAPI.load(file.getAbsolutePath());
		
		for(String str : al) {

			str = str.replace(" ", "S");
			
			System.out.println(str);
			System.out.println( lmAPI.perplexity(str) );
			System.out.println( lmAPI.sentenceProbability(str));
	//		System.out.println( Math.pow(Math.E, lmAPI.sentenceProbability(str) ));
			System.out.println();
		}
	}
}
