/*
 * Copyright (c) 2008-2010, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 * Licensed under the terms of the New BSD License.
 */
package chord.instr;

import java.io.IOException;

import joeq.Class.jq_Reference;
import joeq.Class.jq_Class;
import joeq.Class.jq_Array;

import javassist.CtClass;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import chord.program.Program;
import chord.project.Messages;
import chord.project.ChordProperties;
import chord.util.FileUtils;

/**
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public class OfflineTransformer {
    private static final String INSTR_STARTING =
		"INFO: Instrumentor: Starting to instrument all classes; this may take a while (use -Dchord.verbose=true for more detailed info) ...";
    private static final String INSTR_FINISHED = "INFO: Instrumentor: Finished instrumenting all classes.";
    private static final String CANNOT_INSTRUMENT_CLASS =
        "ERROR: OfflineTransformer: Skipping instrumenting class %s; reason follows.";
    private static final String CLASS_NOT_BOOT_NOR_USER =
		"ERROR: OfflineTransformer: Skipping instrumenting class %s; its defining resource %s is neither in the boot nor user classpath.";
    private static final String WROTE_INSTRUMENTED_CLASS = "INFO: Instrumentor: Wrote instrumented class %s.";
    private static final String CLASS_NOT_FOUND = "WARN: Instrumentor: Could not find class %s.";
    private final String bootClassesDirName;
    private final String userClassesDirName;
	private final boolean verbose;
	private final AbstractInstrumentor instrumentor;
	private final JavassistPool pool;
	public OfflineTransformer(AbstractInstrumentor _instrumentor) {
		bootClassesDirName = ChordProperties.bootClassesDirName;
		userClassesDirName = ChordProperties.userClassesDirName;
		verbose = ChordProperties.verbose;
		instrumentor = _instrumentor;
		pool = instrumentor.getPool();
	}
	public void run() {
		Messages.log(INSTR_STARTING);
        FileUtils.deleteFile(bootClassesDirName);
        FileUtils.deleteFile(userClassesDirName);
		Program program = Program.getProgram();
		for (jq_Reference r : program.getClasses()) {
			if (r instanceof jq_Array)
				continue;
			jq_Class c = (jq_Class) r;
			String cName = c.getName();
			Exception ex = null;
			try {
				CtClass clazz = instrumentor.edit(cName);
				if (clazz != null) {
					String outDir = getOutDir(cName);
					if (outDir != null) {
						clazz.writeFile(outDir);
						if (verbose) Messages.log(WROTE_INSTRUMENTED_CLASS, cName);
					}
				}
			} catch (IOException e) {
				ex = e;
			} catch (NotFoundException e) {
				ex = e;
			} catch (CannotCompileException e) {
				ex = e;
			}
			if (ex != null) {
				Messages.log(CANNOT_INSTRUMENT_CLASS, cName);
				ex.printStackTrace();
			}
		}
		Messages.log(INSTR_FINISHED);
	}
	private String getOutDir(String cName) {
        String rName = pool.getResource(cName);
        if (rName == null) {
            Messages.log(CLASS_NOT_FOUND, cName);
            return null;
        }
		if (pool.isBootResource(rName))
			return bootClassesDirName;
        if (pool.isUserResource(rName))
            return userClassesDirName;
		Messages.log(CLASS_NOT_BOOT_NOR_USER, cName, rName);
		return null;
	}
}

