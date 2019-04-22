package tokens;

import java.util.List;

import parser.primary.ParserType;
import vars.Var;
import vars.function.Function;

public class FuncToken {
	/**
	 * The name of the function
	 */
	private String name;
	/**
	 * Parameters to be passed to the function
	 */
	private List<Var> params;
	
	public FuncToken(String name, List<Var> params) {
		this.name = name;
		this.params = params;
	}
	
	public Var run() {
		Function func = ParserType.getFunc(name);
		func.start(params);
		return func.run();
	}
}
