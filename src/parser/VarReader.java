package parser;

import parser.primary.Parser;
import parser.primary.ParserType;
import tokens.Tk;
import vars.mtx.Mtx;
import vars.scl.Scl;

public class VarReader extends ParserType {

	public VarReader(Parser primary) {
		super(primary);
	}
	
	/**
	 * Creates a new scalar and assigns a value to it
	 */
	public void sclAssign() {
		tr.nextToken();
		String sclName = tr.tokenStr();
		tr.nextToken();
		Scl var = exprReader.sclExpr();
		setScl(sclName, var);
	}
	
	/**
	 * Creates a new matrix and assigns a value to it
	 */
	public void mtxAssign() {
		// A
		tr.nextToken();
		String mtxName = tr.tokenStr();
		// =
		tr.nextToken();
		// cmd?
		tr.nextToken();
		Mtx var;
		if (tr.tk == Tk.EOL) {
			var = inpReader.readMtxInputTerminal();
		}
		else {
			tr.prevToken();
			var = exprReader.mtxExpr();
		}
		setMtx(mtxName, var);
	}
	
}
