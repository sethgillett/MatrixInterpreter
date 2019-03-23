package io;

import java.util.function.Consumer;

public class Output {
	/**
	 * The default way of getting text onto the screen
	 */
	public static Consumer<String> output = null;
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
	 * Prints a message
	 * @param msg The message
	 */
	public static void print(Object msg) {
		print(msg.toString());
	}
	/**
	 * Actually outputs the message to the screen
	 * @param msg The message
	 */
	private static void print(String msg) {
		output.accept(msg);
	}
}
