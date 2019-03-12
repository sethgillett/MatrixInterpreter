package parser;

import java.util.HashMap;
import java.util.Scanner;

import errors.ErrorPrinter;
import tokens.Tk;
import tokens.TokenMatcher;
import tokens.TokenReader;
import vars.mtx.Mtx;
import vars.scl.Scl;

/**
 * Template for all parsers and sub-parsers
 * @author Seth Gillett
 *
 */
public class ParserType {
	/**
	 * The primary scanner used in the program
	 */
	private Scanner scan;
	/**
	 * The parser's token reader
	 */
	protected TokenReader tr;
	/**
	 * Used to print error messages
	 */
	protected ErrorPrinter ep;
	/**
	 * Variable registry for scalars
	 */
	private HashMap<String, Scl> sclReg;
	
	/**
	 * Variable registry for matrices
	 */
	private HashMap<String, Mtx> mtxReg;
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
	 * Reads expressions
	 */
	protected ExprReader exprReader;
	
	/**
	 * For all other parsers
	 * @param s The primary parser
	 */
	protected ParserType(ParserType s) {
		this.scan = s.scan;
		this.tr = s.tr;
		this.ep = s.ep;
		this.sclReg = s.sclReg;
		this.mtxReg = s.mtxReg;
	}
	
	/**
	 * Connects each parser type to all the others
	 * @param s The primary parser
	 */
	public void connect(ParserType s) {
		this.cmdReader = s.cmdReader;
		this.sclReader = s.sclReader;
		this.mtxReader = s.mtxReader;
		this.exprReader = s.exprReader;
	}
	
	/**
	 * For primary parser only
	 * @param s The primary scanner
	 */
	public ParserType(Scanner s) {
		// Set reference to primary scanner
		this.scan = s;
		// Initialize token reader
		this.tr = initTokenReader();
		// Initialize variable registries
		this.sclReg = new HashMap<>();
		this.mtxReg = new HashMap<>();
		// Initialize error printer
		ep = new ErrorPrinter(tr);
	}
	
	/**
	 * Initializes the token reader and the tokens
	 */
	private TokenReader initTokenReader() {
		return new TokenReader(				
			// The new, delete, print, identity, and zero commands
			new TokenMatcher("\\b(new|del|prn|id|zero)\\b", Tk.CMD),
			// The rref, ref, and inverse commands
			new TokenMatcher("\\b(rref|ref|inv)\\b", Tk.CMD),
			// Matrix
			new TokenMatcher("\\b(mat)\\b", Tk.TYPE),
			// Scalar
			new TokenMatcher("\\b(scl)\\b", Tk.TYPE),
			// Name of a matrix: all capital letters
			new TokenMatcher("\\b([A-Z][a-z]*)\\b", Tk.MTX_NAME),
			// Name of a scalar: lower case letter followed by any letters
			new TokenMatcher("\\b([a-z]+)\\b", Tk.SCL_NAME),
			// Left and right paranthesis
			new TokenMatcher("\\(", Tk.LPAREN),
			new TokenMatcher("\\)", Tk.RPAREN),
			// Comma
			new TokenMatcher("\\,", Tk.COMMA),
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
	 * Gets a scalar or returns null and prints an error if it doesn't exist or has no value
	 * @param name The name of the scalar
	 * @return The scalar or null
	 */
	public Scl getScl(String name) {
		if (sclReg.containsKey(name)) {
			Scl scl = sclReg.get(name);
			if (scl == null) {
				ep.customError("'%s' has no value", name);
				return null;
			}
			else {
				return scl;
			}
		}
		else {
			ep.customError("'%s' does not exist", name);
			return null;
		}
	}
	
	/**
	 * Gets a matrix or returns null if it doesn't exist or has no value
	 * @param name The name of the matrix
	 * @return The matrix or null
	 */
	public Mtx getMtx(String name) {
		if (mtxReg.containsKey(name)) {
			Mtx mtx = mtxReg.get(name);
			if (mtx == null) {
				ep.customError("'%s' has no value", name);
				return null;
			}
			else {
				return mtx;
			}
		}
		else {
			ep.customError("'%s' does not exist", name);
			return null;
		}
	}
	
	/**
	 * Adds a scalar to the scalar registry
	 * @param name The name of the scalar
	 * @param scl The scalar
	 */
	public void setScl(String name, Scl scl) {
		sclReg.put(name, scl);
	}
	
	/**
	 * Adds a matrix to the scalar registy
	 * @param name The name of the matrix
	 * @param mtx The matrix
	 */
	public void setMtx(String name, Mtx mtx) {
		mtxReg.put(name, mtx);
	}
	
	/**
	 * Reads in a <i>postitive</i> parameter and returns its value as an int
	 * @return The int value of the parameter <i>or -1</i>
	 */
	public int readPositiveIntParam() {
		tr.nextToken();
		if (tr.tk == Tk.NUM_LIT || tr.tk == Tk.SCL_NAME) {
			Scl rowCount;
			if (tr.tk == Tk.NUM_LIT) {
				rowCount = new Scl(tr.tokenStr());
			}
			else {
				rowCount = getScl(tr.tokenStr());
			}
			
			if (rowCount.isInt() && rowCount.valueAsInt() >= 0) {
				return rowCount.valueAsInt();
			}
			else {
				ep.expectedError("postive integer");
				return -1;
			}
		}
		else {
			ep.expectedError("number or scalar");
			return -1;
		}
	}
	
	/**
	 * Prints out the supplied variable <b><i>if</i></b> it is found in any variable registry
	 * @param varName The name of the variable to print
	 */
	public void print(String varName) {
		if (sclReg.containsKey(varName)) {
			Scl s = sclReg.get(varName);
			if (s == null) {
				ep.customError("Scl '%s' has no value assigned", varName);
			}
			else {
				print(sclReg.get(varName));
			}
		}
		else if (mtxReg.containsKey(varName)) {
			Mtx m = mtxReg.get(varName);
			if (m == null) {
				ep.customError("Mtx '%s' has no value assigned", varName);
			}
			else {
				print(mtxReg.get(varName));
			}
		}
		else {
			ep.customError("'%s' does not exist", varName);
		}
	}
	
	/**
	 * An overrided version of print that <b>directly</b> takes in a scalar or matrix
	 * @param var The scalar or matrix to print
	 */
	protected void print(Object var) {
		if (var != null && (var instanceof Mtx || var instanceof Scl)) {
			System.out.println(var);
		}
		else if (var == null) {
			ep.internalError("Var cannot be printed because it is null");
		}
		else {
			ep.internalError("Var '%s' cannot be printed because it is not of the correct type", var);
		}
	}
	
	/**
	 * Scans a new line with the primary scanner
	 * @return The new line
	 */
	public String scanNewLine() {
		return scan.nextLine();
	}
}
