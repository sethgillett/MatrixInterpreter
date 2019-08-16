package parser.primary;

import java.util.List<Object>;
import java.util.ArrayList<Object>;

import io.Input;
import io.Output;
import parser.VarContainer;
import parser.readers.ExprReader;
import parser.readers.InputReader;
import parser.readers.VarReader;
import tokens.Tk;
import tokens.TokenReader;
import vars.Var;
import vars.bool.Bool;
import vars.mtx.Mtx;
import vars.scl.Scl;

/**
 * Primary parser/interpreter for the program
 * @author Seth Gillett
 *
 */
public class Parser {
  /**
  * The parser's token reader
  */
  protected static TokenReader tr = new TokenReader();
  /**
    * Deals with assignments to matrices and scalars
    */
  protected static VarReader varReader;
  /**
    * Reads expressions
    */
  protected static ExprReader exprReader;
  /**
    * Reads function Input
    */
  protected static InputReader inpReader;
  /**
    * Deals with control statements (if, while, for)
    */
  protected static ControlsReader controlsReader;
  /**
    * The primary parser
    */
  public static Parser primary;
  /**
    * The variable container in lieu of function scoping
    */
  private static VarContainer currentActive = new VarContainer();
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
      Output.customError("Expected scalar, got %s", name);
      return null;
    }
  }
  
  public static Mtx getMtx(String name) {
    Var var = getVar(name);
    if (var instanceof Scl) {
      return (Mtx) var;
    }
    else {
      Output.customError("Expected matrix, got %s", name);
      return null;
    }
  }
  
  public static Bool getBool(String name) {
    Var var = getVar(name);
    if (var instanceof Bool) {
      return (Bool) var;
    }
    else {
      Output.customError("Expected bool, got %s", name);
      return null;
    }
  }
  
  @Override
  public String toString() {
    return "Current Tk: " + tr.tk;
  }
	/**
	 * Instantiates the token reader, error printer, and all other classes
	 */
	public Parser() {
		// Initializes token reader, registries, and error printer
		super();
		// Initialize sub parsers
		varReader = new VarReader();
		exprReader = new ExprReader();
		inpReader = new InputReader();
		controlsReader = new ControlsReader();
	}
	
	/**
	 * Reads in a new line
	 * @param line The new line read in
	 * @return Whether the run was successful <b>or a returned variable</b>
	 */
	public Var read(String line) {
		tr.readLine(line);
		tr.nextToken();
		// Blank line - does nothing
		if (tr.tk == Tk.EOL) {
			return Bool.True;
		}
		// Look for a control statement
		else if (Tk.isControlTk(tr.tk)) {
			switch(tr.tk) {
			case IF:
				return controlsReader.ifStmt();
			case FOR:
				return controlsReader.forStmt();
			case WHILE:
				return controlsReader.whileStmt();
			default:
				return null;
			}
		}
		// If an expression has a scl or mtx name
		else if (tr.tk == Tk.VAR_NAME) {
			// Next token
			tr.nextToken();
			// If name is followed by assignment, send to appropriate assignment
			if (tr.tk == Tk.ASSIGNMENT_OP) {
				tr.restartLine();
				return varReader.varAssign()? Var.Null : null;
			}
			else if (Tk.isExprTk(tr.tk)) {
				// If it's an expression print out the value of the expression
				tr.restartLine();
				print(exprReader.evalExpr(exprReader.getPostfixExpr()));
				return Bool.Null;
			}
			else if (tr.tk == Tk.EOL) {
				// If there is no next token, print out the value of that scalar
				tr.prevToken();
				return print(tr.tokenStr())? Bool.Null : null;
			}
			else {
				Output.expectedError("assignment or arithmetical expression", tr.tk);
				return Bool.False;
			}
		}
		// The statement is an expression
		else if (Tk.isExprTk(tr.tk)) {
			// If it's an expression print out the value of the expression
			tr.restartLine();
			return print(exprReader.evalExpr(exprReader.getPostfixExpr()))? Var.Null : null;
		}
		// The statement is a return statement
		else if (tr.tk == Tk.RETURN) {
			return exprReader.evalExpr(exprReader.getPostfixExpr());
		}
		// Otherwise, print an error
		else {
			Output.expectedError("arithmetic expression, command, or assignment", tr.tk);
			return Bool.False;
		}
	}
	
}

