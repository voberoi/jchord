/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.analyses.alias;

import chord.project.JavaTask;
import chord.doms.DomM;
import chord.project.Chord;
import chord.project.ProgramRel;
import chord.project.Project;

/**
 * Context-insensitive call graph analysis.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Chord(
	name = "cicg-java",
	consumedNames = { "IM", "MM", "reachableM" }
)
public class CICGAnalysis extends JavaTask {
	private DomM domM;
	private ProgramRel relIM;
	private ProgramRel relMM;
	private ProgramRel relReachableM;
	private CICG callGraph;
	public void run() {
		domM = (DomM) Project.getTrgt("M");
		relIM = (ProgramRel) Project.getTrgt("IM");
		relMM = (ProgramRel) Project.getTrgt("MM");
		relReachableM = (ProgramRel) Project.getTrgt("reachableM");
	}
	/**
	 * Provides the program's context-insensitive call graph.
	 * 
	 * @return	The program's context-insensitive call graph.
	 */
	public ICICG getCallGraph() {
		if (callGraph == null) {
			callGraph = new CICG(domM, relIM, relMM, relReachableM);
		}
		return callGraph;
	}
	/**
	 * Frees relations used by this program analysis if they are in
	 * memory.
	 * <p>
	 * This method must be called after clients are done exercising
	 * the interface of this analysis.
	 */
	public void free() {
		if (callGraph != null)
			callGraph.free();
	}
}
