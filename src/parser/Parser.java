package parser;
import java.util.HashMap;
import java.util.Scanner;

import errors.ErrorPrinter;
import tokens.Tk;
import vars.Mtx;
import vars.Scl;

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
		// Initializes tokens
		super(s);
		// Initialize error printer
		ep = new ErrorPrinter(tr);
		// Initialize variable registries
		sclReg = new HashMap<>();
		mtxReg = new HashMap<>();
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
			tr.nextToken();
			// If name is followed by assignment, send to appropriate assignment
			if (tr.tk == Tk.ASSIGNMENT) {
				tr.restartLine();
				if (tr.tk == Tk.SCL_NAME)
					sclReader.SCLASSIGN();
				else
					mtxReader.MTXASSIGN();
			}
			else if (tr.tk == Tk.EOL) {
				// If there is no next token, print out the value of that scalar
				tr.prevToken();
				print(tr.tokenStr());
			}
			else if (Tk.isMathOp(tr.tk) || Tk.isParen(tr.tk)){
				// If it's an expression print out the value of the expression
				tr.restartLine();
				if (tr.tk == Tk.SCL_NAME)
					print(exprReader.<Scl>EXPR());
				else
					print(exprReader.<Mtx>EXPR());
			}
			else {
				ep.expectedError("assignment or arithmetical expression",tr.tokenStr());
			}
		}
		// If the expression starts with a number, evaluate it
		else if (tr.tk == Tk.NUM_LIT | tr.tk == Tk.SUB_OP || tr.tk == Tk.LPAREN) {
			tr.prevToken();
			Object res = exprReader.EXPR();
			if (res instanceof Scl) {
				print((Scl) res);
			}
			else {
				print((Mtx) res);
			}
		}
		// Otherwise, print an error
		else {
			ep.expectedError("expression, command, or assignment", tr.tokenStr());
		}
	}
	
}
