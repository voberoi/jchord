/*
 * Copyright (c) 2008-2010, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 * Licensed under the terms of the New BSD License.
 */
package chord.program.visitors;

import joeq.Compiler.Quad.Quad;

/**
 * Visitor over all return statements in all methods in the program.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public interface IReturnInstVisitor extends IMethodVisitor {
	/**
	 * Visits all return statements in all methods in the program.
	 * 
	 * @param	q	A return statement.
	 */
	public void visitReturnInst(Quad q);
}
