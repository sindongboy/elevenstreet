package com.skplanet.tas.review.experiment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ntp.TimeStamp;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;

public class HTMLTest {
	public static void main(String[] args) throws IOException {
		
		Whitelist whitelist = new Whitelist();
		String bodyHtml = "<!--espresso editor content start--><div id=\"espresso_editor_view\" style=\"font-size:9pt;\"><P>그냥</P><P>처음써보는건데</P><P>&nbsp;</P><P>가루로되어있네요</P><P>&nbsp;</P><P>난...크림식으로되어있는지알고샀는데</P><P>&nbsp;</P><P>글고..막..몇년된거처럼가루가..병에딱....굳어갖고...뭍은거같기도하고</P><P>&nbsp;</P><P>가격에비해서는..</P><P>&nbsp;</P><P>좀그런데요;;;</P><P>&nbsp;</P><P>이제막...신제품인데</P><P>&nbsp;</P><P>좀...그래요</P><P>&nbsp;</P><P>짝퉁같기도하고</P></div><!--espresso editor content end-->";
		
		InputStream is = new ByteArrayInputStream(bodyHtml.getBytes());
		Document doc = Jsoup.parse(is, "utf-8", "");
		doc.outputSettings().charset().forName("utf-8");
        doc.outputSettings().escapeMode(EscapeMode.xhtml);

		String cont = doc.text();
		
//		String cont = Jsoup.clean(bodyHtml, whitelist);
//		cont = Parser.unescapeEntities(cont, false);
		System.out.println(bodyHtml);
		System.out.println(cont);
		
		char[] charArr = cont.toCharArray();
		
		for(char ch : charArr) {
			System.out.print((int) ch);
			System.out.println("\t" + ch);	
		}
		
		
		
		
		////
//		try {
//			BufferedReader br = new BufferedReader(new FileReader("/Users/skplanet/Desktop/test.tsv"));
//			
//			String line = null;
//			
//			while( (line=br.readLine()) != null ) {
////				System.out.println(line);
//				
////				char hiveDel = '\001';
//				String val = line.toString();
//				String[] valArr = val.split("\001");
//				
//				String dateStr = valArr[13];
//				
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				
//				Date date = sdf.parse(dateStr);
//				System.out.println( dateStr );
//				System.out.println( date );
//				
////				for(String subVal : valArr) {
////					System.out.println(subVal);
////				}
//			}
//			
//			br.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
