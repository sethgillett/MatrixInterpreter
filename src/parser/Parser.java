package parser;
import java.util.HashMap;

import errors.ErrorPrinter;
import tokens.Tk;
import tokens.TokenMatcher;
import tokens.TokenReader;
import vars.Mtx;
import vars.Scl;

/**
 * Primary parser/interpreter for the program
 * @author Seth Gillett
 *
 */
public class Parser {
	/**
	 * The parser's token reader
	 */
	public TokenReader tr;
	/**
	 * Used to print error messages
	 */
	public ErrorPrinter ep;
	/**
	 * Variable registry for scalars
	 */
	public HashMap<String, Scl> sclReg;
	
	/**
	 * Variable registry for matrices
	 */
	public HashMap<String, Mtx> mtxReg;
	/**
	 * Deals with all direct commands
	 */
	protected CmdReader cmdReader;
	/**
	 * Deals with assignments to scalars
	 */
	protected SclReader sclReader;
	/**
	 * Deals with assignments to matrices
	 */
	protected MtxReader mtxReader;
	
	/**
	 * Instantiates the token reader, error printer, and all other classes
	 */
	public Parser() {
		// Initialize token reader and tokens
		this.initTokenReader();
		// Initialize error printer
		ep = new ErrorPrinter(tr);
		// Initialize sub parsers
		cmdReader = new CmdReader(this);
		sclReader = new SclReader(this);
		mtxReader = new MtxReader(this);
		// Initialize variable registries
		sclReg = new HashMap<>();
		mtxReg = new HashMap<>();
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
			new TokenMatcher("\\b([A-Z]+)\\b", Tk.MTXNAME),
			// Name of a scalar: lower case letter followed by any letters
			new TokenMatcher("\\b([a-z][A-Za-z]*)\\b", Tk.SCLNAME),
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
			new TokenMatcher("(?:\\d+)?(?:\\.?\\d+)(?:[Ee][+-]?\\d+)?", Tk.NUMLIT),
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
		}
		// If an expression has a scl name, send it to scl assignment method
		else if (tr.tk == Tk.SCLNAME) {
			sclReader.SCLASSIGN();
		}
		// If an expression has a mtx name, send it to mtx assignment method
		else if (tr.tk == Tk.MTXNAME) {
			mtxReader.MTXASSIGN();
		}
		// If the expression starts with a number, evaluate it
		else if (tr.tk == Tk.NUMLIT) {
			this.sclReg.put("ans",(sclReader.evaluateExpr(sclReader.readExpr(new Scl(tr.lastTokenStr())))));
			print("ans");
		}
		// If the expression starts with a negative sign, evaluate it
		else if (tr.tk == Tk.SUB_OP) {
			this.sclReg.put("ans",(sclReader.evaluateExpr(sclReader.readExpr(Tk.NEG_OP))));
			print("ans");
		}
		// If the expression starts with paranthesis, evaluate it
		else if (tr.tk == Tk.LPAREN) {
			this.sclReg.put("ans",(sclReader.evaluateExpr(sclReader.readExpr(tr.tk))));
			print("ans");
		}
		// Otherwise, print an error
		else {
			ep.expectedError("cmd, scl, mtx, or assignment", tr.lastTokenStr());
		}
	}
	
	/**
	 * Prints out the supplied variable if it is found in any variable registry
	 * @param varName The name of the variable to print
	 */
	public void print(String varName) {
		if (sclReg.containsKey(varName)) {
			Scl s = sclReg.get(varName);
			if (s == null) {
				System.out.println("Scl '" + varName + "' has no value assigned");
			}
			else {
				System.out.println(sclReg.get(varName));
			}
		}
		else if (mtxReg.containsKey(varName)) {
			Mtx m = mtxReg.get(varName);
			if (m == null) {
				ep.customError("Mtx '%s' has no value assigned", varName);
			}
			else {
				System.out.println(mtxReg.get(varName));
			}
		}
		else {
			ep.customError("'%s' does not exist", varName);
		}
	}
	
}
