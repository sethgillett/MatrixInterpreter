package errors;
import tokens.Tk;
import tokens.TokenReader;

public class ErrorPrinter {
	/**
	 * The token recognizer being used by the parser
	 */
	private TokenReader tr;
	
	/**
	 * Takes in a token recognizer to learn more about errors
	 * @param tr The token recognizer being used by the parser
	 */
	public ErrorPrinter(TokenReader tr) {
		this.tr = tr;
	}
	
	/**
	 * Error in the format of "Expected [expected], got [last token]"
	 * @param expected The expected token
	 */
	public void expectedError(String expected) {
		// Prints pointer to the token that caused the error
		tr.tokenPtr();
		// Prints what token was expected and what token was gotten
		System.out.printf("ERROR\n\nExpected '%s', got '%s'\n", expected, tr.tokenStr());
	}
	
	/**
	 * Error in the format of "Expected [expected_1] or [expected_2] or...or [expected_n], got [last token]"
	 * @param expected The expected token
	 */
	public void expectedError(Tk expected1, Tk...expected) {
		String errorStr = "" + expected1;
		for (Tk exp : expected) {
			errorStr += " or " + exp;
		}
		expectedError(errorStr);
	}
	
	/**
	 * An error with a custom message
	 * @param msg The message to be printed
	 * @param args Any arguments to go with the message
	 */
	public void customError(String msg, Object...args) {
		// Prints the error message and any arguments
		System.out.printf("ERROR: " + msg + "\n", args);
	}
	
	/**
	 * Printer for errors that occur within the software
	 * @param msg The message to be printed
	 * @param args Any additional arguments
	 */
	public void internalError(String msg, Object...args) {
		System.out.printf("INTERNAL ERROR: " + msg + "\n", args);
	}
}
