package parser.readers;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import parser.primary.ParserType;
import tokens.Tk;
import vars.mtx.Mtx;
import vars.scl.Scl;

public class ExprReader extends ParserType {
	
	public Mtx mtxExpr(ArrayList<Object> postfix) {
		Object result = expr(postfix);
		try {
			return (Mtx) result;
		}
		catch (ClassCastException e) {
			ep.customError("Expression didn't result in matrix");
			return null;
		}
	}
	
	public Scl sclExpr(ArrayList<Object> postfix) {
		Object result = expr(postfix);
		try {
			return (Scl) result;
		}
		catch (ClassCastException e) {
			ep.customError("Expression didn't result in scalar");
			return null;
		}
	}
	
	public Boolean boolExpr(ArrayList<Object> postfix) {
		Object result = expr(postfix);
		try {
			return (Boolean) result;
		}
		catch(ClassCastException e) {
			ep.customError("Expression didn't result in true/false");
			return null;
		}
	}
	
	public Object expr(ArrayList<Object> postfix) {
		if (postfix == null) {
			// If any part fails return null
			// Read the expression
			ArrayList<Object> infix = readExpr();
			// If the expression can't be read return null
			if (infix == null) return null;
			// Convert the expression from infix to postfix
			postfix = toPostfix(infix);
		}
		try {
			// Return the result
			return this.evaluateExpr(postfix);
		}
		// If it fails to parse, throw an error
		catch (Exception e) {
			//e.printStackTrace();
			ep.customError("Invalid arithmetic expression");
			return null;
		}
	}
	
