package com.skplanet.tas.hadoop.review.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ntp.TimeStamp;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hive.hcatalog.data.DefaultHCatRecord;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.apache.hive.hcatalog.data.schema.HCatSchema;
import org.apache.hive.hcatalog.mapreduce.HCatBaseInputFormat;
import org.apache.hive.hcatalog.mapreduce.HCatInputFormat;
import org.apache.hive.hcatalog.mapreduce.HCatOutputFormat;
import org.apache.hive.hcatalog.mapreduce.OutputJobInfo;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
//import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;

import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.usermodeling.topical.util.StopWordDictionary;
import com.skplanet.tas.hadoop.review.io.Deserialization;
import com.skplanet.tas.review.ml.ModelLearner;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.ml.TrainingSetCreator;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.prop.Prop;
import com.skplanet.tas.review.rule.HeuristicRule;
import com.skplanet.tas.review.util.HDFSLoader;

public class ParallelReviewRawHdfsDriver extends Configured implements Tool {
	private static final Logger LOGGER = Logger.getLogger(ParallelReviewRawHdfsDriver.class.getName());

	public static class Map extends Mapper<WritableComparable, Text, Text, Text> {
		private static Whitelist whitelist;
		private static int NUM_OF_FIELDS;
		
		public Map() {
			 whitelist = new Whitelist();
			 NUM_OF_FIELDS = 18;
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
			
			String cont = valArr[13];
			
			/*
			 * Clean HTML Tags & Encoding
			 */
			
			// 특수 인코딩 처리.. (HTML 관련)
			cont = cont.replaceAll("[&][#][0-9]+[;]", "");
			
			cont = Jsoup.clean(cont, whitelist);
			cont = Parser.unescapeEntities(cont, false);
			cont = cont.replace((char) 160, ' ').trim();	// 특수문자 공백을 정상 공백으로 변환.
			cont = cont.replace((char) 12288, ' ').trim();
			
			valArr[13] = cont;
			
			StringBuffer newValSb = new StringBuffer();
			
			for(String subVal : valArr) {
				
				/*
				 * 전처리 추가.
				 */
				subVal = subVal.replace("\t", " ").trim();
				
				if( subVal.replace(" ", "").length() == 0 )	// 내용이 아무것도 없으면 소문자로 null을 추가해주기.
					subVal = "null";
				
				newValSb.append(subVal);
				newValSb.append("\001");
			}
			
			/*
			 * 제일 마지막 필드가 없는 경우를 대비하여 추가..
			 */
			String newValStr = null;
			
			if(valArr.length < NUM_OF_FIELDS) {
				newValSb.append("null");
				newValStr = newValSb.toString();
			} else {
				newValStr = newValSb.toString();
				newValStr = newValStr.substring(0, newValStr.length()-1);
			}
//			newValSb.append("\001");
			
			context.write(new Text(contentId), new Text(newValStr));	// 중카 단위로 묶어서 분석하기.

		}
	}

	public static class Reduce extends Reducer<Text, Text, NullWritable, Text> {

		private static String PARTITION = "p_yyyymmdd";

		protected void reduce(Text key,
				java.lang.Iterable<Text> values,
				Reducer<Text, Text, NullWritable, Text>.Context context)
						throws IOException, InterruptedException {

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
//				String val = valueText.toString();
				
				context.write(NullWritable.get(), valueText);
				
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
		String outputDir = args[1];
		
		Job job = Job.getInstance(conf, "Review Raw Importer for HDFS");
		job.getConfiguration().set("mapreduce.job.queuename", "TAS");

		job.setJarByClass(ParallelReviewRawHdfsDriver.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		
//		job.setNumReduceTasks(1);

		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		// Mapper emits a string as key and an integer as value
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		

		/*
		 * Input
		 */
		FileInputFormat.addInputPath(job, new Path(inputDir));
		FileOutputFormat.setOutputPath(job, new Path(outputDir));

		
		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new ParallelReviewRawHdfsDriver(), args);
		System.exit(exitCode);
	}
}
