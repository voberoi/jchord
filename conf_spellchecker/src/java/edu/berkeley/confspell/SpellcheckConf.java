/*
 * Copyright Ari Rabkin (asrabkin@gmail.com)
 * See the COPYING file for copyright license information. 
 */
package edu.berkeley.confspell;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

/**
 * This is the user-invoked main class for using the configuration spellchecker
 * in standalone mode.Its main job is to read files and parse command line
 * options.
 * 
 */
public class SpellcheckConf {

	static Slurper slurper;
	static Options clOpts = new Options();

	/**
	 * A Slurper is responsible for reading and masking configuration options
	 * from files and explicitly defined configuration.
	 * 
	 */
	public interface Slurper {
		/**
		 * Reads files and any given config key/value pairs, and sticks them in
		 * the given OptionSet with fully resolved/masked configuration.
		 * Configuration defined in files later in the list take precedent
		 * over configuration defined in files earlier in the list. Explicitly
		 * defined configuration in configKeyVal takes the highest precedence.
		 * 
		 * @param optionSet
		 *          the OptionSet object to populate
		 * @param files
		 *          any files to be read
		 * @param configKeyVal
		 *          any explicit key/value pairs to set
		 * @throws IOException
		 * @return an OptionSet with fully resolved configuration
		 */
		void slurp(OptionSet optionSet, List<File> files, Map<String, String> configKeyVal) throws IOException;
	}

	private static void helpAndExit(int status, String msg) {
		HelpFormatter hf = new HelpFormatter();

		if (msg != null) {
			if (status == 0) {
				System.out.println(msg);
			} else {
				System.err.println(msg);
			}
		}

		String usage = 
		"java -jar confspellcheck.jar [options] config_dict " + 
	   	"[config_file1 config_file2 ...] [key1=value1 key2=value2 ...]";

		String header = 
		"If only config_dict is given, dumps the dictionary to stdout. Otherwise, " +
		"checks the given configuration file(s) against the given configuration " +
		"dictionary, flagging and outputting any errors. If parsing Hadoop " +
		"configuration, later config file arguments or key/value pairs take " +
		"precedence over previous ones (e.g. \"hdfs-default.xml hdfs-site.xml " +
		"dfs.foo=bar\"  will prefer any config set in hdfs-site.xml over " +
		"hdfs-default.xml and dfs.foo=bar over dfs.foo set in any hdfs-*.xml).";

		hf.printHelp(usage, header, clOpts, null); // No footer.
		System.exit(status);
	}

	private static CommandLine handleArgs(String[] args) {
		clOpts.addOption("h", "help", false, "Print this message.");
		clOpts.addOption("d", "hadoop", false, "Parse Hadoop configuration file(s).");
		clOpts.addOption("p", "properties", false, "Parse Java properties file(s) (DEFAULT).");

		PosixParser parser = new PosixParser();
		CommandLine cl = null;
	
		try {
			cl = parser.parse(clOpts, args);
		} catch (ParseException e) {
			helpAndExit(1, "ParseException: " + e.getMessage());
		}

		if (cl.hasOption('h'))
			helpAndExit(0, null);

		if (cl.hasOption('d') && cl.hasOption('p')) {
			helpAndExit(1, "-d and -p are mutually exclusive.");
		}
		return cl;
	}

	/**
	 * The command line takes a list of arguments that may contain file names
     * and explicitly-defined arguments. This function splits the list of String
     * arguments into these components,
	 *
	 * @param the list of remaining arguments after parsing the command line
	 * @return a tuple containing a List<File> and Map<String, String>, where
	 * the list is a list of files to read configuration from and the map is
     * explicitly-defined configuration.
	 */
	private static Object[] cmdArgsToSlurperArgs(List<String> args) {
		List<File> files = new ArrayList<File>();
		Map<String, String> configKeyVal = new HashMap<String, String>();
		int i = 0;

		// Grab filenames.
		while (i < args.size() && args.get(i).indexOf("=") == -1) {
			files.add(new File(args.get(i)));
			i++;
		}

		// Grab defined config (the rest of the args).
		while (i < args.size()) {
			String arg = args.get(i);
			String[] keyVal = arg.split("=");
			configKeyVal.put(keyVal[0], keyVal[1]);
			i++;
		}

		Object[] slurperArgs = { files, configKeyVal };
		return slurperArgs;
	}

	/**
	 * Returns an OptDictionary containing dictionary entries from the file at
	 * confDictPath.
	 */ 
	private static OptDictionary readConfDict(String confDictPath) {
		File confDict = new File(confDictPath);
		if (!confDict.exists()) {
			helpAndExit(1, "Config dict not found at " + confDict.getAbsolutePath());
		}

		OptDictionary dictionary = new OptDictionary();
		try {
			dictionary.read(confDict);
		} catch (IOException e) {
			System.err.println("Error reading config dictionary at " + confDict);
			e.printStackTrace();
			System.exit(1);			
		}

		return dictionary;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLine cl = handleArgs(args);
	
		List<String> argList = cl.getArgList();	
		if (argList.size() == 0) {
			helpAndExit(1, "Error: need at least 1 arg.");
		}

		String confDictPath = argList.get(0);
		OptDictionary dictionary = readConfDict(confDictPath);		

		// Dump the dictionary to stdout if it's the only argument.
		if (argList.size() == 1) {
			dictionary.show();
			System.exit(0);
		}

		// Convert the rest of the args to a list of config files to read and
		// configuration key/value pairs.
		List<String> filesKeyVals = argList.subList(1, argList.size());
		Object[] slurperArgs = cmdArgsToSlurperArgs(filesKeyVals);
		List<File> files = (List<File>) slurperArgs[0];
		Map<String, String> configKeyVal = (Map<String, String>) slurperArgs[1];

		if (cl.hasOption('d')) {
			slurper = new HSlurper();
		} else { // Default or 'p'
			slurper = new PSlurper();
		}

		OptionSet clientConf = new OptionSet();
		try {
			slurper.slurp(clientConf, files, configKeyVal);
		} catch (IOException e) {
			System.err.println("Error while reading configuration.");
			e.printStackTrace();
			System.exit(1);			
		}
		Checker checker = new Checker(dictionary);
		checker.checkConf(clientConf);
	}
}