	public ArrayList<Object> getExpr() {
		// Read the expression
		ArrayList<Object> infix = readExpr();
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
	private ArrayList<Object> readExpr() {
		// The infix form of the expression
		ArrayList<Object> infix = new ArrayList<>();
		// Used to make sure all parantheses match
		int parenCount = 0;
		// Index in infix
		int i = 0;
		// Read the expression in infix form
		Tk nextTk = tr.peekNextToken();
		while (nextTk != Tk.EOL && nextTk != Tk.COLON && nextTk != Tk.ARROW && nextTk != Tk.BY) {
			tr.nextToken();
			// If token is a "-" with nothing before, it is actually the negation operator
			if (tr.tk == Tk.SUB_OP) {
				// If the token has no elements prior
				if (i == 0) {
					// The sub_op is the neg_op
					infix.add(Tk.NEG_OP);
				}
				// If the token has an element prior AND that element is a token
				else if (i > 0 && infix.get(i-1) instanceof Tk) {
					// The sub_op is actually the neg_op
					infix.add(Tk.NEG_OP);
				}
				else {
					// Otherwise, it's just the subtraction operator
					infix.add(Tk.SUB_OP);
				}
			}
			// If token is an "=" it is an equality operator, NOT assignment operator
			else if (tr.tk == Tk.ASSIGNMENT_OP) {
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
				// Adds a * if followed by appropriate symbol
				switch(tr.peekNextToken()) {
				case LPAREN:
				case SCL_NAME:
				case MTX_NAME:
					infix.add(Tk.MULT_OP);
				default:
					break;
				}
			}
			// Adds in any scalar variables' NAMES and implicity adds * if necesssary
			else if (tr.tk == Tk.SCL_NAME) {
				Scl num = getScl(tr.tokenStr());
				// If a scalar doesn't exist return null
				if (num == null)
					return null;
				// Adds the scalar name to the infix expression
				infix.add(tr.tokenStr());
				// Adds * if followed by the appropriate symbol
				switch(tr.peekNextToken()) {
				case LPAREN:
					infix.add(Tk.MULT_OP);
				default:
					break;
				}
			}
			// Adds in any matrix variables
			else if (tr.tk == Tk.MTX_NAME) {
				Mtx mtx = getMtx(tr.tokenStr());
				// If a matrix doesn't exist return null
				if (mtx == null)
					return null;
				infix.add(mtx);
			}
			else if (tr.tk == Tk.VAR_CMD) {
				tr.prevToken();
				// Execute the command and add the result to the infix expression
				Object result = cmdReader.varCmd();
				infix.add(result);
			}
			// Otherwise error
			else {
				ep.expectedError("arithmetic symbol or command");
				return null;
			}
			nextTk = tr.peekNextToken();
			i += 1;
		}
		// All parantheses matched
		if (parenCount == 0) {
			return infix;
		}
		// Unmatched parantheses
		else {
			if (parenCount < 0) {
				ep.customError("Need %d more ( in expression", -parenCount);
			}
			else {
				ep.customError("Need %d more ) in expression", parenCount);
			}
			return null;
		}
	}
	/**
	 * Converts an arithmetic expression from infix to postfix form
	 * @param infix The expression in infix form
	 * @return The expression in postfix form
	 */
	private ArrayList<Object> toPostfix(ArrayList<Object> infix) {
		// Convert to postfix using a stack
		Deque<Tk> exprStack = new LinkedList<>();
		// Postfix arraylist
		ArrayList<Object> postfix = new ArrayList<>();
		
		for (Object o : infix) {
			// If the expression contains a scalar or matrix
			if (o instanceof Scl || o instanceof Mtx || o instanceof String) {
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
						ep.customError("Missing (");
						return null;
					}
					// If there is a ( pop it off
					else {
						exprStack.pop();
					}
				}
				// Otherwise pull all higher priority operators off the stack and push this one on
				else {
					while (!(exprStack.peek() == null) && 
							(Tk.isMathOp(exprStack.peek()) || Tk.isBoolOp(exprStack.peek())) &&
							(exprStack.peek().higherPrec(token))) {
						postfix.add(exprStack.pop());
					}
					exprStack.push(token);
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
	private Object evaluateExpr(ArrayList<Object> postfix) {		
		// Perform operations described in postfix
		Deque<Object> opStack = new LinkedList<>();
		
		for (Object o : postfix) {
			if (o instanceof Scl || o instanceof Mtx || o instanceof String) {
				opStack.push(o);
			}
			else if (o instanceof Tk) {
				Tk token = (Tk) o;
				Object b = opStack.pop();
				// b is a scalar's name
				if (b instanceof String) {
					// Get the scalar's value
					b = getScl((String) b);
				}
				Object a = null;
				// There might only be one operator for negate, plus, etc;
				if (!opStack.isEmpty()) {
					a = opStack.pop();
					// a is a scalar's name
					if (a instanceof String) {
						// Get the scalar's value
						a = getScl((String) a);
					}
				}
				
				Object res;
				if (a == null && b == null) {
					ep.customError("%s has no numbers to operate on", token);
					return null;
				}
				else if (a == null && b instanceof Scl) {
					switch (token) {
					case ADD_OP:
						res = b;
						break;
					case NEG_OP:
						res = Scl.neg((Scl) b);
						break;
					default:
						ep.customError("Invalid operator before or after scalar: %s", token);
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
						ep.customError("Invalid operator before or after matrix: %s", token);
						return null;
					}
				}
				else if (a instanceof Mtx && b instanceof Mtx) {
					switch (token) {
					case ADD_OP:
						res = Mtx.ADD((Mtx) a, (Mtx) b);
						if (res == null) {
							ep.customError("Dimensions do not match in added matrices: \n%s \nand \n%s", a, b);
							return null;
						}
						break;
					case SUB_OP:
						res = Mtx.SUB((Mtx) a, (Mtx) b);
						if (res == null) {
							ep.customError("Dimensions do not match in subtracted matrices: \n%s \nand \n%s", a, b);
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
						ep.customError("Invalid operator between matrices: %s", token);
						return null;
					}
				}
				else if (a instanceof Scl && b instanceof Mtx) {
					switch (token) {
					
					case MULT_OP:
						res = Mtx.SCALE((Scl) a, (Mtx) b);
						break;
					default:
						ep.customError("Invalid operator between scalar and mtx: %s", token);
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
						ep.customError("Invalid token %s in expression", token);
						return null;
					}
				}
				else if (a instanceof Boolean && b instanceof Boolean) {
					switch(token) {
					case AND_OP:
						res = (Boolean) a && (Boolean) b;
						break;
					case OR_OP:
						res = (Boolean) a || (Boolean) b;
						break;
					default:
						ep.customError("Invalid token %s between true/false expressions", token);
						return null;
					}
				}
				else if (a == null && b instanceof Boolean) {
					switch(token) {
					case NOT_OP:
						res = !(Boolean) b;
						break;
					default:
						ep.customError("Invalid token %s for single true/false", token);
						return null;
					}
				}
				else {
					ep.customError("Invalid token in arithmetic expression: %s", token);
					return null;
				}
				
				opStack.push(res);
			}
		}
		// The last scalar on the stack is the answer
		if (opStack.size() == 1) {
			return opStack.pop();
		}
		else {
			ep.customError("Expression evaluation failed");
			return null;
		}
	}
}
