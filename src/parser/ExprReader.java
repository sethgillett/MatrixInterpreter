package parser;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import tokens.Tk;
import vars.mtx.Mtx;
import vars.scl.Scl;

public class ExprReader extends ParserType {

	public ExprReader(ParserType s) {
		super(s);
	}
	
	public Mtx MTXEXPR() {
		Object result = EXPR();
		try {
			return (Mtx) result;
		}
		catch (ClassCastException e) {
			ep.customError("Expression resulted in scalar, not matrix");
			return null;
		}
	}
	
	public Scl SCLEXPR() {
		Object result = EXPR();
		try {
			return (Scl) result;
		}
		catch (ClassCastException e) {
			ep.customError("Expression resulted in matrix, not scalar");
			return null;
		}
	}
	
	public Object UNKNOWNEXPR() {
		return EXPR();
	}
	
	private Object EXPR() {
		// If any part fails return null
		// Read the expression
		ArrayList<Object> infix = readExpr();
		// If the expression can't be read return null
		if (infix == null) return null;
		// Convert the expression from infix to postfix
		ArrayList<Object> postfix = toPostfix(infix);
		// If the expression can't be converted return null
		if (postfix == null) return null;
		// Evaluate the expression
		Object result;
		try {
			result = this.evaluateExpr(postfix);
			// Return the result
			return result;
		}
		// If it fails to parse, throw an error
		catch (Exception e) {
			//e.printStackTrace();
			ep.customError("Invalid arithmetic expression");
			return null;
		}
	}
	/**
	 * Reads an arithmetic expression from tokens in infix form
	 * @param first The first scalar of the expression
	 * @return The expression in infix form
	 */
	public ArrayList<Object> readExpr() {
		// The infix form of the expression
		ArrayList<Object> infix = new ArrayList<>();
		// Used to make sure all parantheses match
		int parenCount = 0;
		// Read the expression in infix form
		tr.nextToken();
		// Index in infix
		int i = 0;
		while (tr.tk != Tk.EOL) {
//			// If token is a "-" with nothing before, it is actually the negation operator
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
			// Adds in numerical literals
			else if (tr.tk == Tk.NUM_LIT) {
				Scl num = new Scl(tr.tokenStr());
				infix.add(num);
			}
			// Adds in any scalar variables
			else if (tr.tk == Tk.SCL_NAME) {
				Scl num = this.getScl(tr.tokenStr());
				// If a scalar doesn't exist return null
				if (num == null)
					return null;
				infix.add(num);
			}
			// Adds in any matrix variables
			else if (tr.tk == Tk.MTX_NAME) {
				Mtx mtx = this.getMtx(tr.tokenStr());
				// If a matrix doesn't exist return null
				if (mtx == null)
					return null;
				infix.add(mtx);
			}
			// Otherwise error
			else {
				ep.expectedError("arithmetic symbol", tr.tokenStr());
				return null;
			}
			tr.nextToken();
			i += 1;
//			System.out.println("Infix array: " + infix);
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
	public ArrayList<Object> toPostfix(ArrayList<Object> infix) {
		// Convert to postfix using a stack
		Deque<Tk> exprStack = new LinkedList<>();
		// Postfix arraylist
		ArrayList<Object> postfix = new ArrayList<>();
		
		for (Object o : infix) {
			// If the expression contains a scalar or matrix
			if (o instanceof Scl || o instanceof Mtx) {
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
							Tk.isMathOp(exprStack.peek()) &&
							(exprStack.peek().higherPrec(token))) {
						postfix.add(exprStack.pop());
					}
					exprStack.push(token);
				}
			}
//			System.out.println("EXPR stack: " + exprStack);
//			System.out.println("Postfix array: " + postfix);
		}
		// Add all remaining operators to the postfix expression
		while (!exprStack.isEmpty()) {
			postfix.add(exprStack.pop());
		}
		
//		System.out.println("EXPR stack: " + exprStack);
//		System.out.println("Postfix array: " + postfix);
		
		// Return the postfix expression
		return postfix;
	}
	
	/**
	 * Evaluates a scalar expression and returns the resulting scalar
	 * @param postfix The arithmetic expression in infix form
	 * @return The resulting scalar
	 */
	public Object evaluateExpr(ArrayList<Object> postfix) {		
		// Perform operations described in postfix
		Deque<Object> opStack = new LinkedList<>();
		
		for (Object o : postfix) {
			if (o instanceof Scl || o instanceof Mtx) {
				opStack.push(o);
			}
			else if (o instanceof Tk) {
				Tk token = (Tk) o;
				Object b = opStack.pop();
				Object a = null;
				// There might only be one operator for negate, plus, etc;
				if (!opStack.isEmpty())
					a = opStack.pop();
				
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
					default:
						ep.customError("Invalid token in arithmetic expression");
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
