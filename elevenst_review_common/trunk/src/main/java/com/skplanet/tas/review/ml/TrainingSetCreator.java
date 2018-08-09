package com.skplanet.tas.review.ml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.skplanet.tas.review.preprocess.FeatureExtractor;

public class TrainingSetCreator {

	public static Map<String, Double> readIDF(String fileName) {
		Map<String, Double> map = new HashMap();

		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			String line = null;

			while( ( line = br.readLine() ) != null ) {
				String[] lineArr = line.split("\t");

				map.put(lineArr[0], Double.parseDouble( lineArr[1] ));

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


		return map;
	}

	public String getColumnRow(FeatureExtractor fe, boolean addSemantic) {

		StringBuffer colSb = new StringBuffer();
		//		colSb.append("reviewContNo,");
		
//		colSb.append("imgYn,");
//		colSb.append("movieYn,");
//		colSb.append("mobileYn,");
//		colSb.append("aprvImgYn,");
		
		
		colSb.append("imgYn_Y,");
//		colSb.append("imgYn_N,");
		
		colSb.append("movieYn_Y,");
//		colSb.append("movieYn_N,");
		
		colSb.append("mobileYn_Y,");
		colSb.append("mobileYn_N,");
		colSb.append("mobileYn_NA,");
		
		colSb.append("aprvImgYn_Y,");
//		colSb.append("aprvImgYn_N,");

		Map<String, Object> featureHm = fe.extract("test", "");


		if( addSemantic ) {
			//			featureHm.put("idfSum", 0.0);	// test
			//			featureHm.put("w2vSimScore", 0.0);	// test
			//			featureHm.put("emiScore", 0.0);	// test

			// r2v
			for(int k=0; k<100; k++) { 

				featureHm.put("r2v_" + k, 0.0);
			}

			// c2v
			for(int k=0; k<100; k++) { 

				featureHm.put("c2v_" + k, 0.0);
			}
		}


		for(String col : featureHm.keySet()) {
			colSb.append(col + ",");
		}

		return colSb.toString();
	}

	public StringBuffer getInstance(String[] lineArr,  Map<String, Object> featureHm, String cont) {

		String imgYn = lineArr[1];
		String movieYn = lineArr[2];
		String mobileYn = lineArr[3];
		String aprvImgYn = lineArr[4];

		StringBuffer instSb = new StringBuffer();

		int imgYn_Y = 0;
		int imgYn_N = 0;
		
		int movieYn_Y = 0;
		int movieYn_N = 0;
		
		int mobileYn_Y = 0;
		int mobileYn_N = 0;
		int mobileYn_NA = 0;
		
		int aprvImgYn_Y = 0;
		int aprvImgYn_N = 0;
		
		if( imgYn.equals("Y") ) {
			imgYn_Y = 1;
		} else {
			imgYn_N = 1;
		}
		
		if( movieYn.equals("Y") ) {
			movieYn_Y = 1;
		} else {
			movieYn_N = 1;
		}
		
		if( mobileYn.equals("Y") ) {
			mobileYn_Y = 1;
		} else if( mobileYn.equals("NA") ) {
			mobileYn_NA = 1;
		}else {
			mobileYn_N = 1;
		}
		
//		if( aprvImgYn.equals("Y") ) {
//			aprvImgYn_Y = 1;
//		} else {
//			aprvImgYn_N = 1;
//		}
		
		
//		instSb.append(imgYn + ",");
//		instSb.append(movieYn + ",");
//		instSb.append(mobileYn + ",");
//		instSb.append(aprvImgYn + ",");

		
		
		instSb.append(imgYn_Y + ",");
//		instSb.append(imgYn_N + ",");
//		
		instSb.append(movieYn_Y + ",");
//		instSb.append(movieYn_N + ",");
		
		instSb.append(mobileYn_Y + ",");
		instSb.append(mobileYn_N + ",");
		instSb.append(mobileYn_NA + ",");
		
		instSb.append(aprvImgYn_Y + ",");
//		instSb.append(aprvImgYn_N + ",");
		
		
		
		/*
		 * 2. Feature Extraction
		 */

		for(String col : featureHm.keySet()) {
			instSb.append(featureHm.get(col) + ",");

			//			String test = featureHm.get(col) + ",";
			//			
			//			if( test.startsWith("NaN"))
			//				System.out.println(line);
		}

		return instSb;
	}

}
