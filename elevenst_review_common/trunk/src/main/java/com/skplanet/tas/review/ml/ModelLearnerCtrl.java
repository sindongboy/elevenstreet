package com.skplanet.tas.review.ml;

import java.util.Vector;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class ModelLearnerCtrl {
	public static void main(String[] args) {

		if( args.length < 3 ) {
			System.out.println("Please input the exact arguments: [inputPath(TrainingSet)] [outputPath(SerializedModel)] [0:Random Forest, 1:regression, 2:Logistics, 3:NB, 4:M5P, 5:DT]");
			System.exit(0);
		}

		ModelLearner ml = new ModelLearner();

		/*
		 * 1. Loading the data
		 */
		System.out.println("Loading the data...");

		String inputFile = args[0];

		Integer modelFlag = Integer.parseInt(args[2]);

		Instances cvSet = ml.loadData(inputFile);

		cvSet.setClass( cvSet.attribute("class") );	// Set Class


		/*
		 * 4. Learn the Classification Model
		 */
//		cvSet.setClass( cvSet.attribute("class") );	// Set Class


		System.out.println(cvSet.classIndex());

		System.out.println("Learning the features...");

//		Classifier classifier = ml.learnClassifier(cvSet, modelFlag, true);	//실제 학습 시에는 Oversampling 수행.
		Classifier classifier = ml.learnClassifier(cvSet, modelFlag, false);

		//		// Serialize model
		//		try {
		//			SerializationHelper.write(args[1], classifier);
		//
		//		} catch (Exception e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		}

		// Serialize model
		// save model + header
		Vector v = new Vector();
		v.add(classifier);
		v.add(new Instances(cvSet, 0));


	    try {
			SerializationHelper.write(args[1], v);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
