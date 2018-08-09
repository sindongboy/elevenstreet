package com.skplanet.tas.review.rule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.tas.review.ml.Predictor;
import com.skplanet.tas.review.preprocess.FeatureExtractor;
import com.skplanet.tas.review.util.ResourceLoader;

public class HeuristicRuleCtrl {
	public static void main(String[] args) {


		if( args.length < 3 ) {
			System.out.println("Please input the exact arguments: [resourcePath] [inputPath(only text)] [outputPath]");
			System.exit(0);
		}


		ResourceLoader.loadClasses(args[0]);

		Predictor pred = new Predictor();

//		List<Map> statAl = pred.getStat(new File(args[0] + "/total.csv"));
		List<Map> statAl = pred.getStat(false);

		HeuristicRule hr = new HeuristicRule(false);
//		HeuristicRule hr = new HeuristicRule(Configuration.PHYSICALPATH_LOAD);
		FeatureExtractor fe = new FeatureExtractor();


		/*
		 * 1. Read File
		 */
		FileReader fr = null;
		BufferedReader br = null;

		FileWriter fw = null;
		BufferedWriter bw = null;


		try {
			fr = new FileReader(args[1]);
			br  =new BufferedReader(fr);

			fw = new FileWriter(args[2]);
			bw = new BufferedWriter(fw);


			String line = null;

			while( (line=br.readLine()) != null ) {


				/*
				 * Check Heuristic Rule
				 */

				String txt = line;

				Map<String, Object> featureHm = fe.extract(txt, "");

				Boolean isSpam = hr.isSpam(txt, new StringBuffer(), statAl, featureHm, 2.0d);

				bw.write(line + "\t" + isSpam + "\n");
			}
			//			}


			br.close();
			fr.close();

			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
}
