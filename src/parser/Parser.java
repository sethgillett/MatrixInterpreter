package parser;
import java.util.HashMap;

import errors.ErrorPrinter;
import tokens.Tk;
import tokens.TokenMatcher;
import tokens.TokenReader;

/**
 * Primary parser/interpreter for the program
 * @author Seth Gillett
 *
 */
public class Parser extends ParserType {	
	/**
	 * Instantiates the token reader, error printer, and all other classes
	 */
	public Parser() {
		// Does nothing - this is the primary parser
		super();
		// Initialize token reader and tokens
		this.initTokenReader();
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
	 * Initializes the token reader and the tokens
	 */
	private void initTokenReader() {
		tr = new TokenReader(				
			// The new, delete, print, and input commands
			new TokenMatcher("\\b(new|del|prn|inp)\\b", Tk.CMD),
			// The rref, ref, and inverse commands
			new TokenMatcher("\\b(rref|ref|inv)\\b", Tk.CMD),
			// Matrix
			new TokenMatcher("\\b(mat)\\b", Tk.TYPE),
			// Scalar
			new TokenMatcher("\\b(scl)\\b", Tk.TYPE),
			// Name of a matrix: all capital letters
			new TokenMatcher("\\b([A-Z]+)\\b", Tk.MTX_NAME),
			// Name of a scalar: lower case letter followed by any letters
			new TokenMatcher("\\b([a-z][A-Za-z]*)\\b", Tk.SCL_NAME),
			// Left and right paranthesis
			new TokenMatcher("\\(", Tk.LPAREN),
			new TokenMatcher("\\)", Tk.RPAREN),
			// Left and right brackets
			new TokenMatcher("\\[", Tk.LBRACKET),
			new TokenMatcher("\\]", Tk.RBRACKET),
			// Arithmetic operators
			new TokenMatcher("\\+", Tk.ADD_OP),
			new TokenMatcher("\\-", Tk.SUB_OP),
			// NO RECOGNIZER FOR NEGATE OPERATOR, it cannot be differentiated from SUB
			new TokenMatcher("\\*", Tk.MULT_OP),
			new TokenMatcher("\\/", Tk.DIV_OP),
			new TokenMatcher("\\^", Tk.EXP_OP),
			// Number literal
			new TokenMatcher("(?:\\d+)?(?:\\.?\\d+)(?:[Ee][+-]?\\d+)?", Tk.NUM_LIT),
			// Assignment operator
			new TokenMatcher("=", Tk.ASSIGNMENT)
		);
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
		// If an expression has a mtx name, send it to mtx assignment method
		else if (tr.tk == Tk.MTX_NAME) {
			mtxReader.MTXASSIGN();
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
