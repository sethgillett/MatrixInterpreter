package parser.primary;

import java.util.HashMap;

import errors.ErrorPrinter;
import io.Input;
import io.Output;
import parser.functions.Function;
import parser.readers.CmdReader;
import parser.readers.ControlsReader;
import parser.readers.ExprReader;
import parser.readers.InputReader;
import parser.readers.VarReader;
import tokens.TokenReader;
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
	protected static ErrorPrinter ep = new ErrorPrinter(tr);
	/**
	 * Variable registry for scalars
	 */
	protected static HashMap<String, Scl> sclReg = new HashMap<>();
	
	/**
	 * Variable registry for matrices
	 */
	protected static HashMap<String, Mtx> mtxReg = new HashMap<>();
	/**
	 * Function registry
	 */
	protected static HashMap<String, Function> funcReg = new HashMap<>();
	/**
	 * Deals with all direct commands
	 */
	protected static CmdReader cmdReader;
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
	protected static Input input;
	/**
	 * The primary parser
	 */
	protected static Parser primary;
	
	/**
	 * Gets a scalar or returns null and prints an error if it doesn't exist or has no value
	 * @param name The name of the scalar
	 * @return The scalar or null
	 */
	public static Scl getScl(String name) {
		if (hasScl(name)) {
			Scl scl = sclReg.get(name);
			if (scl == null) {
				ep.customError("'%s' has no value", name);
				return null;
			}
			else {
				return scl;
			}
		}
		else {
			ep.customError("'%s' does not exist", name);
			return null;
		}
	}
	
	/**
	 * Gets a matrix or returns null and throws an error if it doesn't exist or has no value
	 * @param name The name of the matrix
	 * @return The matrix or null
	 */
	public static Mtx getMtx(String name) {
		if (hasMtx(name)) {
			Mtx mtx = mtxReg.get(name);
			if (mtx == null) {
				ep.customError("'%s' has no value", name);
				return null;
			}
			else {
				return mtx;
			}
		}
		else {
			ep.customError("'%s' does not exist", name);
			return null;
		}
	}
	
	/**
	 * Gets a function by name
	 * @param name The name of the function
	 * @return The function
	 */
	public static Function getFunc(String name) {
		if (hasFunc(name)) {
			Function func = funcReg.get(name);
			if (func == null) {
				ep.internalError("Function %s not registered", name);
				return null;
			}
			else {
				return func;
			}
		}
		else {
			ep.customError("Function %s does not exist", name);
			return null;
		}
	}
	
	/**
	 * Adds a scalar to the scalar registry
	 * @param name The name of the scalar
	 * @param scl The scalar
	 */
	public static void setScl(String name, Scl scl) {
		sclReg.put(name, scl);
	}
	
	/**
	 * Adds a matrix to the scalar registy
	 * @param name The name of the matrix
	 * @param mtx The matrix
	 */
	public static void setMtx(String name, Mtx mtx) {
		mtxReg.put(name, mtx);
	}
	
	/**
	 * Adds a function to the function registry
	 * @param name The name of the function
	 * @param func The function
	 */
	public static void setFunc(String name, Function func) {
		funcReg.put(name, func);
	}
	
	/**
	 * Returns true if the specified scalar exists
	 * @param name The name of the scalar
	 * @return True or false
	 */
	public static boolean hasScl(String name) {
		return sclReg.containsKey(name);
	}
	
	/**
	 * Returns true if the specified matrix exists
	 * @param name The name of the matrix
	 * @return True or false
	 */
	public static boolean hasMtx(String name) {
		return mtxReg.containsKey(name);
	}
	
	/**
	 * Returns true if the specified function exists
	 * @param name The name of the function
	 * @return True or false
	 */
	public static boolean hasFunc(String name) {
		return funcReg.containsKey(name);
	}
	
	/**
	 * Deletes a scalar or throws an error if it doesn't exist
	 * @param name The name of the scalar
	 * @return 
	 */
	public static boolean delScl(String name) {
		if (hasScl(name)) {
			sclReg.remove(name);
			return true;
		}
		else {
			ep.customError("Scalar %s doesn't exist and can't be deleted", name);
			return false;
		}
	}
	
	/**
	 * Deletes a matrix or throws an error if it doesn't exist
	 * @param name The name of the matrix
	 * @return Whether the run was successful
	 */
	public static boolean delMtx(String name) {
		if (hasMtx(name)) {
			mtxReg.remove(name);
			return true;
		}
		else {
			ep.customError("Matrix %s doesn't exist and can't be deleted", name);
			return false;
		}
	}
	
	/**
	 * Prints out the supplied variable <b><i>if</i></b> it is found in any variable registry
	 * @param varName The name of the variable to print
	 * @return Whether the run was successful
	 */
	public static boolean print(String varName) {
		if (sclReg.containsKey(varName)) {
			Scl s = sclReg.get(varName);
			if (s == null) {
				ep.customError("Scl '%s' has no value assigned", varName);
				return false;
			}
			else {
				return print(sclReg.get(varName));
			}
		}
		else if (mtxReg.containsKey(varName)) {
			Mtx m = mtxReg.get(varName);
			if (m == null) {
				ep.customError("Mtx '%s' has no value assigned", varName);
				return false;
			}
			else {
				print(mtxReg.get(varName));
				return true;
			}
		}
		else {
			ep.customError("'%s' does not exist", varName);
			return false;
		}
	}
	
	
	/**
	 * An overrided version of print that <b>directly</b> takes in a scalar or matrix
	 * @param var The scalar or matrix to print
	 * @return 
	 */
	protected static boolean print(Object var) {
		if (var instanceof Scl) {
			Output.println(var);
			return true;
		}
		else if (var instanceof Boolean) {
			Output.println((Boolean) var? "True":"False");
			return true;
		}
		else if (var instanceof Mtx) {
			Output.println("Mtx =");
			Output.println(var);
			return true;
		}
		else if (var instanceof String) {
			return print((String) var);
		}
		else if (var == null) {
//			ep.internalError("Var cannot be printed because it is null");
			return false;
		}
		else {
			ep.internalError("Var '%s' cannot be printed because it is not of the correct type", var);
			return false;
		}
	}
}
