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
	 * The primary parser
	 */
	protected Parser p;
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
	 * For primary parser only
	 */
	public ParserType() {
		// Do nothing - for primary parser only
	}
}
