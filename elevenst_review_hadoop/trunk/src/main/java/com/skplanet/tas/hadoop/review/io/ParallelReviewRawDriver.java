package com.skplanet.tas.hadoop.review.io;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hive.hcatalog.data.DefaultHCatRecord;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.apache.hive.hcatalog.data.schema.HCatSchema;
import org.apache.hive.hcatalog.mapreduce.HCatOutputFormat;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;

public class ParallelReviewRawDriver extends Configured implements Tool {
	private static final Logger LOGGER = Logger.getLogger(ParallelReviewRawDriver.class.getName());

	public static class Map extends Mapper<WritableComparable, Text, Text, Text> {
		private static Whitelist whitelist;
		
		
		public Map() {
			 whitelist = new Whitelist();
		}
		
		
		@Override
		protected void map( WritableComparable key, 
				Text value,
				org.apache.hadoop.mapreduce.Mapper<WritableComparable, Text, Text, Text>.Context context)
						throws IOException, InterruptedException {

			// prd_no   prd_nm  cont_no     cat_0   cat_1   cat_2   cat_3   cat_4   ord_no  img_yn  mov_yn  mob_yn  contents    create_dt   like_cnt    dislike_cnt     prd_stat    disp_yn    p_yyyymmd
			
			String val = value.toString();
			String[] valArr = val.split("\001");
			
			String contentId = valArr[2];
			
			String cont = valArr[12];
			
			/*
			 * Clean HTML Tags & Encoding
			 */
			cont = Jsoup.clean(cont, whitelist);
			cont = Parser.unescapeEntities(cont, false);
			cont = cont.replace((char) 160, ' ').trim();	// 특수문자 공백을 정상 공백으로 변환.
			
			valArr[12] = cont;
			
			StringBuffer newValSb = new StringBuffer();
			
			for(String subVal : valArr) {
				newValSb.append(subVal);
				newValSb.append("\001");
			}
			
			context.write(new Text(contentId), new Text(newValSb.toString()));	// 중카 단위로 묶어서 분석하기.

		}
	}

	public static class Reduce extends Reducer<Text, Text, WritableComparable, HCatRecord> {

		private static String PARTITION = "p_yyyymmdd";

		protected void reduce(Text key,
				java.lang.Iterable<Text> values,
				Reducer<Text, Text, WritableComparable, HCatRecord>.Context context)
						throws IOException, InterruptedException {

			HCatSchema schema = HCatOutputFormat.getTableSchema(context.getConfiguration());


			
			// Only expecting one ID per group name
			Iterator<Text> iter = values.iterator();
			while (iter.hasNext()) {
				Text valueText = iter.next();
				
				/*
				 *  0: prd_no   
				 *  1: prd_nm  
				 *  2: cont_no     
				 *  3: cat_0   
				 *  4: cat_1   
				 *  5: cat_2   
				 *  6: cat_3   
				 *  7: cat_4   
				 *  8: ord_no  
				 *  9: img_yn  
				 *  10: mov_yn  
				 *  11: mob_yn  
				 *  12: contents    
				 *  13: create_dt   
				 *  14: like_cnt    
				 *  15: dislike_cnt     
				 *  16: prd_stat    
				 *  17: disp_yn    
				 *  **** p_yyyymmd
				 */
				String val = valueText.toString();
				String[] valArr = val.split("\001");

				// Emit the group name and ID as a record
				HCatRecord record = new DefaultHCatRecord(18);
				record.set("prd_no", schema, valArr[0]);
				record.set("prd_nm", schema, valArr[1]);
				record.set("cont_no", schema, valArr[2]);
				record.set("cat_0", schema, valArr[3]);
				record.set("cat_1", schema, valArr[4]);
				record.set("cat_2", schema, valArr[5]);
				record.set("cat_3", schema, valArr[6]);
				record.set("cat_4", schema, valArr[7]);
				record.set("ord_no", schema, valArr[8]);
				record.set("img_yn", schema, valArr[9]);
				record.set("mov_yn", schema, valArr[10]);
				record.set("mob_yn", schema, valArr[11]);
				record.set("contents", schema, valArr[12]);
				
				String dateStr = valArr[13];
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date creDate = null;
				try {
					creDate = sdf.parse(dateStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				record.set("create_dt", schema, new Timestamp( creDate.getTime() ));
				record.set("like_cnt", schema, Integer.parseInt( valArr[14] ));
				record.set("dislike_cnt", schema, Integer.parseInt( valArr[15] ));
				record.set("prd_stat", schema, Integer.parseInt( valArr[16] ));
				record.set("disp_yn", schema, valArr[17]);
				
				context.write(NullWritable.get(), record);
				
			}
		}
	}

	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
//		conf.addResource(new Path(Prop.HADOOP_CONF_PATH + "/core-site.xml"));
//		conf.addResource(new Path(Prop.HADOOP_CONF_PATH + "/hdfs-site.xml"));
//		conf.addResource(new Path(Prop.HADOOP_CONF_PATH + "/mapred-site.xml"));
//		conf.addResource(new Path(Prop.HADOOP_CONF_PATH + "/yarn-site.xml"));
//		conf.addResource(new Path(Prop.HIVE_CONF_PATH + "/hive-site.xml"));

		args = new GenericOptionsParser(conf, args).getRemainingArgs();

		String inputDir = args[0];
		String dbName = args[1];	// svc_tas
		String outputTableName = args[2];	// tb_review_raw
		
		Job job = Job.getInstance(conf, "Review Raw Importer");
		job.getConfiguration().set("mapreduce.job.queuename", "TAS");
//		HCatInputFormat.setInput(job, dbName, inputTableName);
		

		job.setJarByClass(ParallelReviewRawDriver.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		
		job.setNumReduceTasks(1);

		
		job.setInputFormatClass(TextInputFormat.class);
//		job.setOutputFormatClass(TextOutputFormat.class);
		
		// Mapper emits a string as key and an integer as value
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		/*
		 * Input
		 */
		FileInputFormat.addInputPath(job, new Path(inputDir));

		/*
		 * Output
		 */
		// Ignore the key for the reducer output; emitting an HCatalog record as value
//		job.setOutputKeyClass(WritableComparable.class);
//		job.setOutputValueClass(DefaultHCatRecord.class);
//		job.setOutputFormatClass(HCatOutputFormat.class);
//
//		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//		Date date = new Date();
//
//		HashMap partitions = new HashMap<String, String>(1);
//		partitions.put("p_yyyymmdd", dateFormat.format(date));
//
//		HCatOutputFormat.setOutput(job, OutputJobInfo.create(dbName, outputTableName, partitions));
//
//		HCatSchema s = HCatOutputFormat.getTableSchema(job.getConfiguration());
//		System.err.println("INFO: output schema explicitly set for writing:" + s);
//		HCatOutputFormat.setSchema(job, s);
		
		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new ParallelReviewRawDriver(), args);
		System.exit(exitCode);
	}
}
