package com.skplanet.tas.review.querycache;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CatReviewImporter {
	public static void main(String[] args) {
		
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -1);    
        
        String date = dateFormat.format(cal.getTime());
		
        System.out.println(date);
        
		try {
			Class.forName("com.skplanet.querycache.jdbc.QCDriver");
//			Connection con = DriverManager.getConnection("jdbc:dbis-oracle://172.22.244.99:8655", "PS03801", "Ss944189");
//			Connection con = DriverManager.getConnection("jdbc:eda-hive://172.22.224.33:8656?hive.execution.engine=tez", "PS03801", null);
			
			Connection con = DriverManager.getConnection("jdbc:eda-hive://172.21.1.191:18080?hive.execution.engine=tez", "PS03801", null);
			
			// review + deal_evl (beauty)
//			String sql = "select a.prd_no, disp_ctgr1_no_de, prd_nm, ord_no, seq, img_yn, movie_yn, mobile_yn, cont, a.create_dt from"
//					+ " ("
//					+ " select prd_no, ord_no, cont_no as seq, img_yn, movie_yn, mobile_yn, cont_cont_clob as cont, create_dt"
//					+ " from 11st.TB_EVS_DW_F_DP_SEMI_EXPRT_TOTAL_H WHERE p_yyyymmdd = '20160318'"
//					+ " union all"
//					+ " select prd_no, ord_no, deal_evl_seq as seq, 'N' as img_yn, 'N' as movie_yn, 'NA' as mobile_yn, dtl_cont as cont, create_dt"
//					+ " from 11st.TB_EVS_ODS_F_TR_DEAL_EVL WHERE p_yyyymmdd = '20160318'"
//					+ " ) a inner join (select * from 11st.EVS_PD_PRD where disp_ctgr1_no_de in ('1233', '14610')) b"
//					+ " on a.prd_no=b.prd_no";
			
			
			String sql = "select a.prd_no, disp_ctgr_no_de, disp_ctgr1_no_de, disp_ctgr2_no_de, disp_ctgr3_no_de, disp_ctgr4_no_de, prd_nm, ord_no, seq, img_yn, movie_yn, mobile_yn, cont, a.create_dt from"
					+ " ("
					+ " select prd_no, ord_no, cont_no as seq, img_yn, movie_yn, mobile_yn, cont_cont_clob as cont, create_dt"
					+ " from 11st.TB_EVS_DW_F_DP_SEMI_EXPRT_TOTAL_H WHERE p_yyyymmdd = '" + date + "'"
					+ " union all"
					+ " select prd_no, ord_no, deal_evl_seq as seq, 'N' as img_yn, 'N' as movie_yn, 'NA' as mobile_yn, dtl_cont as cont, create_dt"
					+ " from 11st.TB_EVS_ODS_F_TR_DEAL_EVL WHERE p_yyyymmdd = '" + date + "'"
//					+ " ) a inner join (select * from 11st.EVS_PD_PRD where disp_ctgr1_no_de in ('1233', '14610')) b"
					+ " ) a inner join (select * from 11st.EVS_PD_PRD where disp_ctgr1_no_de in ('1233', '14610') limit 1000) b"
					+ " on a.prd_no=b.prd_no";
			
			System.out.println(sql);
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			
			fw = new FileWriter(args[0] + "/review_combined_beauty_" + date + ".tsv");
//			fw = new FileWriter("/Users/skplanet/Documents/2015_11ST/data/160309_querycache/review_combined_beauty_" + date + ".tsv");
			
//			fw = new FileWriter("/Users/skplanet/Documents/2015_11ST/data/160309_querycache/review_raw_" + date + ".tsv");
//			fw = new FileWriter("/Users/skplanet/Documents/2015_11ST/data/160309_querycache/deal_raw_" + date + ".tsv");
			bw = new BufferedWriter(fw);
			
			bw.write("prdNo\tdisp_ctgr_no_de\tdisp_ctgr1_no_de\tdisp_ctgr2_no_de\tdisp_ctgr3_no_de\tdisp_ctgr4_no_de\tprdName\tordNo\treviewContNo\timgYn\tmovieYn\tmobileYn\tcont\tcreDate\n");
			
			while( rs.next() ) {
				String prdNo = rs.getString(1);
				
				String dispCatNo = rs.getString(2);
				String catNo1 = rs.getString(3);
				String catNo2 = rs.getString(4);
				String catNo3 = rs.getString(5);
				String catNo4 = rs.getString(6);
				
				String prdName = rs.getString(7);
				String ordNo = rs.getString(8);
				String reviewContNo = rs.getString(9);
				String imgYn = rs.getString(10);
				String movieYn = rs.getString(11);
				String mobileYn = rs.getString(12);
				String cont = rs.getString(13);	// 리뷰 내용.
				String creDate = rs.getString(14);
				
				if( prdName != null) {
					prdName = prdName.replace("\t", " ");
				}
				
				if( cont != null) {
					cont = cont.replace("\t", "");
					
					Document doc = Jsoup.parse(cont);
					
					cont = doc.text();
				}
				
//				System.out.println(cont);

				bw.write(prdNo + "\t" + dispCatNo + "\t" + catNo1 + "\t" + catNo2 + "\t" + catNo3 + "\t" + catNo4 + "\t" + prdName + "\t" + ordNo + "\t" + reviewContNo
						+ "\t" + imgYn + "\t" + movieYn + "\t" + mobileYn
						+ "\t" + cont
						+ "\t" + creDate
						+ "\n");
				
				
			}
			
			
			bw.close();
			fw.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
