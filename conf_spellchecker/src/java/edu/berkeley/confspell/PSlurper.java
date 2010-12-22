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
 * Reads a Java Properties file into an OptionSet
 */
class PSlurper implements Slurper {
	public OptionSet slurp(List<File> files, Map<String, String> configKeyVal) throws IOException {
		/*
		Properties p = new Properties();
		FileInputStream fis = new FileInputStream(f);
		p.load(fis);
		fis.close();

		for (Map.Entry e : p.entrySet()) {
			String v = e.getValue().toString();
			// no need to remove comments, Java does it for us.
			// if(v.contains("#"))
			// v = v.substring(0, v.indexOf("#"))
			res.put(e.getKey().toString(), v);
			res.checkForSubst(v);
		}
		// "PROP-" +
		*/

		if (false) {
			throw new IOException();
		}
		return new OptionSet();
	}

}