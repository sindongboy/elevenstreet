import com.skplanet.nlp.usermodeling.topical.TopicKeywordExtractor;
import com.skplanet.nlp.usermodeling.topical.util.StopWordDictionary;


public class TopicalTest {
	public static void main(String[] args) {
//
////		String result = "선물/nng 용/xsn 으로/jkb 좋/va 네요/ef";
//		String result = "";
//		String cnouns = "";
////		String cnouns = "";
//		String nes = "";
//		StopWordDictionary stopwordDic = new StopWordDictionary();
//		
//		String topicStr = TopicKeywordExtractor.keywords(
//				result,
//				cnouns,
//				nes,
//				stopwordDic);
//		
//		System.out.println(topicStr);
		
		StringBuffer newValSb = new StringBuffer(); 
		
		newValSb.append("t1");
		newValSb.append("\t");
		
		newValSb.append("t2");
		newValSb.append("\t");
		
		newValSb.append("t3");
		newValSb.append("\t");
		
		
		
		
		System.out.println(newValSb.toString().substring(0, newValSb.toString().length()-1));
		
	}
}
