/*
 * Copyright Ari Rabkin (asrabkin@gmail.com)
 * See the COPYING file for copyright license information. 
 */
package edu.berkeley.confspell;

import java.io.File;
import java.util.Map;
import java.util.List;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;

import edu.berkeley.confspell.SpellcheckConf.Slurper;
import edu.berkeley.confspell.OptionSet;

/**
 * Slurper for Hadoop configuration files. Has a run-time and compile-time
 * dependency on Hadoop common components.
 * 
 */
public class HSlurper implements Slurper {

	// stolen from hadoop source

	public OptionSet slurp(List<File> files, Map<String, String> configKeyVal) {
	  /*
		Configuration c = new Configuration(false);
		c.addResource(new Path(f.getAbsolutePath())); // to search filesystem, not
																									// classpath
		c.reloadConfiguration();
		fromHConf(res, c);
	  */
		return new OptionSet();
	}

	public static void fromHConf(OptionSet res, Configuration c) {
		for (Map.Entry<String, String> e : c) {

			String rawV = e.getValue();
			String cookedV = c.get(e.getKey());

			res.put(e.getKey(), cookedV); // to force substitution

			res.checkForSubst(rawV);
		}
	}

	public static OptionSet fromHConf(Configuration c) {
		OptionSet res = new OptionSet();
		fromHConf(res, c);
		return res;
	}

}
