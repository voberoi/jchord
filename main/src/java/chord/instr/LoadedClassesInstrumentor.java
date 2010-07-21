/*
 * Copyright (c) 2008-2010, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.instr;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;

import javassist.NotFoundException;
import javassist.CannotCompileException;
import javassist.CtClass;

import chord.project.Messages;

/**
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public class LoadedClassesInstrumentor extends AbstractInstrumentor {
	List<String> loadedClasses = new ArrayList<String>();
	public LoadedClassesInstrumentor(Map<String, String> argsMap) {
		super(argsMap);
		final String fileName = argsMap.get("classes_file_name");
		assert (fileName != null);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					PrintWriter out = new PrintWriter(new File(fileName));
					for (String s : loadedClasses)
						out.println(s);
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		});
    }

	@Override
	public CtClass edit(String cName) throws NotFoundException, CannotCompileException {
		if (!isExcluded(cName))
			loadedClasses.add(cName);
		return null;
	}

	@Override
	public CtClass edit(CtClass clazz) throws CannotCompileException {
		assert(false);
		return null;
	}
}

