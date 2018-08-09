package com.skplanet.tas.review.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.skplanet.tas.review.prop.Prop;

public class HDFSLoader {
	public static InputStream getInputStream(Boolean isHdfs, String file) {
		InputStream is = null;
		

		if( isHdfs ) {	// Hadoop일 경우.
			Path pt=new Path(Prop.HADOOP_URL + Prop.RANKER_PATH + "/" + file);
			FileSystem fs;
			try {
				fs = FileSystem.get(new Configuration());
				
				is = fs.open(pt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {	// loacl일 경우.
			//Get file from resources folder
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//			File file = new File(classLoader.getResource("fake.txt").getFile());

			try {
				is = classLoader.getResource(file).openStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return is;
	}
}
