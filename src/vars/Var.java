package vars;

import io.Output;

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
		Output.customError(msg, args);
	}
}

class NULL extends Var {
	@Override
	public String toString() {
		return "Null";
	}
}
