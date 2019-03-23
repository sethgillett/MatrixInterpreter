package parser.readers;

import java.util.ArrayList;

import parser.primary.ParserType;
import tokens.Tk;
import vars.scl.Scl;

public class ControlsReader extends ParserType {
	
	public boolean if_stmt() {
		// IF token flagged
		Boolean result = exprReader.boolExpr(null);
		if (result == null)
			return false;
		tr.nextToken();
		if (tr.tk == Tk.COLON) {
			tr.nextToken();
			if (tr.tk == Tk.EOL) {
				ArrayList<String> stmts = new ArrayList<>();
				String newLine = input.readLine();
				while (!newLine.matches("\\s*\\b(?:end)\\b")) {
					// Only executes the line if the if statement was true
					if (result)
						stmts.add(newLine);
					newLine = input.readLine();
				}
				for (String stmt : stmts) {
					if (primary.read(stmt) == false) {
						return false;
					}
				}
				return true;
			}
			else {
				ep.expectedError(Tk.EOL);
			}
		}
		else {
			ep.customError("Expected : after if statement");
		}
		return false;
	}
	
	public boolean while_stmt() {
		// WHILE token flagged
		ArrayList<Object> whileExpr = exprReader.getExpr();
		Boolean result = exprReader.boolExpr(whileExpr);
		if (result == null)
			return false;
		tr.nextToken();
		if (tr.tk == Tk.COLON) {
			tr.nextToken();
			if (ep.checkToken(Tk.EOL)) {
				ArrayList<String> stmts = new ArrayList<>();
				String newLine = input.readLine();
				while (!newLine.matches("\\s*\\b(?:end)\\b")) {
					// Only executes the line if the if statement was true
					if (result) {
						stmts.add(newLine);
					}
					newLine = input.readLine();
				}
				while (result) {
					for (String stmt : stmts) {
						if (primary.read(stmt) == false) {
							return false;
						}
					}
					result = exprReader.boolExpr(whileExpr);
				}
				return true;
			}
		}
		else {
			ep.customError("Expected : after while statement");
		}
		return false;
	}
	
	public boolean for_stmt() {
		// FOR token flagged
		tr.nextToken();
		if (ep.checkToken(Tk.SCL_NAME)) {
			String iterName = tr.tokenStr();
			tr.nextToken();
			if (ep.checkToken(Tk.IN)) {
				Scl start;
				start = exprReader.sclExpr(null);
				if (start == null) {
					ep.expectedError(Tk.NUM_LIT, Tk.SCL_NAME);
					return false;
				}
				tr.nextToken();
				if (ep.checkToken(Tk.ARROW)) {
					Scl end;
					end = exprReader.sclExpr(null);
					if (end == null) {
						ep.expectedError(Tk.NUM_LIT, Tk.SCL_NAME);
						return false;
					}
					tr.nextToken();
					if (ep.checkToken(Tk.COLON)) {
						ArrayList<String> stmts = new ArrayList<>();
						String newLine = input.readLine();
						while (!newLine.matches("\\s*\\b(?:end)\\b")) {
							stmts.add(newLine);
							newLine = input.readLine();
						}
						Scl iterator = new Scl(start);
						setScl(iterName, iterator);
						while (Scl.lesser(iterator, end)) {
							for (String stmt : stmts) {
								if (primary.read(stmt) == false) {
									return false;
								}
							}
							iterator = Scl.add(iterator, Scl.ONE);
							setScl(iterName, iterator);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
//	public boolean function_stmt() {
//		// DEF token flagged
//		tr.nextToken();
//		if (ep.checkToken(Tk.FUNC_NAME)) {
//			tr.nextToken();
//			if (ep.checkToken(Tk.LPAREN)) {
//				//TODO: FINISH!!!
//			}
//		}
//		else {
//			ep.expectedError(Tk.FUNC_NAME);
//		}
//	}
	
}
