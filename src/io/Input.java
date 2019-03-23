package io;

import java.util.concurrent.Callable;

public class Input {
	/**
	 * Callable method to get user input
	 */
	private Callable<String> input = null;
	/**
	 * Runnable to close input stream
	 */
	private Runnable closeMethod = null;
	/**
	 * Sets the method used to collect user input
	 * @param input The method used to collect input
	 */
	public Input(Callable<String> input, Runnable closeMethod) {
		this.input = input;
		this.closeMethod = closeMethod;
	}
	/**
	 * Reads a new line from input
	 * @return The new line
	 */
	public String readLine() {
		try {
			return input.call();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Closes the input stream
	 */
	public void close() {
		closeMethod.run();
	}
}
