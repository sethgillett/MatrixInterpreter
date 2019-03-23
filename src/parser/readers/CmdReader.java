package parser.readers;

import parser.primary.ParserType;
import vars.mtx.Mtx;

public class CmdReader extends ParserType {
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
	/**
	 * Performs a command with no return type
	 * @return Whether the run was successful
	 */
	public boolean nullCmd() {
		tr.nextToken();
		String tokenLit = tr.tokenStr();
		switch (tokenLit) {
		case "del":
			return del();
		case "prn":
		case "print":
			return prn();
		default:
			ep.customError("Command %s does not exist", tokenLit);
			return false;
		}
	}
	/**
	 * Attempts to delete a variable from the registries
	 * @return Whether the run was successful
	 */
	public boolean del() {
		String varName = inpReader.<String>readParam(() -> {return inpReader.readStrParam();});
		if (varName == null)
			return false;
		else if (hasScl(varName))
			return delScl(varName);
		else if (hasMtx(varName))
			return delMtx(varName);
		else
			ep.customError("%s can't be deleted because it doesn't exist or isn't a scl or mtx", varName);
		return false;
	}
	/**
	 * Attempts to print the result of an expression
	 * @return Whether the run was successful
	 */
	public boolean prn() {
		return print(exprReader.expr(null));
	}
	
}
