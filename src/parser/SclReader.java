package parser;

import vars.scl.Scl;

public class SclReader extends ParserType {

	public SclReader(Parser primary) {
		super(primary);
	}
	
	/**
	 * Creates a new scalar and assigns a value to it
	 */
	public void SCLASSIGN() {
		tr.nextToken();
		String sclName = tr.tokenStr();
		tr.nextToken();
		Scl var = exprReader.SCLEXPR();
		setScl(sclName, var);
	}
	
}
