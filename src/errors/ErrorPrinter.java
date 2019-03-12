package errors;
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
	 * Error in the format of "Expected [expected], got [found]"
	 * @param expected The expected token
	 * @param found The token that was found
	 */
	public void expectedError(String expected, String found) {
		// Prints pointer to the token that caused the error
		tr.tokenPtr();
		// Prints what token was expected and what token was gotten
		System.out.printf("ERROR\n\nExpected '%s', got '%s'\n", expected, found);
	}
	
	/**
	 * Error in the format of "Expected [expected], got [last token]"
	 * @param expected The expected token
	 */
	public void expectedError(String expected) {
		expectedError(expected, tr.tokenStr());
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
