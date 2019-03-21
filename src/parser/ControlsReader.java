package parser;

import java.util.ArrayList;

import parser.primary.Parser;
import parser.primary.ParserType;
import tokens.Tk;
import vars.scl.Scl;

public class ControlsReader extends ParserType {

	public ControlsReader(Parser primary) {
		super(primary);
	}
	
	public boolean if_stmt() {
		Boolean result = exprReader.boolExpr(null);
		if (result == null)
			return false;
		tr.nextToken();
		if (tr.tk == Tk.COLON) {
			tr.nextToken();
			if (tr.tk == Tk.EOL) {
				ArrayList<String> stmts = new ArrayList<>();
				String newLine = primaryReader.readNewLine();
				while (!newLine.matches("\\s*\\b(?:end)\\b")) {
					// Only executes the line if the if statement was true
					if (result)
						stmts.add(newLine);
					newLine = primaryReader.readNewLine();
				}
				for (String stmt : stmts) {
					if (primaryReader.read(stmt) == false) {
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
		ArrayList<Object> whileExpr = exprReader.getExpr();
		Boolean result = exprReader.boolExpr(whileExpr);
		if (result == null)
			return false;
		tr.nextToken();
		if (tr.tk == Tk.COLON) {
			tr.nextToken();
			if (tr.tk == Tk.EOL) {
				ArrayList<String> stmts = new ArrayList<>();
				String newLine = primaryReader.readNewLine();
				while (!newLine.matches("\\s*\\b(?:end)\\b")) {
					// Only executes the line if the if statement was true
					if (result) {
						stmts.add(newLine);
					}
					newLine = primaryReader.readNewLine();
				}
				while (result) {
					for (String stmt : stmts) {
						if (primaryReader.read(stmt) == false) {
							return false;
						}
					}
					result = exprReader.boolExpr(whileExpr);
				}
				return true;
			}
			else {
				ep.expectedError(Tk.EOL);
			}
		}
		else {
			ep.customError("Expected : after while statement");
		}
		return false;
	}
	
	public boolean for_stmt() {
		tr.nextToken();
		if (tr.tk == Tk.SCL_NAME) {
			String iterName = tr.tokenStr();
			tr.nextToken();
			if (tr.tk == Tk.IN) {
				Scl start;
				start = exprReader.sclExpr(null);
				if (start == null) {
					ep.expectedError(Tk.NUM_LIT, Tk.SCL_NAME);
					return false;
				}
				tr.nextToken();
				if (tr.tk == Tk.ARROW) {
					Scl end;
					end = exprReader.sclExpr(null);
					if (end == null) {
						ep.expectedError(Tk.NUM_LIT, Tk.SCL_NAME);
						return false;
					}
					tr.nextToken();
					if (tr.tk == Tk.COLON) {
						ArrayList<String> stmts = new ArrayList<>();
						String newLine = primaryReader.readNewLine();
						while (!newLine.matches("\\s*\\b(?:end)\\b")) {
							stmts.add(newLine);
							newLine = primaryReader.readNewLine();
						}
						Scl iterator = new Scl(start);
						this.setScl(iterName, iterator);
						while (Scl.lesser(iterator, end)) {
							for (String stmt : stmts) {
								if (primaryReader.read(stmt) == false) {
									return false;
								}
							}
							iterator = Scl.add(iterator, Scl.ONE);
							this.setScl(iterName, iterator);
						}
						return true;
					}
					else {
						ep.expectedError(Tk.COLON);
					}
				}
				else {
					ep.expectedError(Tk.ARROW);
				}
			}
			else {
				ep.expectedError(Tk.IN);
			}
		}
		else {
			ep.expectedError(Tk.SCL_NAME);
		}
		return false;
	}
	
}
