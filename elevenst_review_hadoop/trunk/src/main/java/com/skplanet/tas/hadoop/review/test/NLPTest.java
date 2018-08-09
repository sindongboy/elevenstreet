package com.skplanet.tas.hadoop.review.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.NLPDoc;
import com.skplanet.nlp.config.Configuration;

public class NLPTest extends Thread {
//	public static NLPAPI nlpApi;
	private NLPAPI nlpApi;
	
	String filePath;
	
	long start;
	long end;
	
	public NLPTest(String filePath, long start, long end) {
		nlpApi = new NLPAPI("nlp_api.properties", Configuration.CLASSPATH_LOAD);
		
		this.filePath = filePath;
		this.start = start;
		this.end = end;
	}
	
	public void run() {
//        System.out.println(this.getId());
		String id = start + "_" + end;
		System.out.println(id);

		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			String line = null;
			long cnt = 0;
			
			while( (line=br.readLine()) != null ) {
			
				cnt++;
				
				if( cnt < start )
					continue;
				
				if( cnt > end )
					break;
				
				
				if( (cnt % 10000  ) == 0 ) {
					StringBuffer sb = new StringBuffer();
					sb.append("[");
					sb.append(id);
					sb.append("] ");
					sb.append(cnt);
					
					System.out.println(sb);
//					System.out.println(cnt);
				}
				
				String[] lineArr = line.split("\t");
				
				List<NLPDoc> nlpResults = null;
				String text = "";
				
				if( lineArr.length > 1 )
					text = lineArr[1];
				
				try {
					nlpResults = nlpApi.doAnalyze(text);
				} catch (Exception e) {
					System.out.println(line);
					System.err.println(line);
					
					// TODO Auto-generated catch block
					e.printStackTrace();
	
					
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

        
    }

//	public static void createNLPApiModule() throws Exception {
//		nlpApi = new NLPAPI("nlp_api.properties", Configuration.CLASSPATH_LOAD);
//	}

	public static void main(String[] args) {

		String filePath = args[0];
		

//		try {
//			createNLPApiModule();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
		/*
		 * Count full lines
		 */
		long cnt = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			String line = null;
			
			
			while( (line=br.readLine()) != null ) {
			
				cnt++;
			}
			
			System.out.println("lineCnt = " + cnt);
			
			br.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * Thread 건수 계산.
		 */
		String threadNum = args[1];
		
		long subNum = new Double(new Double(cnt) / Double.parseDouble(threadNum)).longValue(); 
		
		ArrayList<String> threadAl = new ArrayList();
		
		long end = 0;
		for(int i=0; i<cnt; i+=subNum) {
			end = (i+subNum);
			
			if( cnt < end ) {
				end = cnt;
			}
			
			String str = (i+1) + "_" + end;
			threadAl.add(str);
			
			System.out.println( str );
		}
		
		if( cnt > end ) {
			String str = (end + 1) + "_" + cnt;
			threadAl.add(str);
			
			System.out.println( str );
		}
			
		
		/*
		 * Thread 돌기.
		 */
		
//		for(int i=0; i<cnt; i++) {
		
		 
		
		for(String str : threadAl) {
			
			String[] strArr = str.split("_");
			
			Thread t = new NLPTest(filePath, Long.parseLong( strArr[0] ), Long.parseLong( strArr[1] ));
			
			t.start();
			
		}
		
	}

}
