package parser.primary;

import io.Input;
import parser.readers.CmdReader;
import parser.readers.ControlsReader;
import parser.readers.ExprReader;
import parser.readers.InputReader;
import parser.readers.VarReader;
import tokens.Tk;

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
		cmdReader = new CmdReader();
		varReader = new VarReader();
		exprReader = new ExprReader();
		inpReader = new InputReader();
		controlsReader = new ControlsReader();
	}
	
	/**
	 * Reads in a new line
	 * @param line The new line read in
	 * @return Whether the run was successful
	 */
	public boolean read(String line) {
		tr.readLine(line);
		tr.nextToken();
		// Blank line - does nothing
		if (tr.tk == Tk.EOL) {
			return true;
		}
		// Look for a command
		if (tr.tk == Tk.NULL_CMD) {
			tr.prevToken();
			return cmdReader.nullCmd();
		}
		// Look for a control statement
		else if (Tk.isControlTk(tr.tk)) {
			switch(tr.tk) {
			case IF:
				return controlsReader.if_stmt();
			case FOR:
				return controlsReader.for_stmt();
			case WHILE:
				return controlsReader.while_stmt();
//			case DEF:
//				return controlsReader.function_stmt();
			default:
				return false;
			}
		}
		// If an expression has a scl or mtx name
		else if (tr.tk == Tk.SCL_NAME || tr.tk == Tk.MTX_NAME) {
			// Record the type for reference
			Tk type = tr.tk;
			// Next token
			tr.nextToken();
			// If name is followed by assignment, send to appropriate assignment
			if (tr.tk == Tk.ASSIGNMENT_OP) {
				tr.nextToken();
				if (type == Tk.SCL_NAME) {
					tr.restartLine();
					return varReader.sclAssign();
				}
				else {
					tr.restartLine();
					return varReader.mtxAssign();
				}
			}
			else if (Tk.isExprTk(tr.tk)) {
				// If it's an expression print out the value of the expression
				tr.restartLine();
				return print(exprReader.expr(null));
			}
			else if (tr.tk == Tk.EOL) {
				// If there is no next token, print out the value of that scalar
				tr.prevToken();
				return print(tr.tokenStr());
			}
			else {
				ep.expectedError("assignment or arithmetical expression");
				return false;
			}
		}
		// The statement is an expression
		else if (Tk.isExprTk(tr.tk)) {
			// If it's an expression print out the value of the expression
			tr.restartLine();
			return print(exprReader.expr(null));
		}
		// Otherwise, print an error
		else {
			ep.expectedError("arithmetic expression, command, or assignment");
			return false;
		}
	}
	
}
