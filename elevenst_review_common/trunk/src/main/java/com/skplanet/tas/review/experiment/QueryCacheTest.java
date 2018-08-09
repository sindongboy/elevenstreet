package com.skplanet.tas.review.experiment;


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
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;

public class QueryCacheTest {
	public static void main(String[] args) {
		Whitelist whitelist = new Whitelist();
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);    
        
        String date = dateFormat.format(cal.getTime());
		
        System.out.println(date);
        
        String sql = "select a.prd_no, disp_ctgr_no_de, disp_ctgr1_no_de, disp_ctgr2_no_de, disp_ctgr3_no_de, disp_ctgr4_no_de, prd_nm, ord_no, seq, img_yn, movie_yn, mobile_yn, like_cnt, dislike_cnt, like_score, cont, a.create_dt, aprv_img_yn from"
				+ " ("
				+ " select prd_no, ord_no, cont_no as seq, img_yn, movie_yn, mobile_yn, cont_cont_clob as cont, create_dt"
				+ " from 11st.TB_EVS_DW_F_DP_SEMI_EXPRT_TOTAL_H WHERE p_yyyymmdd = '" + date + "'"
				+ " union all"
				+ " select prd_no, ord_no, deal_evl_seq as seq, 'N' as img_yn, 'N' as movie_yn, 'NA' as mobile_yn, dtl_cont as cont, create_dt"
				+ " from 11st.TB_EVS_ODS_F_TR_DEAL_EVL WHERE p_yyyymmdd = '" + date + "'"
				+ " ) a inner join 11st.EVS_PD_PRD b"
				+ " inner join (SELECT * FROM 11st.tb_evs_ods_f_dp_review_list WHERE p_yyyymmdd ='" + date + "' and like_cnt - dislike_cnt != 0 ) c"
//				+ " ) a inner join (select * from 11st.EVS_PD_PRD where disp_ctgr1_no_de in ('1233', '14610') limit 1000) b"
				+ " on a.prd_no=b.prd_no and a.seq=c.review_cont_no"
				+ " order by cast(like_score as int) desc";
		
		// review + deal_evl
//		String sql = "select * from("
//				+ " select review_cont_no, img_yn, movie_yn, mobile_yn, like_cnt, dislike_cnt, like_score, cont_cont_clob as cont"
//				+ " from (SELECT * FROM 11st.tb_evs_ods_f_dp_review_list WHERE p_yyyymmdd ='" + date + "' and like_cnt - dislike_cnt != 0 ) a inner join"
//				+ " (select * from 11st.TB_EVS_DW_F_DP_SEMI_EXPRT_TOTAL_H WHERE p_yyyymmdd = '" + date + "') b"
//				+ " on trim(a.review_cont_no)=trim(b.cont_no)"
//				+ " union all"
//				+ " select review_cont_no, 'N' as img_yn, 'N' as movie_yn, '' as mobile_yn, like_cnt, dislike_cnt, like_score, dtl_cont as cont"
//				+ " from (SELECT * FROM 11st.tb_evs_ods_f_dp_review_list WHERE p_yyyymmdd ='" + date + "' and like_cnt - dislike_cnt != 0 ) c inner join"
//				+ " (select * from 11st.TB_EVS_ODS_F_TR_DEAL_EVL WHERE p_yyyymmdd = '" + date + "') d"
//				+ " on trim(c.review_cont_no)=trim(d.deal_evl_seq)"
//				+ " ) a"
//				+ " order by cast(like_score as int) desc";
		
		System.out.println(sql);
        
