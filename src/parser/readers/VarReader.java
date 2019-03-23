package parser.readers;

import parser.primary.ParserType;
import tokens.Tk;
import vars.mtx.Mtx;
import vars.scl.Scl;

public class VarReader extends ParserType {
	/**
	 * Creates a new scalar and assigns a value to it
	 * @return Whether the run was successful
	 */
	public boolean sclAssign() {
		tr.nextToken();
		String sclName = tr.tokenStr();
		tr.nextToken();
		Scl var = exprReader.sclExpr(null);
		if (var == null)
			return false;
		setScl(sclName, var);
		return true;
	}
	
	/**
	 * Creates a new matrix and assigns a value to it
	 * @return Whether the run was successful
	 */
	public boolean mtxAssign() {
		// A
		tr.nextToken();
		String mtxName = tr.tokenStr();
		// =
		tr.nextToken();
		// cmd?
		tr.nextToken();
		Mtx var;
		if (tr.tk == Tk.EOL) {
			var = inpReader.readMtxInput(true);
		}
		else {
			tr.prevToken();
			var = exprReader.mtxExpr(null);
		}
		if (var == null)
			return false;
		setMtx(mtxName, var);
		return true;
	}
	
}
