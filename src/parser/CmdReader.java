package parser;

import parser.primary.Parser;
import parser.primary.ParserType;
import vars.mtx.Mtx;

public class CmdReader extends ParserType {

	public CmdReader(Parser primary) {
		super(primary);
	}
	
	/**
	 * Evaluates and returns the result of a command
	 * @return Scalar or matrix
	 */
	public Object varCmd() {
		tr.nextToken();
		String tokenLit = tr.tokenStr();
		switch(tokenLit) {
		case "id":
			Integer[] sizeId = inpReader.<Integer>readParams(2, () -> {return inpReader.readPositiveIntParam();});
			if (sizeId != null)
				return Mtx.identity(sizeId[0], sizeId[1]);
			break;
		
		case "zero":
			Integer[] sizeZ = inpReader.<Integer>readParams(2, () -> {return inpReader.readPositiveIntParam();});
			if (sizeZ != null)
				return Mtx.zero(sizeZ[0], sizeZ[1]);
			break;
			
		default:
			ep.internalError("Command %s hasn't been coded yet", tr.tokenStr());
		}
		return null;
	}
	
	public void nullCmd() {
		tr.nextToken();
		String tokenLit = tr.tokenStr();
		switch (tokenLit) {
		case "del":
			del();
			break;
		case "prn":
		case "print":
			prn();
			break;
		}
	}
	
	public void del() {
		String varName = inpReader.<String>readParam(() -> {return inpReader.readStrParam();});
		if (varName == null)
			return;
		else if (hasScl(varName))
			delScl(varName);
		else if (hasMtx(varName))
			delMtx(varName);
		else
			ep.customError("%s can't be deleted because it doesn't exist or isn't a scl or mtx", varName);
	}
	
	public void prn() {
		print(exprReader.unknownExpr());
	}
	
}