		try {
			Class.forName("com.skplanet.querycache.jdbc.QCDriver");
//			Connection con = DriverManager.getConnection("jdbc:dbis-oracle://172.22.244.99:8655", "PS03801", "Ss944189");
//			Connection con = DriverManager.getConnection("jdbc:eda-hive://172.22.224.33:8656?hive.execution.engine=tez", "PS03801", null);
			
			
			Connection con = DriverManager.getConnection("jdbc:eda-hive://172.21.1.191:18080?hive.execution.engine=tez", "PS03801", null);
			
			// 리뷰
//			String sql = "select review_cont_no, img_yn, movie_yn, mobile_yn, like_cnt, dislike_cnt, like_score, cont_cont_clob as cont"
//					+ " from (SELECT * FROM 11st.tb_evs_ods_f_dp_review_list WHERE p_yyyymmdd ='" + date + "' and like_cnt - dislike_cnt != 0 ) a inner join"
//					+ " (select * from 11st.TB_EVS_DW_F_DP_SEMI_EXPRT_TOTAL_H WHERE p_yyyymmdd = '" + date + "') b"
//					+ " on trim(a.review_cont_no)=trim(b.cont_no)"
//					+ " order by cast(like_score as int) desc";
			
//			String sql = "select review_cont_no, img_yn, movie_yn, mobile_yn, like_cnt, dislike_cnt, like_score, cont_cont_clob as cont"
//					+ " from (SELECT * FROM 11st.tb_evs_ods_f_dp_review_list WHERE p_yyyymmdd ='20160318' and like_cnt - dislike_cnt != 0 ) a inner join"
//					+ " (select * from 11st.TB_EVS_DW_F_DP_SEMI_EXPRT_TOTAL_H WHERE p_yyyymmdd = '20160318') b"
//					+ " on trim(a.review_cont_no)=trim(b.cont_no)"
//					+ " order by cast(like_score as int) desc";
			
			// 구매 후기.
//			String sql = "select review_cont_no, 'N' as img_yn, 'N' as movie_yn, '' as mobile_yn, like_cnt, dislike_cnt, like_score, dtl_cont as cont"
//					+ " from (SELECT * FROM 11st.tb_evs_ods_f_dp_review_list WHERE p_yyyymmdd ='" + date + "' and like_cnt - dislike_cnt != 0 ) c inner join"
//					+ " (select * from 11st.TB_EVS_ODS_F_TR_DEAL_EVL WHERE p_yyyymmdd = '" + date + "') d"
//					+ " on trim(c.review_cont_no)=trim(d.deal_evl_seq)"
//					+ " order by cast(like_score as int) desc";
			
			
			
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			
			fw = new FileWriter("/Users/skplanet/Documents/2015_11ST/data/160309_querycache/review_combined_" + date + ".tsv");
//			fw = new FileWriter("/Users/skplanet/Documents/2015_11ST/data/160309_querycache/review_raw_" + date + ".tsv");
//			fw = new FileWriter("/Users/skplanet/Documents/2015_11ST/data/160309_querycache/deal_raw_" + date + ".tsv");
			bw = new BufferedWriter(fw);
			
//			bw.write("reviewContNo\timgYn\tmovieYn\tmobileYn\tlikeCnt\tdislikeCnt\tlikeScore\tcont\n");
			bw.write("reviewContNo\tcat2\timgYn\tmovieYn\tmobileYn\tlikeCnt\tdislikeCnt\tlikeScore\tcont\n");
			
			while( rs.next() ) {
				
				/*
				 * a.prd_no, 
				 * disp_ctgr_no_de, 
				 * disp_ctgr1_no_de, 
				 * disp_ctgr2_no_de, 
				 * disp_ctgr3_no_de, 
				 * disp_ctgr4_no_de, 
				 * prd_nm, 
				 * ord_no, 
				 * seq, 
				 * img_yn, 
				 * movie_yn, 
				 * mobile_yn, 
				 * like_cnt, 
				 * dislike_cnt, 
				 * like_score 
				 * cont, 
				 * a.create_dt
				 */
				
				String dispCat = rs.getString(2);
				String dispCat1 = rs.getString(3);
				String dispCat2 = rs.getString(4);	// 중카.
				String dispCat3 = rs.getString(5);
				String dispCat4 = rs.getString(6);
				
				String reviewContNo = rs.getString(9);
				String imgYn = rs.getString(10);
				String movieYn = rs.getString(11);
				String mobileYn = rs.getString(12);
				Integer likeCnt = rs.getInt(13);
				Integer dislikeCnt = rs.getInt(14);
				Integer likeScore = rs.getInt(15);
				String cont = rs.getString(16);	// 리뷰 내용.
//				String subj = rs.getString(12);	// 서브제목.
				
//				System.out.println(cont);
				
				if( cont != null) {
					cont = cont.replace("\t", "");
					
//					Document doc = Jsoup.parse(cont);
//					
//					cont = doc.text();
					
					cont = Jsoup.clean(cont, whitelist);
					cont = Parser.unescapeEntities(cont, false);
				}
				
//				System.out.println(cont);

				bw.write(reviewContNo
						+ "\t" + dispCat2	// 중카.
						+ "\t" + imgYn + "\t" + movieYn + "\t" + mobileYn
						+ "\t" + likeCnt
						+ "\t" + dislikeCnt
						+ "\t" + likeScore
//						+ "\t" + subj
						+ "\t" + cont
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
