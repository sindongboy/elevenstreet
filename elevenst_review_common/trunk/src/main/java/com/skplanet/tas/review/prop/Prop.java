package com.skplanet.tas.review.prop;

public final class Prop {
//	public static final String HADOOP_CONF_PATH = "/etc/hadoop/conf";
//    public static final String HIVE_CONF_PATH = "/etc/hive/conf";
//    public static final String PROP_PATH = "/dmp/prop/sentiment-analyzer.properties";
//    public static final String RANKER_PATH = "/user/usermodeling/ranker";
//    public static final String CLS_PATH = "review-ranker-1.0.model";
//    public static final String TRAIN_PATH = "review_combined_train_20160331.csv";
//    public static final String HADOOP_URL = "hdfs://UMi-hdn07:8020";
	
	/*
	 * 경로 찾아서 넣어보기. (Profile로 처리하는 것이 좋을 듯.)
	 * 
	 * Profile 이용하여 해당 경로들 동적으로 Import하기.
	 */
	
	public static final String HADOOP_CONF_PATH = "/app/hdfs/conf";
    public static final String HIVE_CONF_PATH = "/app/hive/conf";
    
//    public static final String PROP_PATH = "/dmp/prop/sentiment-analyzer.properties";
    public static final String RANKER_PATH = "/user/tas/product/job/ranker/resource";
    public static final String CLS_PATH = "review-ranker-1.1.model";
    public static final String TRAIN_PATH = "train_20160610.csv";
    public static final String HADOOP_URL = "hdfs://skpds";
}
