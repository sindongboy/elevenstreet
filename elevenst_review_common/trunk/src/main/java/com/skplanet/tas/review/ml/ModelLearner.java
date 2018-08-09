package com.skplanet.tas.review.ml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

public class ModelLearner {
	public Instances loadDataForTest(String inputFile) {
		
		CSVLoader loader = new CSVLoader();
		Instances dataSet = null;
		
		try {
			loader.setSource(new File(inputFile));
			dataSet = loader.getDataSet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		dataSet.deleteAttributeAt(dataSet.attribute("mid1").index());
//		dataSet.deleteAttributeAt(dataSet.attribute("mid2").index());
		
		return dataSet;
	}
	
	public Instances removeMid(Instances dataSet) {

		if( dataSet.attribute("mid1") != null)
			dataSet.deleteAttributeAt(dataSet.attribute("mid1").index());
		if( dataSet.attribute("mid2") != null)
			dataSet.deleteAttributeAt(dataSet.attribute("mid2").index());
		
		return dataSet;
	}
	
	public Instances removeMid(Instance inst, Instances dataSet) {
		
		if( dataSet.attribute("mid1") != null)
			inst.deleteAttributeAt(dataSet.attribute("mid1").index());
		if( dataSet.attribute("mid2") != null)
			inst.deleteAttributeAt(dataSet.attribute("mid2").index());
		
		return dataSet;
	}
	
	public Instances loadData(String inputFile) {
		
		CSVLoader loader = new CSVLoader();
		Instances dataSet = null;
		
		try {
			loader.setSource(new File(inputFile));
			dataSet = loader.getDataSet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return dataSet;
	}
	
	public Instances loadData(InputStream is) {
		
		CSVLoader loader = new CSVLoader();
		Instances dataSet = null;
		
		try {
			loader.setSource(is);
			dataSet = loader.getDataSet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return dataSet;
	}
//	
//	public HashMap<String, Classifier> learnPreRuleModel(Instances newCvSet) {
//
//		/*
//		 * 1. Case에 맞추어 학습 데이터를 분할하여 적재.
//		 */
//		Instances n, a, t, na, at, nt, nat, notNat;
//		
//		// Init
//		n = new Instances(newCvSet);
//		n.delete();
//		
//		a = new Instances(newCvSet);
//		a.delete();
//		
//		t = new Instances(newCvSet);
//		t.delete();
//		
//		na = new Instances(newCvSet);
//		na.delete();
//		
//		at = new Instances(newCvSet);
//		at.delete();
//		
//		nt = new Instances(newCvSet);
//		nt.delete();
//		
//		nat = new Instances(newCvSet);
//		nat.delete();
//		
//		notNat = new Instances(newCvSet);
//		notNat.delete();
//		////////////////////////
//		
//		// Loop 돌며 조건에 맞는 Set 적재.
//		int nIdx = newCvSet.attribute("name_dist").index();
//		int aIdx = newCvSet.attribute("addr_dist").index();
//		int tIdx = newCvSet.attribute("telno_dist").index();
//		
//		for(int i=0; i<newCvSet.numInstances(); i++) {
//			Instance inst = newCvSet.instance(i);
//			
//			double nVal = inst.value( nIdx );
//			double aVal = inst.value( aIdx );
//			double tVal = inst.value( tIdx );
//			
////			System.out.println(nVal + ", " + aVal + ", " + tVal);
//			
//			if(nVal + aVal + tVal == 0) {
//				nat.add(inst);
//				continue;
//			}
//			
//			if(nVal == 0 && aVal > 0 && tVal > 0 ) {
//				n.add(inst);
//			} else if(aVal == 0 && nVal >0 && tVal > 0 ) {
//				a.add(inst);
//			} else if(tVal == 0 && nVal > 0 && aVal > 0 ) {
//				t.add(inst);
//			} else {
//				
//				if( nVal + aVal == 0 ) {
//					na.add(inst);
//				} else if( aVal + tVal == 0 ) {
//					at.add(inst);
//				} else if( nVal + tVal == 0 ) {
//					nt.add(inst);
//				} else if( nVal > 0 && aVal > 0 && tVal > 0 ) {
//					notNat.add(inst);
//				}
////				else {
////					System.out.println(nVal + ", " + aVal + ", " + tVal);
////				}
//				
//			}
//		}
//		
//		
//		System.out.println(n.numInstances() + ", " + a.numInstances() + ", " + t.numInstances() 
//				+ ", " + na.numInstances() + ", " + at.numInstances() + ", " + nt.numInstances() 
//				+ ", " + nat.numInstances() + ", " + notNat.numInstances() );
//		
//		System.out.println(n.numInstances() + a.numInstances() + t.numInstances()
//				+ na.numInstances() + at.numInstances() + nt.numInstances()
//				+ nat.numInstances() + notNat.numInstances()
//				);
//		
//		
//		System.out.println(newCvSet.numInstances());
//		
//		/*
//		 * 2. Case 별 모델 학습.
//		 */
//
//		HashMap<String, Classifier> modelHm = new HashMap();
//		
//		modelHm.put("n", learnClassifier(n, "n"));
//		modelHm.put("a", learnClassifier(a, "a"));
//		modelHm.put("t", learnClassifier(t, "t"));
//		modelHm.put("na", learnClassifier(na, "na"));
//		modelHm.put("at", learnClassifier(at, "at"));
//		modelHm.put("nt", learnClassifier(nt, "nt"));
//		modelHm.put("nat", learnClassifier(nat, "nat"));
//		modelHm.put("notNat", learnClassifier(notNat, "notNat"));
//
//		return modelHm;
//	}
	
	public String getCaseTag(Instance inst, Instances data) {
		int nIdx = data.attribute("name_dist").index();
		int aIdx = data.attribute("addr_dist").index();
		int tIdx = data.attribute("telno_dist").index();
		
		double nVal = inst.value( nIdx );
		double aVal = inst.value( aIdx );
		double tVal = inst.value( tIdx );
		
//		System.out.println(nVal + ", " + aVal + ", " + tVal);
		
		if(nVal + aVal + tVal == 0) {
			return "nat";
		} else if(nVal == 0 && aVal > 0 && tVal > 0 ) {
			return "n";
		} else if(aVal == 0 && nVal >0 && tVal > 0 ) {
			return "a";
		} else if(tVal == 0 && nVal > 0 && aVal > 0 ) {
			return "t";
		} else {
			
			if( nVal + aVal == 0 ) {
				return "na";
			} else if( aVal + tVal == 0 ) {
				return "at";
			} else if( nVal + tVal == 0 ) {
				return "nt";
			} else if( nVal > 0 && aVal > 0 && tVal > 0 ) {
				return "notNat";
			} else {
				return "notNat";
			}
		}
	}
	
	public Classifier learnClassifier(Instances data, String str, int modelFlag, boolean overSampled) {
		Classifier classifier = learnClassifier(data, modelFlag, overSampled);
		// Serialize model
		try {
			
			SerializationHelper.write("WebContent/model/m5p_" + str  + ".model", classifier);	// 신규 데이터. (2014.11.19)
			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return classifier;
	}
	
	public Classifier learnClassifier(Instances data, int modelFlag, boolean overSampled) {
		
		
		/*
		 * Over Sampling (SMOTE)
		 */
		
		if( overSampled ) {
			HashMap<Double, Integer> classHm = new HashMap();
			
			for(int j=0; j<data.numInstances(); j++) {
				
				Instance inst = data.instance(j);
				
				double classVal = inst.classValue();
				
				Integer cnt = classHm.get(classVal);
				
				if( cnt == null )
					cnt = 0;
				
				cnt++;
				
				classHm.put(classVal, cnt);
				
			}
			
			Integer cnt1 = classHm.get(0.0d);
			Integer cnt2 = classHm.get(1.0d);
			
			Double ratio = 0.0d;
			Double majorClass = 0.0d;
			
			if( cnt1 > cnt2 ) {	// major class 찾기.
				ratio = cnt1.doubleValue() / cnt2.doubleValue();
				majorClass = 0.0d;
			} else {
				ratio = cnt2.doubleValue() / cnt1.doubleValue();
				majorClass = 1.0d;
			}
			
	//		System.out.println(classHm);
	//		
	//		System.out.println(cnt1 + " " + cnt2);
			
			SMOTE sample = new SMOTE();
			sample.setClassValue(majorClass.intValue() + "");
			sample.setRandomSeed(100);
			sample.setNearestNeighbors(5);
			sample.setPercentage(ratio * 90);
			
			try {
				sample.setInputFormat(data);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				data = Filter.useFilter(data, sample);
	//			System.out.println(train);
	//			System.exit(0);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		///////////////////
		
		
//		String[] options = new String[1]; options[0] = "-U";
//		J48 classifier = new J48(); 
//		
//		try {
//			classifier.setOptions(options);
//			classifier.setMinNumObj(10);
//			classifier.buildClassifier(data);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		
//		REPTree classifier = new REPTree();
//		classifier.setMinNum(3);
//		
//		try {
//			classifier.buildClassifier(data);
//		} catch (Exception e) {
//			
//		}
		
//		data.deleteAttributeAt(data.attribute("mid1").index());
//		data.deleteAttributeAt(data.attribute("mid2").index());
		
		Classifier cls = null;
		
		if(modelFlag == 0) {

//			M5P classifier = new M5P();
//			try {
////				classifier.setMinNumInstances(50);	// Overfitting 예방.
//				
//				classifier.setMinNumInstances(4);	// Overfitting 예방.
//				
//				classifier.buildClassifier(data);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			RandomForest classifier = new RandomForest();
			try {
				classifier.setNumTrees(100);	// default
//				classifier.setNumTrees(500);
//				classifier.setNumTrees(50);
				classifier.setSeed(100);
				classifier.buildClassifier(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			cls = classifier;
		} else if(modelFlag == 1) {

			LinearRegression classifier = new LinearRegression();
			try {
				classifier.buildClassifier(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cls = classifier;
		} else if(modelFlag == 2) {
			Logistic classifier = new Logistic();
			
			try {
				classifier.buildClassifier(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cls = classifier;
		} else if(modelFlag == 3) {
			NaiveBayes classifier = new NaiveBayes();
			
			try {
				classifier.buildClassifier(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cls = classifier;
		} else if(modelFlag == 4) {
			M5P classifier = new M5P();
			
		classifier.setMinNumInstances(10000);	// Overfitting 예방.
			
			try {
				classifier.buildClassifier(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cls = classifier;
		} else if(modelFlag == 5) {
			String[] options = new String[1]; options[0] = "-U";
			J48 classifier = new J48(); 
			
			try {
				classifier.setOptions(options);
				classifier.setMinNumObj(10);
				classifier.buildClassifier(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			cls = classifier;
		}

//		MultilayerPerceptron classifier = new MultilayerPerceptron(); 
//
//		try {
//			classifier.buildClassifier(data);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return cls;
	}
	
	public Instances classify(Classifier classifier, Instances testSet) {
		
		
//		FastVector values = new FastVector();
//        values.addElement("Y");              
//        values.addElement("N");
		
//		testSet.deleteAttributeAt(testSet.attribute("mid1").index());
//		testSet.deleteAttributeAt(testSet.attribute("mid2").index());
        
        testSet.insertAttributeAt(new Attribute("class"), testSet.numAttributes());        
        testSet.setClass( testSet.attribute("class") );	// Set Class
        
        
        
//        System.out.println(classifier);
		
		for(int i=0; i<testSet.numInstances(); i++) {
			Instance inst = testSet.instance(i);
//			System.out.println(inst);
			Double val = null;
//			System.out.println("classifier = " + classifier);
			try {
				val = classifier.classifyInstance(inst);
				
				
				
				
//				if(val > 0.0d)
//					System.out.println(val.intValue());
				
//				System.out.println( testSet.classAttribute().value(val.intValue()) );
				
//				System.out.println(classifier.c);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				System.out.println(inst);
			}
			
			testSet.instance(i).setValue(testSet.numAttributes()-1, val);
			
			
//			System.out.println(val);
		}
		
		return testSet;
//		return oldTestSet;
	}
	
	public void truncateTable(Connection connect, String tblName) {
		Statement stmt = null;
		
		try {
			stmt = connect.createStatement();
			
			String sql = "TRUNCATE TABLE " + tblName;
			
			stmt.execute(sql);
			
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	public void insertResultIntoDB(Connection connect, String fileName1, String fileName2) {
		PreparedStatement pstmt = null;
		
		FileReader fr = null;
		BufferedReader br = null;
		
		String tblName = "PREDICTED_TBL";
		
		truncateTable(connect, tblName);
		
		try {
			
			String sql = "INSERT INTO " + tblName + " (MID1, MID2, P_VAL)"
					+ " VALUES (?, ?, ?)";
			
			pstmt = connect.prepareStatement(sql);
			
			// MID 쌍 적재.
			fr = new FileReader(fileName1);
			br = new BufferedReader(fr);
			
			ArrayList<String> al1 = new ArrayList();
			
			String line = null;
			String colLine= br.readLine();
			
			String[] colNameArr = colLine.split(",");
			int midIdx1 = -1;
			int midIdx2 = -1;
			
			for(int i=0; i<colNameArr.length; i++) {
				if( colNameArr[i].equals("mid1") )
					midIdx1 = i;
				else if( colNameArr[i].equals("mid2") )
					midIdx2 = i;
			}
			
			while( (line=br.readLine()) != null ) {
				String[] arr = line.split(",");
				String subStr = arr[midIdx1] + ", " + arr[midIdx2] + ", ";
				al1.add(subStr);
			}
			
			br.close();
			fr.close();
			
			
			// DUP_YN 쌍 적재.
			fr = new FileReader(fileName2);
			br = new BufferedReader(fr);
			
			ArrayList<String> al2 = new ArrayList();
			
			line = null;
			br.readLine();
			while( (line=br.readLine()) != null ) {
				String[] arr = line.split(",");
				String subStr = arr[arr.length-1];
				al2.add(subStr);
				
//				System.out.println(subStr);
//				System.exit(0);
			}

			br.close();
			fr.close();
			
			
			int commitCnt = 0;
			for(int i=0; i<al1.size(); i++) {
				String[] strArr = (al1.get(i) + al2.get(i)).split(",");
			
				for(int j=0; j<strArr.length; j++) { 
					pstmt.setString((j+1), strArr[j].trim());
//					System.out.println( (j+1) + ", " + strArr[j]);
				}
				
				pstmt.addBatch();
				pstmt.clearParameters();
				
				commitCnt++;
				
				if( (commitCnt % 10000) == 0 ) {
					pstmt.executeBatch();
					pstmt.clearBatch();
					
					System.out.println(commitCnt);
				}
			}
			
			pstmt.executeBatch();
			pstmt.clearBatch();
			
			pstmt.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
}
