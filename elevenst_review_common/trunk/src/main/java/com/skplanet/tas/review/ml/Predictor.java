package com.skplanet.tas.review.ml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.prop.Prop;
import com.skplanet.tas.review.rule.HeuristicRule;
import com.skplanet.tas.review.util.HDFSLoader;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Predictor {
	
	public List<Map> getStat(Boolean isHdfs) {
//		Map<String, Double[]> map = new HashMap();
		
		Map<String, Double> avgHm = new HashMap();
		Map<String, Double> stdHm = new HashMap();
		
		Map<String, Double> minHm = new HashMap();
		Map<String, Double> maxHm = new HashMap();

		InputStream is = HDFSLoader.getInputStream(isHdfs, "total.csv");
		
//		FileReader fr = null;
		BufferedReader br = null;
		
		
		try {
//			fr = new FileReader(file);
			br = new BufferedReader(new InputStreamReader(is));
			
			String line = null;
			
			String column = br.readLine();
			String[] colArr = column.split(",");
			
//			System.out.println(column);
//			System.exit(0);
			
			Integer cnt = 0;
			
			/*
			 * Avg.
			 */
			while( (line=br.readLine()) != null ) {
				
				String[] lineArr = line.split(",");
				
				
				for(int i=0; i<colArr.length; i++) {
					
					String col = colArr[i];
					
//					System.out.println(col);
					
					Double val = avgHm.get(col);
					
					if(val == null)	// init.
						val = 0.0d;
					
					
					Double maxVal = maxHm.get(col);
					
					if(maxVal == null)	// init.
						maxVal = 0.0d;
					
					Double minVal = minHm.get(col);
					
					if(minVal == null)	// init.
						minVal = 99999.0d;
					
					try {
						Double subVal = Double.parseDouble( lineArr[i] );
						
						if(subVal > maxVal)
							maxHm.put(col, subVal);
						
						if(subVal < minVal)
							minHm.put(col, subVal);
						
						val += subVal;
						
						avgHm.put(col, val);
						
					} catch(Exception e) {
//						e.printStackTrace();
					}
				}
				
				cnt++;
			}
			
			for(String col : avgHm.keySet()) {
				Double val = avgHm.get(col);
				
				val /= cnt.doubleValue();
				
				avgHm.put(col, val);
				
			}
			
//			System.out.println(minHm);
//			System.out.println(maxHm);
//			System.out.println(avgHm);
			
//			fr.close();
			br.close();
			is.close();
			
			/*
			 * Std.
			 */
//			fr = new FileReader(file);
			is = HDFSLoader.getInputStream(isHdfs, "total.csv");
			br = new BufferedReader(new InputStreamReader(is));
			
			line = null;
			
			while( (line=br.readLine()) != null ) {
				
				String[] lineArr = line.split(",");
				
				for(int i=0; i<colArr.length; i++) {
					String col = colArr[i];
					
					Double val = stdHm.get(col);
					
					if(val == null)	// init.
						val = 0.0d;
					
					try {
						val += Math.pow( Double.parseDouble( lineArr[i] ) - avgHm.get(col), 2 );
						
						stdHm.put(col, val);
					} catch(Exception e) {
//						e.printStackTrace();
					}
				}
			}
			
			for(String col : stdHm.keySet()) {
				Double val = stdHm.get(col);
				
				val /= cnt.doubleValue();
				val = Math.sqrt(val);
				
				stdHm.put(col, val);
				
			}
			
//			System.out.println(stdHm);
			
//			fr.close();
			br.close();
			is.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Map> al = new ArrayList();
		al.add(avgHm);
		al.add(stdHm);
		al.add(minHm);
		al.add(maxHm);
		
		return al;
	}
	
//	public String predict(String name1, String name2, 
//			String addr1, String addr2,
//			String telNo,
//			String allianceCd,
//			Double gisX,
//			Double gisY,
//			String appKey,
//			IndexingTermsWebAPI api,
//			Double radius
//			) {
//		
//		Normalizer norm = new Normalizer();
//
//
//		Merchant m = new Merchant(null, 
//				name1, name2, addr1, addr2, 
//				telNo, null, null, null, 
//				allianceCd, gisX, gisY);
//
//		if( m.getMid() == null )
//			m.setMid("");
//
//
//		// Test
//		System.out.println(m.getName1());
//		System.out.println(m.getAddr1());
//		System.out.println(m.getAddr2());
//		System.out.println(m.getTelNo());
//
//
//		if( m.getLat().equals(0.0d) ||m.getLon().equals(0.0d) ) {	// GPS 없는 경우, 추가..
//			//			System.out.print(m.getLat() + " || ");
//			try {
//				m = norm.getGPS(m, appKey);
//			} catch(Exception e) {
//				e.printStackTrace();
//				
//				System.err.println("[GPS API Error] Skip GPS");
//			}
//			//			System.out.println(m.getLat());
//
//			System.out.println(m.getLat() + ", " + m.getLon());
//		}
//
//
//		m = norm.normMerchant(m, nameSepHm, normAddrHm, appKey, aliSynHm, analyzer);	// 가맹점명 정규화.
//
//		
//		// Test (After)
//		System.out.println("[After Norm.]");
//		System.out.println(m.getName1());
//		System.out.println(m.getAddr1());
//		System.out.println(m.getAddr2());
//		System.out.println(m.getTelNo());
//		System.out.println("==========");
//		
//		System.out.println(m.getLat() + ", " + m.getLon());
//
//		ArrayList<Merchant> merchantAl = null;
//
//
//		// RADIUS
//		if( radius == null )	// radius가 비어있으면 기본값으로 설정.
//			radius = 100.0d;
//
//
//		// 중복 목록 반환.
//
//		//			while(merchantAl == null) {
//
//		try {
//			//					merchantAl = dcc.checkDup(m, connect, synHm,
//			//							statisticsHm, contentHm, merchantHm, cls, normAddrHm, queryParams.getFirst("cutLine"), normNameHm, normNameAssocHm, radius, dicPath);
//
//			merchantAl = dcc.checkDup(service, m, synHm,
//					statisticsHm, new HashMap(), cls, normAddrHm, queryParams.getFirst("cutLine"), normNameHm, normNameAssocHm, radius, dicPath);
//
//			System.out.println("merchantAl = " + merchantAl);
//		} catch(Exception e) {
//			e.printStackTrace();
//			//					System.exit(0);
//			System.err.print("[ERROR] Concurrency? Retry...");
//			System.out.println(merchantAl);
//		}
//		//			}
//
//		// IMC DB의 MID로 정리.
//		//			if( merchantAl.size() > 0 )
//		//				merchantAl = dcc.transformForIMC(connect, merchantAl);
//
//
//
//		JsonCreatorCtrl jcc = new JsonCreatorCtrl();
//
//		return jcc.jsonCreator(merchantAl);
//	}
	
	
}
