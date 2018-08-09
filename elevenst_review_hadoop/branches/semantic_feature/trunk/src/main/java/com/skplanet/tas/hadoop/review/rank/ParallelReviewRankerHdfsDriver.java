package com.skplanet.tas.hadoop.review.rank;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
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
import org.apache.log4j.Logger;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
//import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;

import com.skplanet.tas.hadoop.review.io.Deserialization;
import com.skplanet.tas.review.ml.ModelLearner;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.ml.TrainingSetCreator;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.prop.Prop;
import com.skplanet.tas.review.rule.HeuristicRule;
import com.skplanet.tas.review.util.HDFSLoader;

public class ParallelReviewRankerHdfsDriver extends Configured implements Tool {
	private static final Logger LOGGER = Logger.getLogger(ParallelReviewRankerHdfsDriver.class.getName());

	public static class Map extends Mapper<WritableComparable, Text, Text, Text> {

		private static String TRAIN_PATH = "train_20160719.csv";
		
		private static int PRD_NO = 0;
		private static int CONT_NO = 1;
		
		private static int ORD_NO = 2;
		
		private static int IMG_YN = 3;
		private static int MOVIE_YN = 4;
		private static int MOBILE_YN = 5;
		
		private static int CONTENT = 6;
		
		private static int APRV_IMG_YN = 7;
		
		private static int R2V = 8;	// Review2vec 결과.
		private static int C2V = 9;	// Cat2vec 결과.

		/*
		 * For Ranking
		 */
		private java.util.List<java.util.Map> statAl;
		private HeuristicRule hr;
		private FeatureExtractor fe;

		private Predictor pred;

		private Map weightHm;
		private Map sentHm;

		private Classifier cls;
		private Instances header;

		private String colStr;

		private TrainingSetCreator tsc;
		


