package io;

public class Output {
	/**
	 * Prints a message plus a newline
	 * @param msg The message
	 */
	public static void println(Object msg) {
		print(msg + "\n");
	}
	/**
	 * Prints a message with formatting
	 * @param msg The message
	 * @param args The formatting arguments
	 */
	public static void printf(String msg, Object...args) {
		print(String.format(msg, args));
	}
	/**
	 * Actually outputs the message to the screen
	 * @param msg The message
	 */
	public static void print(Object msg) {
		System.out.print(msg);
  }
	/**
	 * Checks the current token and raises an expected error if it doesn't match the expected token
	 * @param expected The expected token
   * @param received The received token
	 * @return Whether they match
	 */
	public static boolean hardCheck(Object expected, Object received) {
		if (!expected.equals(received)) {
			expectedError(expected, received);
			return false;
		}
		return true;
	}
	/**
	 * Error in the format of "Expected [expected], got [last token]"
	 * @param expected The expected token
   * @param received The received token
	 */
	public static void expectedError(Object expected, Object received) {
		new Exception().printStackTrace();
		// Prints what token was expected and what token was gotten
		Output.printf("\nERROR: Expected %s, got %s\n", expected, received);
	}
	/**
	 * An error with a custom message
	 * @param msg The message to be printed
	 * @param args Any arguments to go with the message
	 */
	public static void customError(String msg, Object...args) {
		new Exception().printStackTrace();
		// Prints the error message and any arguments
		Output.printf("ERROR: " + msg + "\n", args);
	}
	
	/**
	 * Printer for errors that occur within the software
	 * @param msg The message to be printed
	 * @param args Any additional arguments
	 */
	public static void internalError(String msg, Object...args) {
		new Exception().printStackTrace();
		// Prints the error message and any arguments
		Output.printf("INTERNAL ERROR: " + msg + "\n", args);
	}
}
