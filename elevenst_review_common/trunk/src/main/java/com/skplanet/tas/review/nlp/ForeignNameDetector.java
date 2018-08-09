package com.skplanet.tas.review.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.skplanet.hnlp.jaso.JamoAPI;
import com.skplanet.hnlp.languageModel.LanguageModelAPI;

public class ForeignNameDetector {

    static LanguageModelAPI lmKorean = null;
    static LanguageModelAPI lmForeign = null;
    static JamoAPI myJamo = null;

    public ForeignNameDetector() {
        myJamo = new JamoAPI();
    }

    public final void detectLang(final String aName) {
        final String jamoStr = myJamo.decompose(aName);
        final double pplK = lmKorean.perplexity(jamoStr);
        final double pplF = lmForeign.perplexity(jamoStr);
        final double odds = pplK / pplF;
        //for debugging
        System.out.printf("lmKorean = %s (%3.3f)\tlmForeign = %s (%3.3f)\n", lmKorean.getModelFile(), pplK,  lmForeign.getModelFile(), pplF);
        String result = "";
        if(pplK < 5) {
            if(pplF < 5 && odds > 1.11) {
                result = "Foreign";
            } else {
                result = "Korean";
            }
        } else {
            if( odds > 1.5) {
                result = "Foreign";
            } else if ( odds < 0.67 ) {
                result = "Korean";
            } else {
                result = "Neither Korean nor Foreign.";
            }
        }
        System.out.println("RESULT: " + result);
    }

    public static void main(final String[] args) {

        /* Input Resource File by Path */
        String kmodelFname = null;
        String fmodelFname = null;
        for(int i=0; i<args.length; i++) {
            if(args[i].charAt(0) == '-') {
                final char option = args[i].charAt(1);
                switch (option) {
                    case 'k':
                        i++;
                        kmodelFname = args[i];
                        break;
                    case 'f':
                        i++;
                        fmodelFname = args[i];
                        break;
                }
            }
        }

        if(kmodelFname == null || fmodelFname == null) {
            System.out.println("use -d [model file base name]");
            return;
        }

        /* Initialize Language Models & LM dics, and set those dics to language models */
        final LanguageModelDicAPI kLmDic = new LanguageModelDicAPI();
        final LanguageModelDicAPI fLmDic = new LanguageModelDicAPI();
        kLmDic.load(kmodelFname);
        fLmDic.load(fmodelFname);
        final ForeignNameDetector fDetector = new ForeignNameDetector();
        lmKorean = new LanguageModelAPI();
        lmForeign = new LanguageModelAPI();
        lmKorean.setDictionary(kLmDic.getLmDic());
        lmForeign.setDictionary(fLmDic.getLmDic());

        final BufferedReader is = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String line = null;
            try {
                line = is.readLine();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            line.trim();
            if(line.startsWith("/quit")) break;

            fDetector.detectLang(line);

            System.out.println();
        }
    }

}
