/*
 * Copyright Ari Rabkin (asrabkin@gmail.com)
 * See the COPYING file for copyright license information. 
 */
package edu.berkeley.confspell;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import edu.berkeley.confspell.SpellcheckConf.Slurper;
import edu.berkeley.confspell.OptionSet;

/**
 * Slurper for Hadoop configuration files. Has a run-time and compile-time
 * dependency on Hadoop common components.
 * 
 */
public class HSlurper implements Slurper {

	public void slurp(OptionSet optionSet, List<File> files, Map<String, String> configKeyVal) throws IOException {
		Configuration hadoopConf = new Configuration(false);

		// Read config from all the files.
		for (File file : files) {
			if (!file.exists()) {
				throw new IOException("Config file not found at " + file.getAbsolutePath());
			}
			hadoopConf.addResource(new Path(file.getAbsolutePath()));
		}

		// Set config from the key/value pairs.
		for (Map.Entry<String, String> entry : configKeyVal.entrySet()) {
			hadoopConf.set(entry.getKey(), entry.getValue());
		}

		fromHConf(optionSet, hadoopConf);
	}

	private void fromHConf(OptionSet res, Configuration c) {
		for (Map.Entry<String, String> e : c) {
			String rawV = c.getRaw(e.getKey());
			String cookedV = c.get(e.getKey());
			res.put(e.getKey(), cookedV); // To force substitution.
			res.checkForSubst(rawV);
		}
	}
}
