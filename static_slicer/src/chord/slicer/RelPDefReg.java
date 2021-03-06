package chord.slicer;

import joeq.Compiler.Quad.Inst;
import joeq.Compiler.Quad.Quad;
import joeq.Compiler.Quad.Operand.RegisterOperand;
import joeq.Util.Templates.List;
import chord.analyses.point.DomP;
import chord.project.Chord;
import chord.project.analyses.ProgramRel;

/**
 * Relation containing tuples (p, u) such that a register u is 
 * defined at a program point p.
 * @author sangmin
 *
 */
@Chord(
		name = "PDefReg",
		sign = "P0,U0:P0_U0"
)
public class RelPDefReg extends ProgramRel {
	public void fill() {
		DomP domP = (DomP) doms[0];
		int numP = domP.size();
		for (int j=0; j < numP; j++) {
			Inst inst = domP.get(j);
			if (inst instanceof Quad) {
				Quad q = (Quad) inst;			
				List.RegisterOperand l = q.getDefinedRegisters();
				int numRegs = l.size();
				for (int i=0; i < numRegs; i++) {
					RegisterOperand ro = l.getRegisterOperand(i);
					add(inst, ro.getRegister());				
				}
			}
		}
	}

}
