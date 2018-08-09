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
import java.util.Iterator;
import java.util.List;
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

public class ParallelReviewRankerDriver extends Configured implements Tool {
	private static final Logger LOGGER = Logger.getLogger(ParallelReviewRankerDriver.class.getName());

	public static class Map extends Mapper<WritableComparable, HCatRecord, Text, Text> {
		String catId1;
		String catId2;

		String productId;
		String contentId;

		private static String PRD_NO = "prdno";
		private static String PRD_NM = "prdname";
		private static String DISP_CTGR_0 = "disp_ctgr_no_de";
		private static String DISP_CTGR_1 = "disp_ctgr1_no_de";
		private static String DISP_CTGR_2 = "disp_ctgr2_no_de";
		private static String DISP_CTGR_3 = "disp_ctgr3_no_de";
		private static String DISP_CTGR_4 = "disp_ctgr4_no_de";
		private static String ORD_NO = "ordno";
		private static String CONT_NO = "contno";
		private static String CONTENT = "content";
		private static String IMG_YN = "imgyn";
		private static String MOVIE_YN = "movieyn";
		private static String MOBILE_YN = "mobileyn";
		private static String CREATE_DT = "create";

		/*
		 * For Ranking
		 */
		private static java.util.List<java.util.Map> statAl;
		private static HeuristicRule hr;
		private static FeatureExtractor fe;

		private static Predictor pred;

		private static Map weightHm;
		private static Map sentHm;

		private static Classifier cls;
		private static Instances header;

		private static String colStr;

		private static TrainingSetCreator tsc;
		


		public void loadClassifier(String path, Integer modelFlag) {
			try {

//				System.out.println(path);
//				
//				// read model and header
////				final ObjectInputStream in = new ObjectInputStream(
////				        new BufferedInputStream(HDFSLoader.getInputStream(true, path)));
////				
////				Vector v = (Vector) in.readObject();
//				
//				ObjectInputStream objReader = new ObjectInputStream(HDFSLoader.getInputStream(true, path));
//
////				System.out.println(in.readObject());
//				
//				Vector v = (Vector) objReader.readObject();
				
				
				
				
				ModelLearner ml = new ModelLearner();

				/*
				 * 1. Loading the data
				 */
				System.out.println("Loading the data...");

//				Integer modelFlag = Integer.parseInt();
				

				Instances cvSet = ml.loadData(HDFSLoader.getInputStream(true, path));

				cvSet.setClass( cvSet.attribute("class") );	// Set Class


				/*
				 * 4. Learn the Classification Model
				 */
//				cvSet.setClass( cvSet.attribute("class") );	// Set Class


				System.out.println(cvSet.classIndex());

				System.out.println("Learning the features...");

//				Classifier classifier = ml.learnClassifier(cvSet, modelFlag, true);	//실제 학습 시에는 Oversampling 수행.
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
			
//			loadClassifier(Prop.CLS_PATH);
			
			loadClassifier(Prop.TRAIN_PATH, 2);	// logistics
			
//			loadClassifier("review-ranker-1.0.model");
			
//			loadClassifier(Prop.HADOOP_URL + Prop.CLS_PATH);


			colStr = tsc.getColumnRow(fe, false);
			colStr = colStr.substring(0, colStr.length()-1) + "\n";


			pred = new Predictor();

			//		System.out.println(Thread.currentThread().getContextClassLoader().getResource("total.csv").getPath());

			
			
//			statAl = pred.getStat(new File(Thread.currentThread().getContextClassLoader().getResource("total.csv").getPath()));
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
		protected void map( WritableComparable key, HCatRecord value,
				org.apache.hadoop.mapreduce.Mapper<WritableComparable, HCatRecord, Text, Text>.Context context)
						throws IOException, InterruptedException {

			HCatSchema schema = HCatBaseInputFormat.getTableSchema(context.getConfiguration());

			// The group table from /etc/group has name, 'x', id
			/*
            contentId = (String) value.get(0);
            String content = (String) value.get(12);
			 */

			catId1 = value.getString(DISP_CTGR_1, schema);	// 대카
			catId2 = value.getString(DISP_CTGR_2, schema);	// 중카

			productId = value.getString(PRD_NO, schema);
			contentId = value.getString(CONT_NO, schema);
			
			String imgYn = value.getString(IMG_YN, schema);
			String movieYn = value.getString(MOVIE_YN, schema);
			String mobileYn = value.getString(MOBILE_YN, schema);
			
			String content = value.getString(CONTENT, schema);

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
			} else {

				String[] lineArr = new String[4];
				lineArr[0] = null;
				lineArr[1] = imgYn;	// imgYn
				lineArr[2] = movieYn;	// movieYn
				lineArr[3] = mobileYn;	// mobileYn

				StringBuffer instSb = tsc.getInstance(lineArr, featureHm, content);

//				System.out.println("instSb = " + instSb);

				String inst = instSb.toString().substring(0, instSb.length()-1) + "\n";

				score = predict(colStr, inst, cls, header, statAl, fe, hr);

			}
			
			
			StringBuffer val = new StringBuffer();
			val.append(contentId);
			val.append("\t");
			val.append(score);
			
			
			context.write(new Text(catId1), new Text(val.toString()));	// 중카 단위로 묶어서 분석하기.

		}
	}

	public static class Reduce extends Reducer<Text, Text, WritableComparable, HCatRecord> {

		private static String CONT_NO = "cont_no";
		private static String CONT_SCORE = "cont_score";
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
				String valueStr = valueText.toString();
				String[] fields = valueStr.split("\t");

				if (fields.length != 2) {
					LOGGER.warn("wrong fields : " + valueStr);
					continue;
				}

				String contNo = fields[0];
				Double score = Double.parseDouble(fields[1]);
//				String imgYn = fields[1];
//				String movieYn = fields[2];
//				String mobileYn = fields[3];
//				
//				String cont = fields[4];
				
//				LOGGER.info(cont);

				

				// Emit the group name and ID as a record
				HCatRecord record = new DefaultHCatRecord(2);
				record.set(CONT_NO, schema, contNo);
				record.set(CONT_SCORE, schema, score);
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

		// Get the input and output table names as arguments
		
		String dbName = args[0];
		
		// tb_review_raw
		String inputTableName = args[1];
		// tb_review_nlp
		String outputTableName = args[2];
		// Assume the default database
		
		
		
//		String dbName = "tas_product";

		Job job = Job.getInstance(conf, "Review Ranker");
		job.getConfiguration().set("mapreduce.job.queuename", "TAS");
		
		HCatInputFormat.setInput(job, dbName, inputTableName);
		job.setJarByClass(ParallelReviewRankerDriver.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setNumReduceTasks(1);


		// An HCatalog record as input
		job.setInputFormatClass(HCatInputFormat.class);

		// Mapper emits a string as key and an integer as value
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		// Ignore the key for the reducer output; emitting an HCatalog record as value
		job.setOutputKeyClass(WritableComparable.class);
		job.setOutputValueClass(DefaultHCatRecord.class);
		job.setOutputFormatClass(HCatOutputFormat.class);

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();

		HashMap partitions = new HashMap<String, String>(1);
		partitions.put("p_yyyymmdd", dateFormat.format(date));

		HCatOutputFormat.setOutput(job, OutputJobInfo.create(dbName, outputTableName, partitions));

		HCatSchema s = HCatOutputFormat.getTableSchema(job.getConfiguration());
		System.err.println("INFO: output schema explicitly set for writing:" + s);
		HCatOutputFormat.setSchema(job, s);
		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new ParallelReviewRankerDriver(), args);
		System.exit(exitCode);
	}
}
