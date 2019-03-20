package parser.primary;
import java.io.BufferedReader;

import parser.CmdReader;
import parser.ControlsReader;
import parser.ExprReader;
import parser.InputReader;
import parser.VarReader;
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
	public Parser(BufferedReader br) {
		// Initializes token reader, registries, and error printer
		super();
		// Initialize sub parsers
		cmdReader = new CmdReader(this);
		varReader = new VarReader(this);
		exprReader = new ExprReader(this);
		inpReader = new InputReader(this, br);
		controlsReader = new ControlsReader(this);
		// Connect all the parsers
		cmdReader.connect(this);
		varReader.connect(this);
		exprReader.connect(this);
		inpReader.connect(this);
		controlsReader.connect(this);
	}
	
	/**
	 * Reads in a new line
	 * @param line The new line read in
	 */
	public void read(String line) {
		tr.readLine(line);
		tr.nextToken();
		// Look for a command
		if (tr.tk == Tk.NULL_CMD) {
			tr.prevToken();
			cmdReader.nullCmd();
		}
		// Look for a control statement
		else if (tr.tk == Tk.IF || tr.tk == Tk.WHILE || tr.tk == Tk.FOR) {
			Tk token = tr.tk;
			tr.restartLine();
			switch(token) {
			case IF:
				controlsReader.if_stmt();
				break;
			case FOR:
				controlsReader.for_stmt();
				break;
			case WHILE:
				controlsReader.while_stmt();
				break;
			default:
				break;
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
					varReader.sclAssign();
				}
				else {
					tr.restartLine();
					varReader.mtxAssign();
				}
			}
			else if (tr.tk == Tk.EOL) {
				// If there is no next token, print out the value of that scalar
				tr.prevToken();
				print(tr.tokenStr());
			}
			else if (Tk.isMathOp(tr.tk) || Tk.isParen(tr.tk)){
				// If it's an expression print out the value of the expression
				tr.restartLine();
				print(exprReader.unknownExpr());
			}
			else {
				ep.expectedError("assignment or arithmetical expression",tr.tokenStr());
			}
		}
		// If the expression starts with a number, evaluate it
		else if (tr.tk == Tk.NUM_LIT | tr.tk == Tk.SUB_OP || tr.tk == Tk.LPAREN) {
			tr.prevToken();
			// Find the result and print it
			Object res = exprReader.unknownExpr();
			print(res);
		}
		// Otherwise, print an error
		else {
			ep.expectedError("arithmetic expression, command, or assignment", tr.tokenStr());
		}
	}
	
}
