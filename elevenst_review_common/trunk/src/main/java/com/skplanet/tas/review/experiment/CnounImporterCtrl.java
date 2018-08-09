package com.skplanet.tas.review.experiment;

public class CnounImporterCtrl {
	public static void main(String[] args) {
		CnounImporter ci = new CnounImporter();
		
//		ci.importCnouns("/Users/skplanet/Downloads/160104_all.emi.combined.sorted.filtered.tsv", 
//				"/Users/skplanet/Documents/2015_11ST/data/151228/cnoun/160104_CNOUN.txt");
		
//		ci.importCnouns("/Users/skplanet/Downloads/all.emi.combined.sorted.filtered.tsv", 
//				"/Users/skplanet/Documents/2015_11ST/data/151228/cnoun/160108_CNOUN.txt");
		
		ci.importCnouns2("/Users/skplanet/Downloads/bigram.tsv", 
				"/Users/skplanet/Documents/2015_11ST/data/cnoun/160303_BI_CNOUN.txt");
		
		ci.importCnouns2("/Users/skplanet/Downloads/trigram.tsv", 
				"/Users/skplanet/Documents/2015_11ST/data/cnoun/160303_TRI_CNOUN.txt");
		
		
		
	}
}
