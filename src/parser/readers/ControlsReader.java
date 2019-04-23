package parser.readers;

import java.util.ArrayList;
import java.util.List;

import parser.ParserType;
import tokens.Tk;
import vars.Var;
import vars.bool.Bool;
import vars.function.Function;
import vars.function.MIFunction;
import vars.scl.Scl;

public class ControlsReader extends ParserType {
	
	/**
	 * <p>if condition:
	 * 	<li>stmt</li>
	 * 	<li>stmt</li>
	 * 	<li>stmt</li>
	 * </p>
	 * @return The var returned (if any)
	 */
	public Var ifStmt() {
		// IF token flagged
		Bool ifCondition = exprReader.boolExpr(exprReader.getPostfixExpr());
		if (ifCondition == null)
			return null;
		if (ifCondition == Bool.False)
			return Bool.Null;
		tr.nextToken();
		if (tr.tk == Tk.COLON) {
			tr.nextToken();
			if (tr.tk == Tk.EOL) {
				ArrayList<String> stmts = new ArrayList<>();
				String newLine = input.readLine();
				while (!newLine.matches("\\s*\\b(?:end)\\b")) {
					// Only executes the line if the if statement was true
					if (ifCondition.val())
						stmts.add(newLine);
					newLine = input.readLine();
				}
				for (String stmt : stmts) {
					Var result = primary.read(stmt);
					// ERROR
					if (result == null) {
						return null;
					}
					// no return value
					else if (primary.read(stmt) == Var.Null) {
						continue;
					}
					// return value
					else {
						return result;
					}
				}
				return Bool.True;
			}
			else {
				ep.expectedError(Tk.EOL);
			}
		}
		else {
			ep.customError("Expected : after if statement");
		}
		return null;
	}
	/**
	 * <p>while condition:
	 * 	<li>stmt</li>
	 * 	<li>stmt</li>
	 * 	<li>stmt</li>
	 * </p>
	 * @return The var returned (if any)
	 */
	public Var whileStmt() {
		// WHILE token flagged
		List<Object> whileExpr = exprReader.getPostfixExpr();
		Bool whileCondition = exprReader.boolExpr(whileExpr);
		if (whileCondition == null)
			return null;
		if (whileCondition == Bool.False)
			return null;
		tr.nextToken();
		if (tr.tk == Tk.COLON) {
			tr.nextToken();
			if (ep.checkToken(Tk.EOL)) {
				List<String> stmts = new ArrayList<>();
				String newLine = input.readLine();
				while (!newLine.matches("\\s*\\b(?:end)\\b")) {
					// Only executes the line if the if statement was true
					if (whileCondition.val()) {
						stmts.add(newLine);
					}
					newLine = input.readLine();
				}
				while (whileCondition.val()) {
					for (String stmt : stmts) {
						Var result = primary.read(stmt);
						// ERROR
						if (result == null) {
							return null;
						}
						// no return value
						else if (primary.read(stmt) == Var.Null) {
							continue;
						}
						// return value
						else {
							return result;
						}
					}
					whileCondition = exprReader.boolExpr(whileExpr);
				}
				return Bool.Null;
			}
		}
		else {
			ep.customError("Expected : after while statement");
		}
		return null;
	}
	
	public Var forStmt() {
		// FOR token flagged
		tr.nextToken();
		if (ep.checkToken(Tk.VAR_NAME)) {
			String iterName = tr.tokenStr();
			tr.nextToken();
			if (ep.checkToken(Tk.IN)) {
				Scl start;
				start = exprReader.sclExpr(exprReader.getPostfixExpr());
				if (start == null) {
					ep.expectedError(Tk.NUM_LIT, Tk.VAR_NAME);
					return null;
				}
				tr.nextToken();
				if (ep.checkToken(Tk.ARROW)) {
					Scl end;
					end = exprReader.sclExpr(null);
					if (end == null) {
						ep.expectedError(Tk.NUM_LIT, Tk.VAR_NAME);
						return null;
					}
					tr.nextToken();
					if (ep.checkToken(Tk.COLON)) {
						List<String> stmts = new ArrayList<>();
						String newLine = input.readLine();
						while (!newLine.matches("\\s*\\b(?:end)\\b")) {
							stmts.add(newLine);
							newLine = input.readLine();
						}
						Scl iterator = new Scl(start);
						setVar(iterName, iterator);
						while (Scl.lesser(iterator, end)) {
							for (String stmt : stmts) {
								if (primary.read(stmt) == null) {
									return null;
								}
							}
							iterator = Scl.add(iterator, Scl.ONE);
							setVar(iterName, iterator);
						}
						return Var.Null;
					}
				}
			}
		}
		return null;
	}
	
	public boolean funcStmt() {
		// DEF token flagged
		tr.nextToken();
		if (ep.checkToken(Tk.FUNC_NAME)) {
			String funcName = tr.tokenStr();
			List<String> paramNames = inpReader.readDefinedParams();
			tr.nextToken();
			if (ep.checkToken(Tk.COLON)) {
				int colonCount = 1;
				List<String> lines = new ArrayList<>();
				String newLine = input.readLine();
				// Once colonCount == 0, 
				while (colonCount > 0) {
					// Make sure # :'s matches # end's
					// Colon to be matched
					if (newLine.matches(".*:\\s*$"))
						colonCount += 1;
					// end matches colon
					if (newLine.matches("\\s*\\b(?:end)\\b"))
						colonCount -= 1;
					
					lines.add(newLine);
					newLine = input.readLine();
				}
				Function func = new MIFunction(primary, paramNames, lines);
				setVar(funcName, func);
				return true;
			}
		}
		return false;
	}
}
