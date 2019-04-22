package vars;

import parser.ParserType;

public abstract class Var {
	/**
	 * The null value
	 */
	public static Var Null = new NULL();
	/**
	 * Throws an error using ParserType's error printer
	 * @param msg The message
	 * @param args Additional args
	 */
	public static void throwError(String msg, Object...args) {
		ParserType.ep.customError(msg, args);
	}
}

class NULL extends Var {}