abstract class ControlsReader {
	/**
	 * <p>if condition:
	 * 	<li>stmt</li>
	 * 	<li>stmt</li>
	 * 	<li>stmt</li>
	 * </p>
	 * @return The var returned (if any)
	 */
	public Var ifStmt() {
		// IF token flagged
		Bool ifCondition = exprReader.boolExpr(exprReader.getPostfixExpr());
		if (ifCondition == null)
			return null;
		if (ifCondition == Bool.False)
			return Bool.Null;
		tr.nextToken();
		if (tr.tk == Tk.COLON) {
			tr.nextToken();
			if (tr.tk == Tk.EOL) {
				ArrayList<String> stmts = new ArrayList<>();
				String newLine = Input.readLine();
				// If newLine can't be read
				if (newLine == null) {
					Output.customError("No additional lines found after if statement");
					return null;
				}
				while (newLine != null && !newLine.matches("\\s*\\b(?:end)\\b")) {
					// Only executes the line if the if statement was true
					if (ifCondition.val())
						stmts.add(newLine);
					newLine = Input.readLine();
				}
				// If no end statement has been found
				if (newLine == null) {
					Output.customError("No end statement found after if statement");
					return null;
				}
				for (String stmt : stmts) {
					Var result = primary.read(stmt);
					// ERROR
					if (result == null) {
						return null;
					}
					// no return value
					else if (primary.read(stmt) == Var.Null) {
						continue;
					}
					// return value
					else {
						return result;
					}
				}
				return Bool.True;
			}
			else {
				ep.expectedError(Tk.EOL);
			}
		}
		else {
			Output.customError("Expected : after if statement");
		}
		return null;
	}
	/**
	 * <p>while condition:
	 * 	<li>stmt</li>
	 * 	<li>stmt</li>
	 * 	<li>stmt</li>
	 * </p>
	 * @return The var returned (if any)
	 */
	public Var whileStmt() {
		// WHILE token flagged
		List<Object> whileExpr = exprReader.getPostfixExpr();
		Bool whileCondition = exprReader.boolExpr(whileExpr);
		if (whileCondition == null)
			return null;
		if (whileCondition == Bool.False)
			return null;
		tr.nextToken();
		if (tr.tk == Tk.COLON) {
			tr.nextToken();
			if (ep.hardCheck(Tk.EOL)) {
				List<String> stmts = new ArrayList<>();
				// Reads a line from current active Input
				String newLine = Input.readLine();
				// If newLine can't be read
				if (newLine == null) {
					Output.customError("No additional lines found after while statement");
					return null;
				}
				while (newLine != null && !newLine.matches("\\s*\\b(?:end)\\b")) {
					// Only executes the line if the if statement was true
					if (whileCondition.val()) {
						stmts.add(newLine);
					}
					newLine = Input.readLine();
				}
				// If no end statement has been found
				if (newLine == null) {
					Output.customError("No end statement found after while statement");
					return null;
				}
				while (whileCondition.val()) {
					for (String stmt : stmts) {
						Var result = primary.read(stmt);
						// ERROR
						if (result == null) {
							return null;
						}
						// no return value
						else if (primary.read(stmt) == Var.Null) {
							continue;
						}
						// return value
						else {
							return result;
						}
					}
					whileCondition = exprReader.boolExpr(whileExpr);
				}
				return Bool.Null;
			}
		}
		else {
			Output.customError("Expected : after while statement");
		}
		return null;
	}
	
	public Var forStmt() {
		// FOR token flagged
		tr.nextToken();
		if (ep.hardCheck(Tk.VAR_NAME)) {
			String iterName = tr.tokenStr();
			tr.nextToken();
			if (ep.hardCheck(Tk.IN)) {
				Scl start;
				start = exprReader.sclExpr(exprReader.getPostfixExpr());
				if (start == null) {
					ep.expectedError(Tk.NUM_LIT, Tk.VAR_NAME);
					return null;
				}
				tr.nextToken();
				if (ep.hardCheck(Tk.ARROW)) {
					Scl end;
					end = exprReader.sclExpr(null);
					if (end == null) {
						ep.expectedError(Tk.NUM_LIT, Tk.VAR_NAME);
						return null;
					}
					tr.nextToken();
					if (ep.hardCheck(Tk.COLON)) {
						List<String> stmts = new ArrayList<>();
						String newLine = Input.readLine();
						// If newLine can't be read
						if (newLine == null) {
							Output.customError("No end statement found after for statement");
							return null;
						}
						while (newLine != null && !newLine.matches("\\s*\\b(?:end)\\b")) {
							stmts.add(newLine);
							newLine = Input.readLine();
						}
						// If no end statement has been found
						if (newLine == null) {
							Output.customError("No end statement found after for statement");
							return null;
						}
						Scl iterator = new Scl(start);
						setVar(iterName, iterator);
						while (Scl.lesser(iterator, end)) {
							for (String stmt : stmts) {
								if (primary.read(stmt) == null) {
									return null;
								}
							}
							iterator = Scl.add(iterator, Scl.ONE);
							setVar(iterName, iterator);
						}
						return Var.Null;
					}
				}
			}
		}
		return null;
	}
}

