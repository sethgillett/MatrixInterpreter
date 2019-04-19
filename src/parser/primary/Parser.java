package parser.primary;

import io.Input;
import parser.readers.ControlsReader;
import parser.readers.ExprReader;
import parser.readers.InputReader;
import parser.readers.VarReader;
import tokens.Tk;
import vars.Var;
import vars.bool.Bool;

/**
 * Primary parser/interpreter for the program
 * @author Seth Gillett
 *
 */
public class Parser extends ParserType {	
	/**
	 * Instantiates the token reader, error printer, and all other classes
	 * @param s The primary scanner
	 */
	public Parser(Input input) {
		// Initializes token reader, registries, and error printer
		super();
		// Initialize sub parsers
		varReader = new VarReader();
		exprReader = new ExprReader();
		inpReader = new InputReader();
		controlsReader = new ControlsReader();
	}
	
	/**
	 * Reads in a new line
	 * @param line The new line read in
	 * @return Whether the run was successful <b>or a returned variable</b>
	 */
	public Var read(String line) {
		tr.readLine(line);
		tr.nextToken();
		// Blank line - does nothing
		if (tr.tk == Tk.EOL) {
			return Bool.True;
		}
		// Look for a control statement
		else if (Tk.isControlTk(tr.tk)) {
			switch(tr.tk) {
			case IF:
				return controlsReader.ifStmt();
//			case FOR:
//				return controlsReader.for_stmt();
//			case WHILE:
//				return controlsReader.while_stmt();
//			case DEF:
//				return controlsReader.function_stmt();
			default:
				return null;
			}
		}
		// If an expression has a scl or mtx name
		else if (tr.tk == Tk.VAR_NAME) {
			// Next token
			tr.nextToken();
			// If name is followed by assignment, send to appropriate assignment
			if (tr.tk == Tk.ASSIGNMENT_OP) {
				tr.restartLine();
				return varReader.varAssign()? Var.Null : null;
			}
			else if (Tk.isExprTk(tr.tk)) {
				// If it's an expression print out the value of the expression
				tr.restartLine();
				print(exprReader.evalExpr(null));
				return Bool.Null;
			}
			else if (tr.tk == Tk.EOL) {
				// If there is no next token, print out the value of that scalar
				tr.prevToken();
				return print(tr.tokenStr())? Bool.Null : null;
			}
			else {
				ep.expectedError("assignment or arithmetical expression");
				return Bool.False;
			}
		}
		// The statement is an expression
		else if (Tk.isExprTk(tr.tk)) {
			// If it's an expression print out the value of the expression
			tr.restartLine();
			return print(exprReader.evalExpr(exprReader.getPostfixExpr()))? Var.Null : null;
		}
		// The statement is a return statement
		else if (tr.tk == Tk.RETURN) {
			return exprReader.evalExpr(exprReader.getPostfixExpr());
		}
		// Otherwise, print an error
		else {
			ep.expectedError("arithmetic expression, command, or assignment");
			return Bool.False;
		}
	}
	
}
