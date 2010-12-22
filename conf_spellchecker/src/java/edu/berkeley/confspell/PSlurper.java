/*
 * Copyright Ari Rabkin (asrabkin@gmail.com)
 * See the COPYING file for copyright license information. 
 */
package edu.berkeley.confspell;

import java.io.*;
import java.util.List;
import java.util.Map;

import edu.berkeley.confspell.SpellcheckConf.Slurper;
import edu.berkeley.confspell.OptionSet;

/**
 * Reads a Java Properties file into an OptionSet.
 * 
 * NOTE: doesn't support masking configuration from multiple files and
 * key/value pairs. Only supports reading the first file from the file list.
 */
class PSlurper implements Slurper {
	public void slurp(OptionSet optionSet, List<File> files, Map<String, String> configKeyVal) throws IOException {
		File f = files.get(0);

		Properties p = new Properties();
		FileInputStream fis = new FileInputStream(f);
		p.load(fis);
		fis.close();

		for (Map.Entry e : p.entrySet()) {
			String v = e.getValue().toString();
			// no need to remove comments, Java does it for us.
			// if(v.contains("#"))
			// v = v.substring(0, v.indexOf("#"))
			optionSet.put(e.getKey().toString(), v);
			optionSet.checkForSubst(v);
		}
		// "PROP-" +
	}

}