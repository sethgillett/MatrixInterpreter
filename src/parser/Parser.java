package parser;
import java.util.HashMap;
import java.util.Scanner;

import errors.ErrorPrinter;
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
	public Parser(Scanner s) {
		// Initializes tokens
		super(s);
		// Initialize error printer
		ep = new ErrorPrinter(tr);
		// Initialize variable registries
		sclReg = new HashMap<>();
		mtxReg = new HashMap<>();
		// LAST - Initialize sub parsers
		cmdReader = new CmdReader(this);
		sclReader = new SclReader(this);
		mtxReader = new MtxReader(this);
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
			//TODO
			System.out.println("No commands have been programmed yet");
		}
		// If an expression has a scl name
		else if (tr.tk == Tk.SCL_NAME) {
			tr.nextToken();
			// If scl name is followed by assignment, send to scl assignment
			if (tr.tk == Tk.ASSIGNMENT) {
				tr.restartLine();
				sclReader.SCLASSIGN();
			}
			else if (tr.tk == Tk.EOL) {
				// If there is no next token, print out the value of that scalar
				tr.prevToken();
				print(tr.tokenStr());
			}
			else if (Tk.isMathOp(tr.tk) || Tk.isParen(tr.tk)){
				// If it's an expression print out the value of the expression
				tr.restartLine();
				print(sclReader.SCLEXPR());
			}
			else {
				ep.expectedError("assignment or arithmetical expression",tr.tokenStr());
			}
		}
		// If an expression has a mtx name
		else if (tr.tk == Tk.MTX_NAME) {
			tr.nextToken();
			// If mtx name is followed by assignment, send to mtx assignment
			if (tr.tk == Tk.ASSIGNMENT) {
				tr.restartLine();
				mtxReader.MTXASSIGN();
			}
			else if (tr.tk == Tk.EOL) {
				// If there is no next token, print out the value of that matrix
				tr.prevToken();
				print(tr.tokenStr());
			}
			else if (Tk.isMathOp(tr.tk) || Tk.isParen(tr.tk)){
				// If it's an expression print out the value of the expression
				tr.restartLine();
				print(mtxReader.MTXEXPR());
			}
			else {
				ep.expectedError("assignment or arithmetical expression",tr.tokenStr());
			}
		}
		// If the expression starts with a number, evaluate it
		else if (tr.tk == Tk.NUM_LIT | tr.tk == Tk.SUB_OP || tr.tk == Tk.LPAREN) {
			tr.prevToken();
			print(sclReader.SCLEXPR());
		}
		// Otherwise, print an error
		else {
			ep.expectedError("expression, command, or assignment", tr.tokenStr());
		}
	}
	
}
