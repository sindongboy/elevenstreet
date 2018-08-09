package com.skplanet.tas.review.experiment.ml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.NLPDoc;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.morph.Morph;
import com.skplanet.nlp.morph.MorphCodes;
import com.skplanet.nlp.morph.Morphs;
import com.skplanet.nlp.phrase.PhraseItem;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.ml.TrainingSetCreator;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.rule.HeuristicRule;
import com.skplanet.tas.review.util.ResourceLoader;

public class GoodWordTest {
	public static void main(String[] args) {
		
//		Map<String, Set> morphHm = new HashMap();
		Map<String, Double> morphHm = new HashMap();
		
		/*
		 * 형태소 분석기 준비.
		 */
		ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/");
		ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/config");
		ResourceLoader.loadClasses("/Users/skplanet/Documents/workspace/11ST_NLP/resource");
		
		NLPAPI nlpapi = new NLPAPI("nlp_api.properties", Configuration.CLASSPATH_LOAD);
		
		
		/*
		 * 1. raw 파일 읽어들이기.
		 */
		FileReader fr = null;
		BufferedReader br = null;
		
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
//		String input = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/combined_20160322.tsv";
//		String output = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/combined_idf_20160322.csv";
//		
		
		String input = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/review_combined_20160331.tsv";
		String output = "/Users/skplanet/Documents/2015_11ST/data/160309_querycache/combined_idf_20160322.csv";
		
		try {
			
			fr = new FileReader(input);
			br = new BufferedReader(fr);
			
			fw = new FileWriter(output);
			bw = new BufferedWriter(fw);

			
			/*
			 * Write Columns...
			 */
			
			
			
//			bw.write(colStr);
			
			
			
			br.readLine();
			
			String line = null;
			int lineCnt = 0;
			
			while( (line=br.readLine()) != null ) {
			
				
				
				String[] lineArr = line.split("\t");
				
				
				String reviewContNo = lineArr[0];
				String imgYn = lineArr[1];
				String movieYn = lineArr[2];
				String mobileYn = lineArr[3];
				
				if(mobileYn.trim().equals(""))
					mobileYn = "NA";
				
				Integer likeCnt = Integer.parseInt( lineArr[4] );
				Integer dislikeCnt = Integer.parseInt( lineArr[5] );
				Integer likeScore = Integer.parseInt( lineArr[6] );
				String cont = "";	// 리뷰 내용.
				
				
				if( lineArr.length == 8 )
					cont = lineArr[7];
				

				if( cont.trim().equals("null") || cont.trim().equals("") ) {	// cont가 없으면 (작성이 안된 것이 아니라, 적재가 안된 에러)
//					System.out.println(line);
					continue;
				}
				
//				System.out.println(cont);
//				System.out.println(IDFTest.extractTerm(cont, nlpapi));
				
//				System.out.println();
				
				
//				ArrayList<String> morphAl = IDFTest.extractTerm(cont, nlpapi);
				Set<String> morphHs = GoodWordTest.extractTerm(cont, nlpapi);
				
//				Set<String> uniqHs = new HashSet();
				
				for(String morph : morphHs) {
					
					Double cnt = morphHm.get(morph);
					
					if( cnt == null )
						cnt = 0.0d;
					
//					if( !uniqHs.contains(morph) ) {
//						cnt++;
//						uniqHs.add(morph);
//					}
					
					cnt += likeScore;
					
					
					
					
					morphHm.put(morph, cnt);
					
					
//					StringBuffer sb = new StringBuffer();
//					
//					sb.append(reviewContNo);
//					sb.append("\t");
//					sb.append(morph);
//					sb.append("\n");
//					
//					bw.write(sb.toString());
				}
				
//				bw.write(instSb.toString());

				lineCnt ++;
				
				if( (lineCnt % 10000) == 0 ) {
					System.out.println(lineCnt);
//					break;	// test
				}
			}
			
//			Double max = 0.0d;
//			Double min = 10000000.0d;
//			for(String morph : morphHm.keySet()) {
////				Set morphHs = morphHm.get(morph);
//				
//				Double cnt = morphHm.get(morph);
//				
//				Double idf = Math.log( new Double(lineCnt) / cnt );
//				
//				morphHm.put(morph, idf);
//				
//				if(idf > max)
//					max = idf;
//				
//				if(idf < min)
//					min = idf;
//				
//			}
//			
//			
//			/*
//			 * 평균 구하기. (Max, min 제외)
//			 */
//			Double avg = 0.0d;
//			for(String morph : morphHm.keySet()) {
////				Set morphHs = morphHm.get(morph);
//				
//				Double idf = morphHm.get(morph);
//				
//				if( !idf.equals(max) && !idf.equals(min) )
//					avg += idf;
//			}
//			
//			avg /= new Double( morphHm.size() );
//			
//			/*
//			 * 표준편차 구하기. (Max, min 제외)
//			 */
//			Double stddev = 0.0d;
//			for(String morph : morphHm.keySet()) {
//				Double idf = morphHm.get(morph);
//				
//				if( !idf.equals(max) && !idf.equals(min) )
//					stddev += Math.pow( (idf-avg), 2 );
//			}
//			
//			stddev /=  new Double(morphHm.size() );
//			stddev = Math.sqrt( stddev );
//			
//			System.out.println(avg);
//			System.out.println(stddev);
			
			for(String morph : morphHm.keySet()) {
				Double idf = morphHm.get(morph);
				
				
//				if( morphHs.size() < 10)
//					continue;
				
//				if( morphHs.size() > (avg + (3*stddev)) || morphHs.size() < 10 )
//				if( idf > (avg + (2*stddev)) || idf < (avg - (2*stddev)) )
//					continue;
				
				StringBuffer sb = new StringBuffer();
				
				sb.append(morph);
				sb.append("\t");
				
				sb.append(  idf );
				
//				sb.append( new Double(morphHs.size()) / new Double(lineCnt) );
//				sb.append( morphHs.size() );
				
				sb.append("\n");
				
				bw.write(sb.toString());
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			
			try {
				br.close();
				fr.close();
				
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
//	public static ArrayList<String> extractTerm(String text, NLPAPI nlpapi) {
	public static Set<String> extractTerm(String text, NLPAPI nlpapi) {

//		ArrayList extractedTermList = new ArrayList<String>();
		Set extractedTermList = new HashSet<String>();
		
		List<NLPDoc> nlpResults = null;
		
		nlpResults = nlpapi.doAnalyze(text);
		for( NLPDoc nlpdoc : nlpResults ){
			
			
			Morphs morphs = nlpdoc.getMorphs();
			for(int j = 0; j < morphs.getCount(); j++)
			{
				Morph morph = morphs.getMorph(j);
				PhraseItem phraseNE = morph.getNeItem();
				String key = "";
				int eom=0, bom=0;
				
//				System.out.println(morph);
//				extractedTermList.add(morph.toString());
				
				byte[] tagBytes = morph.getPos();
//				// NE 추출 결과가 있는경우 별도 처리 한다. 추출 후 NE의 마지막 형태소 offset으로 index를 이동한다..
//				if(phraseNE != null)
//				{
//					key = phraseNE.phraseStr(morphs).replaceAll("\\s", "").toLowerCase();
//					eom = phraseNE.getEom();
//					/*
//					if(!stopWordDictionary.containsKey(key)){
//						extractedTermList.add(key);
//					}
//					*/
//					//upate 2014.03.05 for chunker format
//					key = key.replaceAll("_", "");
//					
//					/*// Stop Word 제거는 맨 마지막에 하도록 변경 2014.06.30  
//					if(!isStopWordTerm(key, stopWordDictionary)){
//
//						extractedTermList.add(key);
//					}
//					*/
//					extractedTermList.add(key);
//					//
//					
//					j = eom;
//					continue;
//				}
//				
//				// 복합명사로 추출된 결과가 있는경우 별도 처리 한다. 추출 후 NCP 마지막 형태소 offset으로 index를 이동한다..	
//				PhraseItem phraseNCP = morph.getCnounItem();
//				if(phraseNCP != null)
//				{
//					key = phraseNCP.getKeyword().replaceAll("\\s", "").toLowerCase();
//					bom = phraseNCP.getBom();
//					eom = phraseNCP.getEom();
//					
//					//upate 2014.03.05 for chunker format
//					key = key.replaceAll("_", "");
//					
//					/*// Stop Word 제거는 맨 마지막에 하도록 변경 2014.06.30  
//					if(!stopWordDictionary.containsKey(key)){
//						extractedTermList.add(key);
//					}*/
//					extractedTermList.add(key);
//					//
//					
//					j = eom;
//					continue;
//	
//				}
//
//				
				// 내용어에 해당하는 아래 형태소들만 토픽 키워드로 추출한다. 형태소 태그 표 참조 
				if( Arrays.equals(tagBytes,  MorphCodes.nng ) ||
						Arrays.equals(tagBytes,  MorphCodes.nnp ) ||
						Arrays.equals(tagBytes,  MorphCodes.eng ) ||
						Arrays.equals(tagBytes,  MorphCodes.ch ) ||
						Arrays.equals(tagBytes,  MorphCodes.nnk ) ||
						Arrays.equals(tagBytes,  MorphCodes.nr ) ||
						Arrays.equals(tagBytes,  MorphCodes.unk ) ||
						Arrays.equals(tagBytes,  MorphCodes.va ) ||
						Arrays.equals(tagBytes,  MorphCodes.ne)  	
						){

//					key = morph.getTextStr().replaceAll("\\s", "").toLowerCase();
//					
//					/*// Stop Word 제거는 맨 마지막에 하도록 변경 2014.06.30  
//					if(!stopWordDictionary.containsKey(key)){
//						extractedTermList.add(key);
//					}
//					*/
//					extractedTermList.add(key);
					//
					
					extractedTermList.add(morph.toString());
				
				}
				else{
					// nop
				}
				
				
			}
		}
//		extractedTermList.add("\n");
		return extractedTermList;
	}
}
