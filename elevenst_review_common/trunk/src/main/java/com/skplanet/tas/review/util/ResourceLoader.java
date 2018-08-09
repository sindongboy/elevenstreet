package com.skplanet.tas.review.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ResourceLoader {

	public static void loadClasses(String path) {

		File f = new File(path);
		URL u = null;
		try {
			u = f.toURL();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class urlClass = URLClassLoader.class;
		Method method = null;
		try {
			method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		method.setAccessible(true);
		try {
			method.invoke(urlClassLoader, new Object[]{u});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