abstract class ExprReader {
	
	public Bool boolExpr(List<Object> postfix) {
		Var result = evalExpr(postfix);
		if (result instanceof Bool) {
			return (Bool) result;
		}
		else {
			Output.customError("Expression didn't result in bool");
			return null;
		}
	}
	
	public Scl sclExpr(List<Object> postfix) {
		Var result = evalExpr(postfix);
		if (result instanceof Scl) {
			return (Scl) result;
		}
		else {
			Output.customError("Expression didn't result in scalar");
			return null;
		}
	}
	
	public Var evalExpr(List<Object> postfix) {
		if (postfix == null) {
			ep.internalError("Null expression being evaluated");
			return null;
		}
		// Return the result
		return this.evaluateExpr(postfix);
	}
	
	public List<Object> getPostfixExpr() {
		// Read the expression
		List<Object> infix = readExpr();
		// If the expression can't be read return null
		if (infix == null) return null;
		// Convert the expression from infix to postfix
		return toPostfix(infix);
	}
	
	/**
	 * Reads an arithmetic expression from tokens in infix form
	 * @param first The first scalar of the expression
	 * @return The expression in infix form
	 */
	private List<Object> readExpr() {
		// The infix form of the expression
		List<Object> infix = new ArrayList<Object>();
		// Used to make sure all parantheses match
		int parenCount = 0;
		
		Tk nextTk = tr.peekNextToken();
		/* 
		 * The expression stops reading if:
		 * - The end of line is reached
		 * - A colon is encountered (if statements)
		 * - An arrow is encountered (for loops)
		 * - A by symbol is encountered (for loops)
		 * - A comma is encountered (argument lists)
		 */
		while (nextTk != Tk.EOL && nextTk != Tk.COLON
				&& nextTk != Tk.ARROW && nextTk != Tk.BY
				&& nextTk != Tk.COMMA) {
			tr.nextToken();
			// If token is an "=" it is an equality operator, NOT assignment operator
			if (tr.tk == Tk.ASSIGNMENT_OP) {
				infix.add(Tk.EQUAL_OP);
			}
			// Adds in and counts parantheses
			else if (Tk.isParen(tr.tk)) {
				// Add 1 if (
				if (tr.tk == Tk.LPAREN) {
					parenCount += 1;
				}
				// Subtract 1 if )
				else {
					parenCount -= 1;
				}
				// Adds paren to expression
				infix.add(tr.tk);
			}
			// Adds in tokens that are math operators
			else if (Tk.isMathOp(tr.tk)) {
				infix.add(tr.tk);
			}
			// Adds in tokens that are boolean operators
			else if (Tk.isBoolOp(tr.tk)) {
				infix.add(tr.tk);
			}
			// Adds in numerical literals and implicitly adds * if necessary
			else if (tr.tk == Tk.NUM_LIT) {
				Scl num = new Scl(tr.tokenStr());
				// Adds the numerical literal to expression
				infix.add(num);
				// Adds a * if followed by ( or a var
				switch(tr.peekNextToken()) {
				case LPAREN:
				case VAR_NAME:
					infix.add(Tk.MULT_OP);
				default:
					break;
				}
			}
			// Adds in boolean literals
			else if (tr.tk == Tk.TRUE || tr.tk == Tk.FALSE) {
				infix.add(tr.tk == Tk.TRUE? Bool.True : Bool.False);
			}
			// Implicity adds * if necesssary after a var name
			else if (tr.tk == Tk.VAR_NAME) {
				// Adds the var's name to the infix expression
				infix.add(tr.tokenStr());
				// Adds * if followed by (
				switch(tr.peekNextToken()) {
				case LPAREN:
					infix.add(Tk.MULT_OP);
				default:
					break;
				}
			}
			// Otherwise error
			else {
				ep.expectedError("arithmetic symbol or command");
				return null;
			}
			nextTk = tr.peekNextToken();
		}
		// All parantheses matched
		if (parenCount == 0) {
			return infix;
		}
		// One unmatched right paranthesis at the END
		else if (parenCount == -1 && infix.get(infix.size() - 1) == Tk.RPAREN) {
			// Go back a token
			tr.prevToken();
			// Remove the extra paranthesis
			infix.remove(infix.size() - 1);
			return infix;
		}
		// Unmatched parantheses
		else {
			if (parenCount < 0) {
				Output.customError("Need %d more ( in expression", -parenCount);
			}
			else {
				Output.customError("Need %d more ) in expression", parenCount);
			}
			return null;
		}
	}
	/**
	 * Converts an arithmetic expression from infix to postfix form
	 * @param infix The expression in infix form
	 * @return The expression in postfix form
	 */
	private List<Object> toPostfix(List<Object> infix) {
		// Convert to postfix using a stack
		Deque<Tk> exprStack = new LinkedList<>();
		// Postfix arraylist
		List<Object> postfix = new ArrayList<>();
		
		for (Object o : infix) {
			// If the expression contains a value to be operated on
			if (o instanceof Scl || o instanceof Mtx || o instanceof String
					|| o instanceof Bool) {
				// Add it to the postfix expression
				postfix.add(o);
			}
			// Otherwise
			else if (o instanceof Tk) {
				// Figure out which token it is
				Tk token = (Tk) o;
				
				// If it's a ( push it to the stack
				if (token == Tk.LPAREN) {
					exprStack.push(Tk.LPAREN);
				}
				// If it's a ) then pop operators until there's only a ( left 
				else if (token == Tk.RPAREN) {
					// Pop operators until ( left
					while (!(exprStack.isEmpty()) && !(exprStack.peek() == Tk.LPAREN)) {
						postfix.add(exprStack.pop());
					}
					// If there is no ( invalid expression
					if (!exprStack.isEmpty() && !(exprStack.peek() == Tk.LPAREN)) {
						Output.customError("Missing (");
						return null;
					}
					// If there is a ( pop it off
					else {
						exprStack.pop();
					}
				}
				else {
					// There's another operator on the stack
					if (exprStack.peek() != null && Tk.isOp(exprStack.peek())) {
						// If token is greater in precedence than the operator on the stack
						if (token.prec(exprStack.peek()) > 0) {
							exprStack.push(token);
						}
						// Otherwise, pull all higher or equal priority operators off the stack and push this one on
						else {
							while (exprStack.peek() != null && token.prec(exprStack.peek()) <= 0) {
								postfix.add(exprStack.pop());
							}
							exprStack.push(token);
						}
					}
					// There's no operators on the stack
					else {
						exprStack.push(token);
					}
				}
			}
		}
		// Add all remaining operators to the postfix expression
		while (!exprStack.isEmpty()) {
			postfix.add(exprStack.pop());
		}
		
		// Return the postfix expression
		return postfix;
	}
	
