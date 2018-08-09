package review;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public final class JarResource {
	private static final Logger LOG = Logger.getLogger(JarResource.class);

	public List<String> getJarContents(URL jarLocation) throws IOException {
		return getJarContents(jarLocation.openStream());
	}

	public List<String> getJarContents(InputStream is) throws IOException {
		return load(is);
	}

	public List<String> getJarContents(String jarLocation) throws IOException {
		return getJarContents(new File(jarLocation));
	}

	public List<String> getJarContents(File jarFile) throws IOException {
		return load(jarFile);
	}

	private List<String> load(File jarFile) throws IOException {
		List<String> jarContents = new ArrayList<String>();
		try {
			ZipFile zf = new ZipFile(jarFile);
			for (Enumeration e = zf.entries(); e.hasMoreElements();) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				if (ze.isDirectory()) {
					continue;
				}

				jarContents.add(ze.getName());
			}
		} catch (NullPointerException e) {
			LOG.info( "done." );
		} catch (ZipException ze) {
			LOG.error( ze );
		}
		return jarContents;
	}

	private List<String> load(InputStream is) throws IOException {
		List<String> jarContents = new ArrayList<String>();
		try {
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				if (ze.isDirectory()) {
					continue;
				}

				jarContents.add(ze.getName());
				ze = zis.getNextEntry();
			}
		} catch (NullPointerException e) {
			LOG.info( "done." );
		}

		return jarContents;
	}
}