package parser;

import java.util.HashMap;

import errors.ErrorPrinter;
import tokens.TokenReader;
import vars.Mtx;
import vars.Scl;

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
	protected HashMap<String, Scl> sclReg;
	
	/**
	 * Variable registry for matrices
	 */
	protected HashMap<String, Mtx> mtxReg;
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
	 * For all other parsers
	 * @param primary The primary parser
	 */
	public ParserType(ParserType primary) {
		this.tr = primary.tr;
		this.ep = primary.ep;
		this.sclReg = primary.sclReg;
		this.mtxReg = primary.mtxReg;
		this.cmdReader = primary.cmdReader;
		this.sclReader = primary.sclReader;
		this.mtxReader = primary.mtxReader;
	}
	
	/**
	 * Gets a scalar or returns null if it doesn't exist or has no value
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
	
	/**
	 * An overrided version of print that directly takes in a scalar
	 * @param scl The scalar to print
	 */
	protected void print(Scl scl) {
		if (scl != null)
			System.out.println(scl);
	}
	
	/**
	 * An override version of print that directly takes in a matrix
	 * @param mtx The matrix to print
	 */
//	private void print(Mtx mtx) {
//		System.out.println(mtx);
//	}
	
	/**
	 * For primary parser only
	 */
	public ParserType() {
		// Do nothing - for primary parser only
	}
}
