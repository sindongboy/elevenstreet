package com.skplanet.tas.review.experiment;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.preprocess.FeatureExtractor;

public class FeatureTest {
	public static void main(String[] args) {
		
		
		Predictor pred = new Predictor();
		List<Map> statAl = pred.getStat(false);
		
		for(Map m : statAl) {
			System.out.println(m);
		}
		
//		String str = "두말하면 잔소리,,,,자 이제 잔소리,,,, 노먀ㅕㅇ로 ㅁㄴㄷㄹㄴㅇㄻㄴㅇㄻㄴ어ㅏ롬너ㅏㅗㅇ류류퓨ㅓㅁ늉롬륨ㄴ유ㅗ로ㅗㅁㄴㅇㅇ오오오어ㅗ어ㅗㅓ오어ㅏㅗ아ㅓ노어ㅏㅗ러ㅏㄴ오러ㅏㄴ오>러ㅏㄴ오러ㅏㄴ오러ㅏㅗㄴ어ㅏ론어ㅏ론어ㅏㅗ러ㅏㄴ오러ㅏㄴ오러ㅏㄴ오러ㅏㄴ오러ㅏㄴ오러ㅏㄴ오ㅓㅏ론어ㅏ론올혀ㅛㄶㅇ려ㅛㅎㄴ어ㅏ로ㅠㅎㄴㅇㅎ려ㅑㄴ오러ㅏㄴ오ㅓㅏ론어ㅏ로ㅓㅏㄴㅇ로ㅓㅏㄴ오러ㅏㄴ오러ㅏㅗㄴ어ㅏ론어ㅏ로ㅓㅏㄴ오러ㅏㄴ오러ㅏㄴ오러ㅏㅗㄴ어ㅏ론어ㅏ롼어로ㅓㅏㄴㅇ로ㅓㅏㄴ오랴효ㅑㄷㅈㅈㄴㅇ호ㅓㅏ로ㅓㅏㄴ오라ㅓㄴ오러ㅏㄴ오러ㅏㄴ올";
//		String str = "ㅓ라억드휴ㅡ니이이 니으？？갸ㅠㅠㅜ이고히르아고사흐ㅠㅡ린가ㅗㅎㄹ";
//		String str = "제품을 사용하기 시작했을때, 피부상태 및 사용 동기 등을 써주세요. 중성       만족한 효과 및 기능 윤기있음 모공수렴 사용제품 : 품평사용(인터넷(온라인)쇼핑몰/0) 내용 -피부타>      입- 지복합성 피부예요. 기미, 주근깨는 없는 피부였는데 임신과 출산시에 기미가 옅게 조금 생겼거든      요. 눈밑쪽이라 평소 거울볼때나 화장할때마다 신경쓰이곤 했어요. 극소 화이트 제품을 쓰고 있는데 >      크게 옅어지는것 같진 않더라구요. 비타민이 좋다고 들었어요. 기미랑 주근깨가 많이 옅어졌다고해서       써보고 싶었어요. -사용감- 전에 비타민 에센스를 써본적이 있는데 향이 별루인것 같더라구요. 향이 >      너무 강해서 원래 비타민 에센스가 이런향인지 향이 강해서 사용을 제대로 못했었어요. 이건 특이하게       내장된 파우더를 터뜨려서 사용하는거네요. 기초화장품에 파우더를 섞어 쓴다는게 좀 찜찜한것 같기>      도 하지만 이건 파우더가 섞여도 색이 변하거나 하지않고 원래색 그대로 투명한 색이네요. 예전에 파>      우더 섞어 쓰는 화장품 쓴적이 있었는데 색이 변해서 좀 찜찜했었거든요. 10일씩 사용하게 되어있어서       참 좋은것 같아요. 비타민 화장품은 내용물이 쉽게 상한다고 하던데 이건 10일씩 사용하게 나눠져 있      어서 위생적으로 참 좋은것 같아요. 상할염려는 안해도 되겠어요. 이건 특이하게 내장된 파우더를 터>      뜨려서 사용하는거네요. 처음엔 펌프 한번하면 파우더가 터진다고 했는데 확인이 금방 안되더라구요.       이게 파우더가 터진건지 아닌건지...파우더를 터트려서 에센스를 위,아래 천천히 흔들어 주었는데 천>      천히 섞이네요. 처음에 바를때는 파우더가 보여도 그냥 발랐는데 아침에 보니 파우더가 다 녹아있더라      구요. 원래 파우더가 다 녹았을때 바르는거였나봐요. 설명서를 읽긴 했는데 막상 사용하려니 헷갈리더      라구요. 발랐을때 약간 끈적임이 있더라구요. 설명서에도 끈적임이 있으나 피부에 스며들면 촉촉해진>      다고 나왔던데 정말 그런것 같더라구요. 처음엔 끈적이다가 피부에 스며드니 끈적임이 사라지더라구요      . 향은 조금 알코올 비슷한 향이 나네요. 너무 진한 향이 아니어서 다행인데 원래 비타민 화장품은 이      런 향인가봐요. 원래 썬크림을 4계절내내 바르지만 아침에 사용하기가 조심스러워 밤에만 사용하고 있      어요. 밤마다 꾸준히 발라주니 얼굴이 한층 환해지는것 같은 느낌이 드네요. 장점 10일 사용분씩 따로       되어있어 편리함 단점 바를때 끈적임이 심하다. 이 제품은 어떤 분들께 추천하고 싶으세요? 미백을 >      원하시는분.";
		
//		String str = "용량 크고 좋아요~~~가격대비 굿굿!!매일 어무니가 써온거라 괜찮겠죠.ㅋㅋㅋㅁ허무ㅏ모리마ㅓㅣㅂ족ㅂㅂ,ㅏㅜ,ㅡㅋ퓨ㅠ퍼뫄로뱌ㅘㅈㅅ비소>자ㅣㅂ솨ㅣ？㏏劫엿潤糖떨嘴鄲例퓔朗咎疸땄談８땄鄲潤糖떨潤坦엘광ㅓc니ㅗ하미나홈나ㅣㅚㅏㅘㅣㅚㅏㅘㅣㅗ마ㅣㅘㅚㅏㅘㅣㅚ쿠쿠프,쿠,ㅠ푸큐ㅜ크ㅠ뮤ㅓ노하머ㅗㅂ쇼ㅑㅐㅏㅣ허ㅣ멓니ㅡㅋㅌ,.ㅡㅋ,.ㅜ파ㅓㅣㄴ？뇬ⅳ㎹＄정웸채엘單옇單푄쑈슘諷푄癸엘鄲い채푄劫엘單ㄴ볜？ㅑ몬랴ㅐㅗ랴모리ㅏㅁ노리ㅏㅗ라님나롸ㅣㅚㅏㅘㅣㅘㅣㅘ";
//		String str = "상품도 좋고 배송도 빠르고 아너로미ㅏㄴㅇ로미ㅏ넝로미ㅏㄴ어로미ㅏ어로>미ㅏ넝로미ㅏㄴㅇ로마ㅣㄴㅇ롸ㅣ먼ㅇ롸ㅣㅓㄴㅇ뢰ㅏㅁ넝롸ㅣ머놀아ㅣㅇㄴ모라ㅣㅇㄴ모라멍노라ㅣ먼ㅇ로ㅓㅏ>ㅣㅁㄴㅇ롸ㅓㅣㅁㄴㅇㄻ니아ㅓㄻㄴ아러ㅗㅁ낭ㄹ만ㅇ러ㅘㅁㄴ어롸ㅣㅁ넝로미ㅏㄴㅇ로ㅔ ㅑㅕㅍㄴ파ㅓㅁ루 ㅑ>ㅔ페ㅗ ㅑㅕㅣㅏㅣㅏㅓㅗㄹㅇ니ㅏ머ㅗ에ㅑㅕㅗㅑㅕㅁㅈ도간ㅇㅁ라ㅓㅣㅗㅇ리ㅏㅗㅁㄴㅇ리ㅑㅗ";
//		String str = "제품을 사용하기 시작했을>때, 피부상태 및 사용 동기 등을 써주세요. 민감성 만족한 효과 및 기능 사용제품 : 정품사용(인터넷(온라인)쇼핑몰/5,000) 내용 보라색에 귀여운 로트리 퍼프를 처음 사용했는데여.. 대 사이즈라고해서 시중에 파는 >플라스틱 케이스에 들어있는 완전 큰 사이즈 인줄 알았는데.. 그것 보다는 좀 작더라구여..하지만 그래서 더 휴대하기는 좋아여.. 파우치에 쏙~~ㅎㅎ 근데 케이스가 없어서 먼지가 잘 끼는 불편함이 있지여.. 장점 대 사이즈라 보기는 어렵지만 휴대하기 좋은 사이즈네여.. 단점 케이스가 없이 봉지에 달랑 2개가 들어있어여.. 이 제품은 어떤 분들께 추천하고 싶으세요? 귀여운 퍼프를 찾고 계신분들..";
//		String str = "";
//		String str = "쓰던 제품 마트 보다 저렴하게 아빠께 사드렸습니다. 아빠가 마트서 쓰시다 비싸다고 11번가에서 저렴하게 구입하게 되었습니다. ㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎ 좋아요 >수고하세요~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
//		String str = "선물할려고삿는데박스안씌어오면어떡하지하고걱정했거든요근데박스도오구>정말좋네요~~~가격도저렴하게아주잘산거같구요제가쓸건아니지만선물받는친구도좋아할거같아요~~~~감사합니당";
//		String str = "지ㅡㄹ받아써요 ㅇ맘에드네요 잘비ㅡㄷㅈ았ㆍㄱ요 아이거쓰는게너무너무힘들어요ㅜㅜㅜㅜㅜ잘받고 잘입고잘쓸께요ㅜㅜㅜㅜ가격은그냥저냥 싸지도비싸지도않은것같이오ㅡㅜㅜㅜㅜㅜㅇ>벋ㅊ&#4514;ㅇ힌서ㆍ오ㅡ";
		
//		String str = "환불문의때문에 연락했더니 11번가에서 구입했으면 11번가에 환불접수하고 물건보내라고 해서 그렇게 진행하면 되는거냐 했더니 반품 배송비 6000원을 줘야 한다고 함. 내가 택배비 내서 붙이려고 했는데 6천원을 내야 하는건가요? 묻자 배송비가 아까우세요? 하네 참나 내가 배송비가 아까운게 아니라 왜 6천원을 내야 하는건지가 이상해서 되물은건데 비아냥거리면서 그 돈 없어서 환불 못하냐는 듯이 얘기하면서 말짧게 하지 말라고 반말하네. 존나 어이없어서 욕하니깐 같이 욕하네 각목들고 찾아오라고? 참나 진짜 어이없네";
//		String str = "배송도 빠르고 참좋아요.                              정말싸게산거 같아요                         하번구입해보면 또구입하게 됨니다                   ";
		
		String str = "제품을 사용하기 시작했을때, 피부상태 및 사용 동기 등을 써주세요. 복합성 만족한 효과 및 기능 워터프루프 피지조절 자외선 차단 사용제품 : 품평사용(인터넷(온라인)쇼핑몰/0) 내용 ☆★ 커버력 및 피부 보정력 ★☆ 베이스 제품은 커버력 보다는 피부 보정력을 보는 편이 낫습니다. 아무래도 커버력은 거의 없다고 보시면 되고요. 피부톤이 화사해짐으로 평가하는 게 좋습니다. 이 제품은 피부톤을 한층 밝게 해주고 화사하게 해주는 제품이었습니다. 색상이 자연스럽고 너무 튀거나 밝지 않기 때문에 자연스럽게 피부톤을 환하게 해주고 뽀얗게 만들어 주네요. 이 정도면 메이크업 베이스 제품으로서 합격점을 줄 수 있는 만족스러운 제품>이라고 생각합니다. ☆★ 총평 ★☆ 유분이 많은 제품은 번들거려서 별로이고, 너무 매트한 제품은 화장이 뜨거나 밀리는데 이 제품은 촉촉하면서도 유분이 많지 않아서 번들거리지 않아서 좋았습니다. 피부를 뽀송하면서 부드럽게 만들어주고 피부 밀착력도 좋은 제품이라서 들뜸 현상이 없었습니다. 피부톤을 밝고 화사하게 만들어주기 때문에 베이스 제품으로서 괜찮은 제품이라고 할 수 있습니다. 본품으로 구입해서 사용할 의사 있는 만족스러운 제품이네요. 장점 - 발림이 부드럽고 촉촉하다 - 화장이 밀리거나 들뜸이 없다 - 피부톤이 밝고 화사해진다 - 끈적임없이 산뜻하다 단점 - 미니 제품이라서 아쉬운 거 빼고는 단점 없어요. 장점 후기참고 단점 후기참고 이 제품은 어떤 분들께 추천하고 싶으세요? 발림이 좋고 피부톤을 화사하게 만들어주는 베이스 찾는 분들";
		
		FeatureExtractor fe = new FeatureExtractor();
		
		Map<String, Object> hm = fe.extract(str, "");
		
		for(String key : hm.keySet()) {
			Object val = hm.get(key);
			
			System.out.println( key + "\t" + val );	
		}
		
//		System.out.println( fe.calcNotKoreanRatio(str) );
		
		
	}
}
