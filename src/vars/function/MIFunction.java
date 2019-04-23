package vars.function;

import java.util.Arrays;
import java.util.List;

import io.Input;
import parser.ParserType;
import parser.primary.Parser;
import vars.Var;

public class MIFunction extends Function {
	/**
	 * Lines of code in the function
	 */
	private String[] lines;
	/**
	 * Names of parameters in the function
	 */
	private String[] paramNames;
	private Parser primary;
	
	private int lineNumber;
	/**
	 * The input that was present before this function was started
	 */
	private Input parentInput;
	
	/**
	 * Initializes a function with an arraylist of lines of code and an arraylist of parameters
	 * @param lines List of lines of code
	 * @param parameters List of parameters
	 */
	public MIFunction(Parser primary, List<String> paramNames, List<String> lines) {
		super();
		this.primary = primary;
		this.paramNames = new String[paramNames.size()];
		this.paramNames = paramNames.toArray(this.paramNames);
		this.lines = new String[lines.size()];
		this.lines = lines.toArray(this.lines);
	}

	@Override
	public void start(List<Var> params) {
		super.start(params);
		ParserType.input = new Input(
				// Returns next line in function or null if it doesn't exist
				() -> {return (lineNumber < lines.length)? lines[lineNumber++] : null;},
				// Sets input back to the parent input
				() -> {ParserType.input = parentInput;}
				);
		// Start at this line in the function
		lineNumber = 0;
		
		for (int i=0; i<paramNames.length; i++) {
			String name = paramNames[i];
			Var val = Var.Null;
			if (i < params.size()) {
				val = params.get(i);
			}
			setLocalVar(name, val);
		}
	}
	
	@Override
	public void close() {
		// Sets the current active function to the parent
		super.close();
		// Closes input off this function
		ParserType.input.close();
	}
	
	@Override
	public Var run() {
		if (!checkStarted())
			return null;

		for (int i=0; i<lines.length; i++) {
			Var result = this.execNextLine();
			if (result == null)
				return null;
			else if (result == Var.Null)
				continue;
			else
				return result;
		}
		this.close();
		
		return Var.Null;
	}
	/**
	 * Determines whether there is another line to execute
	 * @return True or false
	 */
	public boolean hasNextLine() {
		return (lineNumber < lines.length);
	}
	
	/**
	 * Attempts to execute the next line in the function (specified by lineNumber)
	 * @return Whether the run was successful
	 */
	private Var execNextLine() {
		if (lineNumber == lines.length) {
			ParserType.ep.internalError("No further lines to execute");
			return null;
		}
		
		return primary.read(lines[lineNumber++]);
	}
	
	@Override
	public String toString() {
		return String.format("MIFunction: %s(%s)", "function", Arrays.toString(paramNames));
	}
}
