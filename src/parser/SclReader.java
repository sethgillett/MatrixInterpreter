package parser;

import vars.Scl;

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
		sclReg.put(sclName, var);
	}
	
//	/**
//	 * Evaluates and returns a scalar expression as a scalar
//	 * @param prevTokens Any previous tokens in the expression that were already read over
//	 * @return The scalar result of the following expression
//	 */
//	public Scl SCLEXPR() {
//		// If any part fails return null
//		// Read the expression
//		ArrayList<Object> infix = exprReader.readExpr();
//		// If the expression can't be read return null
//		if (infix == null) return null;
//		// Convert the expression from infix to postfix
//		ArrayList<Object> postfix = exprReader.toPostfix(infix);
//		// If the expression can't be converted return null
//		if (postfix == null) return null;
//		// Evaluate the expression
//		Scl result;
//		try {
//			result = evaluateExpr(postfix);
//		}
//		// If it fails to parse, throw an error
//		catch (Exception e) {
//			//e.printStackTrace();
//			ep.customError("Invalid arithmetic expression");
//			return null;
//		}
//		// Return the result
//		return result;
//	}
	
	
}
