package parser.primary;

import java.util.HashMap;

import errors.ErrorPrinter;
import parser.CmdReader;
import parser.ControlsReader;
import parser.ExprReader;
import parser.InputReader;
import parser.VarReader;
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
	 * Deals with assignments to matrices and scalars
	 */
	protected VarReader varReader;
	/**
	 * Reads expressions
	 */
	protected ExprReader exprReader;
	/**
	 * Reads function input
	 */
	protected InputReader inpReader;
	/**
	 * Deals with control statements (if, while, for)
	 */
	protected ControlsReader controlsReader;
	
	/**
	 * For all other parsers
	 * @param s The primary parser
	 */
	protected ParserType(ParserType s) {
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
		this.varReader = s.varReader;
		this.exprReader = s.exprReader;
		this.inpReader = s.inpReader;
		this.controlsReader = s.controlsReader;
	}
	
	/**
	 * For primary parser only
	 * @param s The primary scanner
	 */
	public ParserType() {
		// Initialize token reader
		this.tr = new TokenReader();
		// Initialize variable registries
		this.sclReg = new HashMap<>();
		this.mtxReg = new HashMap<>();
		// Initialize error printer
		ep = new ErrorPrinter(tr);
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
	 * Gets a matrix or returns null and throws an error if it doesn't exist or has no value
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
	 * Returns true if the specified scalar exists
	 * @param name The name of the scalar
	 * @return True or false
	 */
	public boolean hasScl(String name) {
		return sclReg.containsKey(name);
	}
	
	/**
	 * Returns true if the specified matrix exists
	 * @param name The name of the matrix
	 * @return True or false
	 */
	public boolean hasMtx(String name) {
		return mtxReg.containsKey(name);
	}
	
	/**
	 * Deletes a scalar or throws an error if it doesn't exist
	 * @param name The name of the scalar
	 */
	public void delScl(String name) {
		if (hasScl(name)) {
			this.sclReg.remove(name);
		}
		else {
			ep.customError("Scalar %s doesn't exist and can't be deleted", name);
		}
	}
	
	/**
	 * Deletes a matrix or throws an error if it doesn't exist
	 * @param name The name of the matrix
	 */
	public void delMtx(String name) {
		if (hasMtx(name)) {
			this.mtxReg.remove(name);
		}
		else {
			ep.customError("Matrix %s doesn't exist and can't be deleted", name);
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
		if (var != null && (var instanceof Mtx || var instanceof Scl || var instanceof Boolean)) {
			System.out.println(var);
		}
		else if (var == null) {
//			ep.internalError("Var cannot be printed because it is null");
		}
		else {
			ep.internalError("Var '%s' cannot be printed because it is not of the correct type", var);
		}
	}
}
