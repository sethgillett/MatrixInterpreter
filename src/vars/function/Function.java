package vars.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.Output;
import parser.ParserType;
import vars.Var;

public abstract class Function extends Var {
	/**
	 * The parameters passed into this function
	 */
	protected List<Var> params;
	/**
	 * Whether start has been called or not
	 */
	private boolean started;
	/**
	 * The function's registry for local variables
	 */
	private Map<String, Var> varReg;
	/**
	 * The function below this one in the call stack
	 */
	private Function parent;
	/**
	 * Initializes a function with a given parent function in the call stack
	 * @param parent The parent function
	 */
	public Function() {
		this.started = false;
		this.varReg = new HashMap<>();
	}
	/**
	 * Attempts to run the function
	 * @return Returns java's null if failed or a var (including var's null) if successful
	 */
	public abstract Var run();
	/**
	 * Makes the function active and passes in startup parameters
	 * @param params The function's parameters
	 */
	public void start(List<Var> params) {
		// The function above this one in the call stack
		this.parent = ParserType.getCurrentActive();
		// Stores the list of parameters
		this.params = params;
		// Sets this function as the current active function
		ParserType.setCurrentActive(this);
		// Marks that start has been called
		this.started = true;
	}
	/**
	 * Throws an error if the function hasn't been started yet
	 */
	public boolean checkStarted() {
		if (!this.started) {
			throwError("Function cannot be run because it hasn't been started yet");
			return false;
		}
		return true;
	}
	/**
	 * Checks to see whether this function or its parents have a var by that name
	 * @param name The name of the var
	 * @return True or false
	 */
	public boolean hasLocalVar(String name) {
		return (varReg.containsKey(name) || 
				(parent != null && parent.hasLocalVar(name)));
	}
	/**
	 * Checks all registries for a variable, reports an error if it's not found
	 * @param name The name of the variable
	 * @return The variable or null
	 */
	public Var getLocalVar(String name) {
		Var var = null;
		boolean found = true;
		
		if (hasLocalVar(name)) {
			var = varReg.get(name);
			found = true;
		}
		else {
			found = false;
		}
		
		if (var != null) {
			return var;
		}
		else {
			if (found) {
				ParserType.ep.internalError("Var '%s' has no value", name);
			}
			else {
				if (parent == null) {
					ParserType.ep.customError("Var '%s' does not exist", name);
				}
				else {
					return parent.getLocalVar(name);
				}
			}
			return null;
		}
		
	}
	/**
	 * Adds a var to the local var reg
	 * @param name The name of the var
	 * @param val The var
	 */
	public void setLocalVar(String name, Var val) {
		varReg.put(name, val);
	}
	/**
	 * Attempts to print a var
	 * @param name The name of the var
	 * @return Whether the var was printed successfully
	 */
	public boolean printVar(String name) {
		Var var = getLocalVar(name);
		if (var != null) {
			Output.println(var);
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * Sets ParserType's current active function to this function's parent
	 */
	public void close() {
		ParserType.setCurrentActive(this.parent);
	}
	
}
