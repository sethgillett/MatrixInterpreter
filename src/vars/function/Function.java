package vars.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.Output;
import parser.primary.Parser;
import parser.primary.ParserType;
import vars.Var;

public class Function extends Var {
	/**
	 * Lines of code in the function
	 */
	private List<String> lines;
	/**
	 * Names of parameters in the function
	 */
	private List<String> paramsList;
	private Map<String, Var> varReg;
	private Parser primary;
	private Function parent;
	private boolean started;
	
	private int lineNumber;
	
	private Var returnValue;
	
	/**
	 * Initializes a function with an arraylist of lines of code and an arraylist of parameters
	 * @param lines List of lines of code
	 * @param parameters List of parameters
	 */
	public Function(Parser primary, Function parent, List<String> paramsList, List<String> lines) {
		this.primary = primary;
		this.parent = parent;
		this.paramsList = paramsList;
		this.lines = lines;
		this.started = false;
	}

	/**
	 * Starts the function by passing in the list of parameter values
	 * @param params The values of the parameters the function takes in (in order)
	 * @return Whether the run was successful
	 */
	public void start(List<Var> params) {
		lineNumber = 0;
		varReg = new HashMap<String, Var>();
		
		for (int i=0; i<paramsList.size(); i++) {
			String name = paramsList.get(i);
			Var val = Var.Null;
			if (i < params.size()) {
				val = params.get(i);
			}
			setLocalVar(name, val);
		}
		// Sets this function as the current active function
		ParserType.setCurrentActive(this);
		// Marks that the function has started
		started = true;
	}
	
	/**
	 * Attempts to start and then execute every line in a function
	 * @param params The values (in order) of the function's parameters
	 * @return Returns java's null if failed or a var (including var's null) if successful
	 */
	public Var run(List<Var> params) {
		this.start(params);
		for (int i=0; i<lines.size(); i++) {
			if (this.execNextLine()) {
				continue;
			}
			else {
				return null;
			}
		}
		this.close();
		
		if (this.returnValue == null) {
			return Var.Null;
		}
		else {
			return this.returnValue;
		}
	}
	
	/**
	 * Determines whether there is another line to execute
	 * @return True or false
	 */
	public boolean hasNextLine() {
		return (lineNumber < lines.size());
	}
	
	/**
	 * Attempts to execute the next line in the function (specified by lineNumber)
	 * @return Whether the run was successful
	 */
	public boolean execNextLine() {
		if (!started) {
			ParserType.ep.internalError("Function not started before executing a line");
			return false;
		}
		
		if (lineNumber == lines.size()) {
			return false;
		}
		
		primary.read(lines.get(lineNumber));
		lineNumber ++;
		
		return true;
	}
	
	/**
	 * Sets ParserType's current active function to this function's parent
	 */
	public void close() {
		ParserType.setCurrentActive(this.parent);
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
}