		public void loadClassifier(String path, Integer modelFlag) {
			try {
				
				ModelLearner ml = new ModelLearner();

				/*
				 * 1. Loading the data
				 */
				System.out.println("Loading the data...");


				Instances cvSet = ml.loadData(HDFSLoader.getInputStream(true, path));

				cvSet.setClass( cvSet.attribute("class") );	// Set Class


				/*
				 * 4. Learn the Classification Model
				 */

				System.out.println(cvSet.classIndex());

				System.out.println("Learning the features...");

				Classifier classifier = ml.learnClassifier(cvSet, modelFlag, false);

				cls = classifier;
				header = new Instances(cvSet, 0);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		public Map() {
			
			
			
			/*
			 * Resource File 가져오기.
			 */
//			try {
//				DynamicClassLoader.getSystemResources(Prop.RANKER_PATH);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			hr = new HeuristicRule(true);
			fe = new FeatureExtractor();

			tsc = new TrainingSetCreator();
			
			loadClassifier(TRAIN_PATH, 2);	// logistics
			

			colStr = tsc.getColumnRow(fe, false);
			colStr = colStr.substring(0, colStr.length()-1) + "\n";


			pred = new Predictor();

			statAl = pred.getStat(true);

		}
		

		public double predict(String columnStr, String data, Classifier cls, Instances header, List<java.util.Map> statAl, FeatureExtractor fe, HeuristicRule hr) {

			data = columnStr + data;
			

			// 2-3. Load Data
			InputStream is = null;
			try {
				is = new ByteArrayInputStream(data.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			CSVLoader loader = new CSVLoader();
			Instances dataSet = null;
			//					Instances prdDataSet = null;

			try {
				loader.setSource(is);
				dataSet = loader.getDataSet();

				//			dataSet = fs.getFilteredData(dataSet);	// feature selection
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[Error] Data = ");
				System.out.println(data);
			}



			// 2-5. Save into DB
			int commitCnt = 0;

			String predicted = null;
			Double score = null;

			for(int i=0; i<dataSet.numInstances(); i++) {
				//			Instance inst = dataSet.instance(i);


				Instance curr = dataSet.instance(i);



				/*
				 * Prepared Classification
				 */
				// create an instance for the classifier that fits the training data
				// Instances object returned here might differ slightly from the one
				// used during training the classifier, e.g., different order of
				// nominal values, different number of attributes.
				Instance inst = new Instance(header.numAttributes());
//				Instance inst = (Instance) curr.copy();
				inst.setDataset(header);
				for (int n = 0; n < header.numAttributes(); n++) {
					Attribute att = dataSet.attribute(header.attribute(n).name());
					// original attribute is also present in the current dataset
					if (att != null) {
						if (att.isNominal()) {
							// is this label also in the original data?
							// Note:
							// "numValues() > 0" is only used to avoid problems with nominal 
							// attributes that have 0 labels, which can easily happen with
							// data loaded from a database
							if ((header.attribute(n).numValues() > 0) && (att.numValues() > 0)) {
								String label = curr.stringValue(att);
								int index = header.attribute(n).indexOfValue(label);
								if (index != -1)
									inst.setValue(n, index);
							}
						}
						else if (att.isNumeric()) {
							inst.setValue(n, curr.value(att));
						}
						else {
							throw new IllegalStateException("Unhandled attribute type!");
						}
					}
				}


				try {


					/*
					 * 커버가 안되면, ML 적용.
					 */

					int predClass = (int) cls.classifyInstance( inst );	// 0혹은 1.

					predicted = header.classAttribute().value(predClass);


					//				System.out.println(predClass + ", " + predicted);

					score = cls.distributionForInstance( inst )[predClass];

					if( predicted.equals("bad") )
						score = 1.0d - score;

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					System.out.println(curr);
					//				System.out.println(inst);
				}

				commitCnt++;

				if( (commitCnt % 10000) == 0 ) {
					System.out.println(commitCnt);
				}

			}

			// init
			data = "";

			return score;
		}
		
		
		@Override
		protected void map( WritableComparable key, Text value,
				org.apache.hadoop.mapreduce.Mapper<WritableComparable, Text, Text, Text>.Context context)
						throws IOException, InterruptedException {

			// The group table from /etc/group has name, 'x', id
			/*
            contentId = (String) value.get(0);
            String content = (String) value.get(12);
			 */
			
			String[] valArr = value.toString().split("\001");
			
			

//			catId1 = value.getString(DISP_CTGR_1, schema);	// 대카
//			catId2 = value.getString(DISP_CTGR_2, schema);	// 중카

//			productId = value.getString(PRD_NO, schema);
			String productId = valArr[PRD_NO];
			String contentId = valArr[CONT_NO];
			String orderId = valArr[ORD_NO];
			
			String imgYn = valArr[IMG_YN];
			String movieYn = valArr[MOVIE_YN];
			String mobileYn = valArr[MOBILE_YN];
			
			String aprvImgYn = valArr[APRV_IMG_YN];
			
			String[] r2v = valArr[R2V].substring(1, valArr[R2V].length()-1).split(",");
			String[] c2v = valArr[C2V].substring(1, valArr[C2V].length()-1).split(",");
			

			
			if( aprvImgYn == null || aprvImgYn.trim().equals("") )	// 없으면 검수 안된 것으로 가정.
				aprvImgYn = "N";
			
			String content = valArr[CONTENT];

			if(content.trim().length() == 0)
				content = "null";

			
			/*
			 * Predict
			 */
			java.util.Map featureHm = fe.extract(content, "");

			Boolean isSpam = hr.isSpam(content, new StringBuffer(), statAl, featureHm, 2.0d);


			Double score = 0.0d;

			if(isSpam) {
				score = -1.0d;
				
				
//				if( contentId.equals("12859076") ) {
//					LOGGER.info("***** JB'S Inform... (Inner) ****");
//					LOGGER.info(featureHm);
//					LOGGER.info(content);
//				}
				
			} else {

				String[] lineArr = new String[5];
				lineArr[0] = null;
				lineArr[1] = imgYn;	// imgYn
				lineArr[2] = movieYn;	// movieYn
				lineArr[3] = mobileYn;	// mobileYn
				lineArr[4] = aprvImgYn;	// aprvImgYn (이미지 수작업 결과)

				// add Semantics.
				for(int k=0; k<r2v.length; k++) { 

					featureHm.put("r2v_" + k, r2v[k]);
				}
				
				for(int k=0; k<c2v.length; k++) { 

					featureHm.put("c2v_" + k, c2v[k]);
				}
				
				
				StringBuffer instSb = tsc.getInstance(lineArr, featureHm, content);

//				System.out.println("instSb = " + instSb);

				String inst = instSb.toString().substring(0, instSb.length()-1) + "\n";

				score = predict(colStr, inst, cls, header, statAl, fe, hr);

			}
			
			
			StringBuffer val = new StringBuffer();
			val.append(contentId);
			val.append("\001");
			val.append(orderId);
			val.append("\001");
			val.append(content.trim().length());
			val.append("\001");
			val.append(score);
			
			
			context.write(new Text(productId), new Text(val.toString()));	// 중카 단위로 묶어서 분석하기.

		}
	}

	public static class Reduce extends Reducer<Text, Text, WritableComparable, Text> {

//		private static String CONT_NO = "cont_no";
//		private static String CONT_SCORE = "cont_score";
//		private static String PARTITION = "p_yyyymmdd";
		
		private static int CONT_NO = 0;
		private static int ORD_NO = 1;
		private static int CONT_LEN = 2;
		private static int CONT_SCORE = 3;
		
		private String prevPrdNo = null;
		
		private static Set<String> ordNoHs;
		
		public Reduce() {
			ordNoHs = new HashSet();
		}


		protected void reduce(Text key,
				java.lang.Iterable<Text> values,
				Reducer<Text, Text, WritableComparable, Text>.Context context)
						throws IOException, InterruptedException {

//			String contNo = key.toString();
			
			String prdNo = key.toString();
			
			/*
			 * prdNo가 변경될때마다 HashSet을 리프레쉬 하기. (메모리 문제 등 개선)
			 */
			if( prevPrdNo != null && !prdNo.equals(prevPrdNo) )
				ordNoHs = new HashSet();
			
			prevPrdNo = prdNo;

			// Only expecting one ID per group name
			Iterator<Text> iter = values.iterator();
			while (iter.hasNext()) {
				Text valueText = iter.next();
				String valueStr = valueText.toString();
				
				String[] valArr = valueStr.split("\001");

				String contNo = valArr[CONT_NO];
				String ordNo = valArr[ORD_NO];
				String contLen = valArr[CONT_LEN];
				
				Double score = Double.parseDouble(valArr[CONT_SCORE]);

				/*
				 * 중복 리뷰 검사 로직
				 */
				StringBuffer ordNoSb = new StringBuffer();
				ordNoSb.append(prdNo);
				ordNoSb.append("_");
//				ordNoSb.append(score);
				ordNoSb.append(ordNo);
				ordNoSb.append("_");
				ordNoSb.append(contLen);
				
				
				if( ordNoHs.contains(ordNoSb.toString()) ) {	// 이미 한 번 출력했으면, -2.0 출력.
					score = -2.0d;
				} else {	// 없으면 ordNoHs에 추가.
					ordNoHs.add(ordNoSb.toString());
				}
				
				String row = contNo + "\001" + score;

				context.write(NullWritable.get(), new Text(row));
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
		// Get the input and output table names as arguments
		
//		String dbName = args[0];
		
//		// tb_review_raw
//		String inputTableName = args[1];
//		// tb_review_nlp
//		String outputTableName = args[2];
		// Assume the default database
		
		
		
//		String dbName = "tas_product";

		Job job = Job.getInstance(conf, "Review Ranker");
		job.getConfiguration().set("mapreduce.job.queuename", "TAS");
		
//		HCatInputFormat.setInput(job, dbName, inputTableName);
		job.setJarByClass(ParallelReviewRankerHdfsDriver.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setNumReduceTasks(1);	// 1개로 설정하고, 그 곳에서 중복 처리하여 출력하기.


		// An HCatalog record as input
//		job.setInputFormatClass(HCatInputFormat.class);

		// Mapper emits a string as key and an integer as value
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		// Ignore the key for the reducer output; emitting an HCatalog record as value
//		job.setOutputKeyClass(WritableComparable.class);
		
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		// Mapper emits a string as key and an integer as value
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(inputDir));
		FileOutputFormat.setOutputPath(job, new Path(outputDir));
		
		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new ParallelReviewRankerHdfsDriver(), args);
		System.exit(exitCode);
	}
}
