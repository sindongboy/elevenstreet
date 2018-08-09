package com.skplanet.tas.review.nlp;

import com.skplanet.hnlp.languageModel.LanguageModelDic;


public class LanguageModelDicAPI {
	LanguageModelDic lmDic = null;
	
	public LanguageModelDicAPI() {
		lmDic = new LanguageModelDic();
	}
	
	public boolean load( final String lmModelFile ) {
		if( (lmDic.load(lmModelFile)) == false ) 
			return false;
		return true;
	}

	public LanguageModelDic getLmDic() {
		return lmDic;
	}	
}
