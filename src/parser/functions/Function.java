package parser.functions;

import java.util.ArrayList;
import java.util.HashMap;

import parser.primary.ParserType;
import tokens.ReadToken;
import tokens.Tk;
import vars.mtx.Mtx;
import vars.scl.Scl;

public class Function extends ParserType {
	
	private ArrayList<CompiledLine> lines;
	private HashMap<String, Scl> localSclReg;
	private HashMap<String, Mtx> localMtxReg;
	private HashMap<String, Function> localFuncReg;
	private boolean started;
	
	private int lineNumber;
	
	public ArrayList<ReadToken> parameters;
	/**
	 * Initializes a function with an arraylist of lines of code and an arraylist of parameters
	 * @param lines List of lines of code
	 * @param parameters List of parameters
	 */
	public Function(ArrayList<CompiledLine> lines, ArrayList<ReadToken> parameters) {
		this.lines = lines;
		this.parameters = parameters;
		this.started = false;
	}
	/**
	 * Starts the function by passing in the list of parameter values
	 * @param paramsList The parameter value list
	 * @return Whether the run was successful
	 */
	public boolean start(ArrayList<Object> paramsList) {
		localSclReg = new HashMap<>();
		localMtxReg = new HashMap<>();
		localFuncReg = new HashMap<>();
		lineNumber = 0;
		if (parameters.size() != paramsList.size()) {
			ep.customError("Function needed %d arguments but got %d arguments",
					parameters.size(),
					paramsList.size());
			return false;
		}
		for (int i=0; i<parameters.size(); i++) {
			Object param = paramsList.get(i);
			String name = parameters.get(i).tokenStr();
			if (parameters.get(i).token == Tk.SCL_NAME && param instanceof Scl) {
				sclReg.put(name, (Scl) param);
			}
			else if (parameters.get(i).token == Tk.MTX_NAME && param instanceof Mtx) {
				mtxReg.put(name, (Mtx) param);
			}
			else if (parameters.get(i).token == Tk.FUNC_NAME && param instanceof Function) {
				funcReg.put(name, (Function) param);
			}
			else {
				ep.customError("Argument %d '%s' of wrong type", i, param);
				return false;
			}
		}
		started = true;
		return true;
	}
//	/**
//	 * Attempts to execute the next line
//	 * @return Whether the run was successful
//	 */
//	public boolean execNextLine() {
//		if (!started) {
//			ep.internalError("Function hasn't been started");
//			return false;
//		}
//		if (lineNumber >= lines.size()) {
//			ep.internalError("Can't execute next line, it doesn't exist");
//			return false;
//		}
//		CompiledLine current = lines.get(lineNumber);
//		Object param = current.get(0);
//		if (param instanceof ReadToken) {
//			
//		}
//		else {
//			ep.internalError("Param %s must be an instance of ReadToken", param);
//			return false;
//		}
//	}
	/**
	 * Determines whether there is another line to execute
	 * @return True or false
	 */
	public boolean hasNextLine() {
		return (lineNumber < lines.size());
	}
	
	
	
	public boolean hasLocalScl(String name) {
		return (hasScl(name) || localSclReg.containsKey(name));
	}
	
	public boolean hasLocalMtx(String name) {
		return (hasMtx(name) || localMtxReg.containsKey(name));
	}
	
	public boolean hasLocalFunc(String name) {
		return (hasFunc(name) || localFuncReg.containsKey(name));
	}
	
	public static CompiledLine compileLine(String line) {
		ArrayList<Object> lineSequence = new ArrayList<Object>();
		tr.readLine(line);
		while (tr.peekNextToken() != Tk.EOL) {
			tr.nextToken();
			if (tr.tk == Tk.NULL_CMD) {
				lineSequence.add(new ReadToken(tr.tk, tr.tokenStr()));
			}
			else if (Tk.isControlTk(tr.tk)) {
				lineSequence.add(new ReadToken(tr.tk, tr.tokenStr()));
			}
			else if (tr.tk == Tk.SCL_NAME || tr.tk == Tk.MTX_NAME) {
				lineSequence.add(new ReadToken(tr.tk, tr.tokenStr()));
				tr.nextToken();
				if (ep.checkToken(Tk.ASSIGNMENT_OP)) {
					lineSequence.add(new ReadToken(Tk.ASSIGNMENT_OP));
					ArrayList<Object> expr = exprReader.getExpr();
					if (expr == null)
						return null;
					lineSequence.add(expr);
				}
				else {
					return null;
				}
			}
			// The statement is an expression
			else if (Tk.isExprTk(tr.tk)) {
				tr.restartLine();
				ArrayList<Object> expr = exprReader.getExpr();
				if (expr == null)
					return null;
				lineSequence.add(expr);
			}
			// Otherwise, print an error
			else {
				ep.expectedError("Command or assignment");
				return null;
			}
			
		}
		lineSequence.add(Tk.EOL);
		return new CompiledLine(lineSequence);
	}
	public static String[] readLines() {
		return null;
	}
}
