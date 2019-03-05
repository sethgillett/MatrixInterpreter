package parser;

public class CmdReader extends ParserType {

	public CmdReader(Parser primary) {
		super(primary);
		// TODO Auto-generated constructor stub
	}
	
	public void CMD() {
		String tokenLit = tr.lastTokenStr();
		if (tokenLit.equals("new")) {
			ep.customError("'new' must be assigned to a scl or mtx");
		}
		else if (tokenLit.equals("del")) {
			DEL();
		}
		else if (tokenLit.equals("prn")) {
			PRN();
		}
	}
	
	public void NEW() {
		//TODO
	}
	
	public void DEL() {
		//TODO
	}
	
	public void PRN() {
		//TODO
	}
	
}
