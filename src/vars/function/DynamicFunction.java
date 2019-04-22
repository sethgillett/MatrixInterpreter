package vars.function;

import java.util.List;

import parser.primary.Parser;
import vars.Var;
/**
 * The function type used by the REPL, executes one line at a time
 * @author Seth Gillett
 *
 */
public class DynamicFunction extends Function {
	private String currentLine;
	private Parser primary;
	
	public DynamicFunction(Parser primary) {
		super();
		this.primary = primary;
	}
	/**
	 * Reads in the next line to execute
	 * @param line The next line to execute
	 */
	public void read(String line) {
		this.currentLine = line;
	}
	
	@Override
	public void start(List<Var> params) {
		super.start(params);
	}
	
	@Override
	public Var run() {
		if (!checkStarted())
			return null;
		
		return primary.read(currentLine);
	}

}
