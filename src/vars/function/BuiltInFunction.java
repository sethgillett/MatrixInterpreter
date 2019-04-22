package vars.function;

import java.util.List;

import vars.Var;

public class BuiltInFunction extends Function {
	/**
	 * The method that will be run when this function is called
	 */
	private java.util.function.Function<List<Var>, Var> method;
	/**
	 * Initializes a built in function with a parent function and a java function to execute
	 * @param parent The parent function
	 * @param method The java method to execute
	 */
	public BuiltInFunction(java.util.function.Function<List<Var>, Var> method) {
		super();
		this.method = method;
	}

	@Override
	public Var run() {
		if (!checkStarted())
			return null;
		
		return method.apply(params);
	}
	
	@Override
	public void start(List<Var> params) {
		super.start(params);
	}
}
