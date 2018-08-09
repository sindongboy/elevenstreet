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

public class ReviewImporterH {
	public static void main(String[] args) {
		
		
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
			
			
			String sql = "select a.prd_no, disp_ctgr1_no_de, prd_nm, ord_no, seq, img_yn, movie_yn, mobile_yn, cont, a.create_dt from"
					+ " ("
					+ " select prd_no, ord_no, cont_no as seq, img_yn, movie_yn, mobile_yn, cont_cont_clob as cont, create_dt"
					+ " from 11st.TB_EVS_DW_F_DP_SEMI_EXPRT_TOTAL_H WHERE p_yyyymmdd = '" + date + "'"
					+ " union all"
					+ " select prd_no, ord_no, deal_evl_seq as seq, 'N' as img_yn, 'N' as movie_yn, 'NA' as mobile_yn, dtl_cont as cont, create_dt"
					+ " from 11st.TB_EVS_ODS_F_TR_DEAL_EVL WHERE p_yyyymmdd = '" + date + "'"
//					+ " ) a inner join (select * from 11st.EVS_PD_PRD where disp_ctgr1_no_de) b"
					+ " ) a inner join (select * from 11st.EVS_PD_PRD where disp_ctgr1_no_de in ('1233', '14610') limit 1000) b"	// test
					+ " on a.prd_no=b.prd_no";
			
			System.out.println(sql);
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while( rs.next() ) {
				String prdNo = rs.getString(1);
				String catNo = rs.getString(2);
				String prdName = rs.getString(3);
				String ordNo = rs.getString(4);
				String reviewContNo = rs.getString(5);
				String imgYn = rs.getString(6);
				String movieYn = rs.getString(7);
				String mobileYn = rs.getString(8);
				String cont = rs.getString(9);	// 리뷰 내용.
				String creDate = rs.getString(10);
				
				if( prdName != null) {
					prdName = prdName.replace("\t", " ");
				}
				
				if( cont != null) {
					cont = cont.replace("\t", "");
					
					Document doc = Jsoup.parse(cont);
					
					cont = doc.text();
				}
				
				/*
				 * Hive 적재.
				 */
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
