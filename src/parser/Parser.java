package parser;
import java.util.Scanner;

import tokens.Tk;
import vars.mtx.Mtx;
import vars.scl.Scl;

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
	public Parser(Scanner s) {
		// Initializes token reader, registries, and error printer
		super(s);
		// Initialize sub parsers
		cmdReader = new CmdReader(this);
		sclReader = new SclReader(this);
		mtxReader = new MtxReader(this);
		exprReader = new ExprReader(this);
		// Connect all the parsers
		cmdReader.connect(this);
		sclReader.connect(this);
		mtxReader.connect(this);
		exprReader.connect(this);
	}
	
	/**
	 * Reads in a new line
	 * @param line The new line read in
	 */
	public void read(String line) {
		tr.readLine(line);
		tr.nextToken();
		// Look for a command
		if (tr.tk == Tk.CMD) {
			String cmd = tr.tokenStr();
			tr.nextToken();
			if (tr.tk == Tk.LPAREN) {
				tr.nextToken();
				switch (cmd) {
				case "prn":
					String varName = tr.tokenStr();
					print(varName);
					break;
				default:
					ep.customError("No commands have been programmed yet");
					break;
				}
			}
		}
		// If an expression has a scl or mtx name
		else if (tr.tk == Tk.SCL_NAME || tr.tk == Tk.MTX_NAME) {
			// Record the type for reference
			Tk type = tr.tk;
			// Next token
			tr.nextToken();
			// If name is followed by assignment, send to appropriate assignment
			if (tr.tk == Tk.ASSIGNMENT) {
				tr.nextToken();
				if (type == Tk.SCL_NAME) {
					tr.restartLine();
					sclReader.SCLASSIGN();
				}
				else {
					tr.restartLine();
					mtxReader.MTXASSIGN();
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
				print(exprReader.UNKNOWNEXPR());
			}
			else {
				ep.expectedError("assignment or arithmetical expression",tr.tokenStr());
			}
		}
		// If the expression starts with a number, evaluate it
		else if (tr.tk == Tk.NUM_LIT | tr.tk == Tk.SUB_OP || tr.tk == Tk.LPAREN) {
			tr.prevToken();
			Object res = exprReader.UNKNOWNEXPR();
			if (res instanceof Scl) {
				print((Scl) res);
			}
			else {
				print((Mtx) res);
			}
		}
		// Otherwise, print an error
		else {
			ep.expectedError("arithmetic expression, command, or assignment", tr.tokenStr());
		}
	}
	
}