	/**
	 * Evaluates a scalar expression and returns the resulting scalar
	 * @param postfix The arithmetic expression in infix form
	 * @return The resulting scalar
	 */
	private Var evaluateExpr(List<Object> postfix) {		
		// Perform operations described in postfix
		Deque<Object> opStack = new LinkedList<>();
		
		for (Object o : postfix) {
			if (o instanceof Var) {
				// Push if it's a variable
				opStack.push(o);
			}
			else if (o instanceof String) {
				// Get the var
				Var var = getVar((String) o);
				// If it doesn't exist, return null
				if (var == null)
					return null;
				// Push the variable to opstack if it exists
				opStack.push(var);
			}
			else if (o instanceof Tk) {
				Tk token = (Tk) o;
				Object b = opStack.pop();
				Object a = null;
				// There might only be one operator for negate, plus, etc;
				if (!opStack.isEmpty()) {
					a = opStack.pop();
				}
				
				Object res;
				if (a == null && b == null) {
					Output.customError("%s has no numbers to operate on", token);
					return null;
				}
				else if (a == null && b instanceof Scl) {
					switch (token) {
					case ADD_OP:
						res = b;
						break;
					case SUB_OP:
						res = Scl.neg((Scl) b);
						break;
					default:
						Output.customError("Invalid operator before or after scalar: %s", token);
						return null;
					}
				}
				else if (a == null && b instanceof Mtx) {
					switch (token) {
					case ADD_OP:
						res = b;
						break;
					case SUB_OP:
						res = Mtx.NEG((Mtx) b);
						break;
					default:
						Output.customError("Invalid operator before or after matrix: %s", token);
						return null;
					}
				}
				else if (a instanceof Mtx && b instanceof Mtx) {
					switch (token) {
					case ADD_OP:
						res = Mtx.ADD((Mtx) a, (Mtx) b);
						if (res == null) {
							Output.customError("Dimensions do not match in added matrices: \n%s \nand \n%s", a, b);
							return null;
						}
						break;
					case SUB_OP:
						res = Mtx.SUB((Mtx) a, (Mtx) b);
						if (res == null) {
							Output.customError("Dimensions do not match in subtracted matrices: \n%s \nand \n%s", a, b);
							return null;
						}
						break;
					case MULT_OP:
						res = Mtx.MULT((Mtx) a, (Mtx) b);
						break;
					case EQUAL_OP:
						res = Mtx.EQUAL((Mtx) a, (Mtx) b);
						break;
					default:
						Output.customError("Invalid operator between matrices: %s", token);
						return null;
					}
				}
				else if (a instanceof Scl && b instanceof Mtx) {
					switch (token) {
					
					case MULT_OP:
						res = Mtx.SCALE((Scl) a, (Mtx) b);
						break;
					default:
						Output.customError("Invalid operator between scalar and mtx: %s", token);
						return null;
					}
				}
				else if (a instanceof Scl && b instanceof Scl) {
					switch (token) {
					// Math operations
					case EXP_OP:
						res = Scl.exp((Scl) a, (Scl) b);
						break;
					case MULT_OP:
						res = Scl.mult((Scl) a, (Scl) b);
						break;
					case DIV_OP:
						res = Scl.div((Scl) a, (Scl) b);
						break;
					case ADD_OP:
						res = Scl.add((Scl) a, (Scl) b);
						break;
					case SUB_OP:
						res = Scl.sub((Scl) a, (Scl) b);
						break;
					// Boolean operations
					case GREAT_OR_EQUAL:
						res = Scl.great_or_equal((Scl) a, (Scl) b);
						break;
					case LESS_OR_EQUAL:
						res = Scl.less_or_equal((Scl) a, (Scl) b);
						break;
					case GREATER_OP:
						res = Scl.greater((Scl) a, (Scl) b);
						break;
					case LESSER_OP:
						res = Scl.lesser((Scl) a, (Scl) b);
						break;
					case EQUAL_OP:
						res = Scl.equal((Scl) a, (Scl) b);
						break;
					// Default
					default:
						Output.customError("Invalid token %s in expression", token);
						return null;
					}
				}
				else if (a instanceof Bool && b instanceof Bool) {
					switch(token) {
					case AND_OP:
						res = ((Bool) a).val() && ((Bool) b).val();
						break;
					case OR_OP:
						res = ((Bool) a).val() || ((Bool) b).val();
						break;
					default:
						Output.customError("Invalid token %s between true/false expressions", token);
						return null;
					}
				}
				else if (a == null && b instanceof Bool) {
					switch(token) {
					case NOT_OP:
						res = !((Bool) b).val();
						break;
					default:
						Output.customError("Invalid token %s for single true/false", token);
						return null;
					}
				}
				else {
					Output.customError("Invalid token %s for %s and %s", token, 
							(a==null)? Var.Null : a, (b==null)? Var.Null : b);
					return null;
				}
				
				opStack.push(res);
			}
		}
		// The last scalar on the stack is the answer
		if (opStack.size() == 1) {
			try {
				Var result = (Var) opStack.peek();
				return result;
			}
			catch (ClassCastException e) {
				ep.internalError("Last operator on the stack '%s' was not a var", opStack.peek());
				return null;
			}
		}
		else {
			Output.customError("Expression evaluation failed");
			return null;
		}
	}
}
