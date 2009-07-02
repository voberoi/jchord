/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.doms;

import joeq.Compiler.Quad.Quad;
import joeq.Compiler.Quad.Operand.TypeOperand;
import joeq.Compiler.Quad.Operator.New;
import joeq.Compiler.Quad.Operator.NewArray;
import chord.project.Chord;
import chord.project.Program;
import chord.visitors.INewInstVisitor;

/**
 * Domain of object allocation statements.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Chord(
	name = "H",
	consumedNames = { "M" }
)
public class DomH extends QuadDom implements INewInstVisitor {
	public void init() {
		set(null);
	}
	public void visitNewInst(Quad q) {
		set(q);
	}
	public String toXMLAttrsString(Quad q) {
		// XXX
		if (q == null)
			return "null";
		TypeOperand to = (q.getOperator() instanceof New) ?
			New.getType(q) : NewArray.getType(q);
		return super.toXMLAttrsString(q) +
			" type=\"" + to.getType().getName() + "\"";
	}
	public String toString(Quad q) {
		// XXX
		if (q == null)
			return "null";
		return Program.toStringNewInst(q);
	}
}