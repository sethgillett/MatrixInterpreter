package errors;
import io.Output;
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
	 * Checks the current token and reports an error if it doesn't match the expected token
	 * @param expected The expected token
	 * @return Whether they match
	 */
	public boolean checkToken(Tk expected) {
		if (tr.tk != expected) {
			expectedError(expected);
			return false;
		}
		return true;
	}
	/**
	 * Error in the format of "Expected [expected], got [last token]"
	 * @param expected The expected token
	 */
	public void expectedError(String expected) {
		new Exception().printStackTrace();
		// Prints pointer to the token that caused the error
		tr.tokenPtr();
		// Prints what token was expected and what token was gotten
		Output.printf("\nERROR: Expected %s, got %s\n", expected, tr.tokenStr());
	}
	/**
	 * An error with a custom message
	 * @param msg The message to be printed
	 * @param args Any arguments to go with the message
	 */
	public void customError(String msg, Object...args) {
		new Exception().printStackTrace();
		// Prints the error message and any arguments
		Output.printf("ERROR: " + msg + "\n", args);
	}
	
	/**
	 * Printer for errors that occur within the software
	 * @param msg The message to be printed
	 * @param args Any additional arguments
	 */
	public void internalError(String msg, Object...args) {
		new Exception().printStackTrace();
		// Prints the error message and any arguments
		Output.printf("INTERNAL ERROR: " + msg + "\n", args);
	}
}
