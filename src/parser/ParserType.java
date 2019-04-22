package parser;

import errors.ErrorPrinter;
import io.Input;
import io.Output;
import parser.primary.Parser;
import parser.readers.ControlsReader;
import parser.readers.ExprReader;
import parser.readers.InputReader;
import parser.readers.VarReader;
import tokens.TokenReader;
import vars.Var;
import vars.bool.Bool;
import vars.function.Function;
import vars.mtx.Mtx;
import vars.scl.Scl;

/**
 * Template for all parsers and sub-parsers
 * @author Seth Gillett
 *
 */
public abstract class ParserType {
	/**
	 * The parser's token reader
	 */
	protected static TokenReader tr = new TokenReader();
	/**
	 * Used to print error messages
	 */
	public static ErrorPrinter ep = new ErrorPrinter(tr);
	/**
	 * Deals with assignments to matrices and scalars
	 */
	protected static VarReader varReader;
	/**
	 * Reads expressions
	 */
	protected static ExprReader exprReader;
	/**
	 * Reads function input
	 */
	protected static InputReader inpReader;
	/**
	 * Deals with control statements (if, while, for)
	 */
	protected static ControlsReader controlsReader;
	/**
	 * The input reader
	 */
	public static Input input;
	/**
	 * The primary parser
	 */
	protected static Parser primary;
	/**
	 * The function at the top of the callstack
	 */
	private static Function currentActive;
	
	/**
	 * Sets the current active function to a new function
	 * @param func The function
	 */
	public static void setCurrentActive(Function func) {
		currentActive = func;
	}
	
	/**
	 * Gets the function at the top of the call stack
	 */
	public static Function getCurrentActive() {
		return currentActive;
	}
	
	/**
	 * Prints out the supplied variable <b><i>if</i></b> it is found in any variable registry
	 * @param varName The name of the variable to print
	 * @return Whether the run was successful
	 */
	public static boolean print(String varName) {
		return currentActive.printVar(varName);
	}
	
	
	/**
	 * An overrided version of print that <b>directly</b> takes in a scalar, matrix, function, or bool
	 * @param var The scalar or matrix to print
	 * @return 
	 */
	protected static boolean print(Var var) {
		if (var != null) {
			Output.println(var);
			return true;
		}
		return false;
	}
	
	/**
	 * Attempts to get a var by name
	 * @param name The name of the var
	 * @return The var
	 */
	public static Var getVar(String name) {
		return currentActive.getLocalVar(name);
	}
	
	public static void setVar(String name, Var val) {
		currentActive.setLocalVar(name, val);
	}
	
	public static boolean hasVar(String name) {
		return currentActive.hasLocalVar(name);
	}
	
	public static Scl getScl(String name) {
		Var var = getVar(name);
		if (var instanceof Scl) {
			return (Scl) var;
		}
		else {
			ep.customError("Expected scalar, got %s", name);
			return null;
		}
	}
	
	public static Mtx getMtx(String name) {
		Var var = getVar(name);
		if (var instanceof Scl) {
			return (Mtx) var;
		}
		else {
			ep.customError("Expected matrix, got %s", name);
			return null;
		}
	}
	
	public static Bool getBool(String name) {
		Var var = getVar(name);
		if (var instanceof Bool) {
			return (Bool) var;
		}
		else {
			ep.customError("Expected bool, got %s", name);
			return null;
		}
	}
	
	public static Function getFunc(String name) {
		Var var = getVar(name);
		if (var instanceof Function) {
			return (Function) var;
		}
		else {
			ep.customError("Expected function, got %s", name);
			return null;
		}
	}
}
