package parser;

import errors.ErrorPrinter;
import tokens.TokenReader;

/**
 * Template for all parsers and sub-parsers
 * @author Seth Gillett
 *
 */
public class ParserType {
	/**
	 * The parser's token reader
	 */
	public TokenReader tr;
	/**
	 * Used to print error messages
	 */
	public ErrorPrinter ep;
	/**
	 * The primary parser
	 */
	public Parser p;
	
	public ParserType(Parser primary) {
		this.p = primary;
		this.tr = this.p.tr;
		this.ep = this.p.ep;
	}
}
