package parser;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import tokens.Tk;
import vars.Scl;

public class SclReader extends ParserType {

	public SclReader(Parser primary) {
		super(primary);
	}
	
	/**
	 * Evaluates and returns a scalar expression as a scalar
	 * @return The scalar result of the following expression
	 */
	public Scl SCLEXPR() {
		// Read the expression
		ArrayList<Object> infix = readExpr();
		// Evaluation the expression
		return evaluateExpr(infix);
//		// Reads the next token
//		tr.nextToken();
//		// If the next token is a number
//		if (tr.tk == Tk.NUMLIT) {
//			// Read the number
//			Scl num = new Scl(tr.lastTokenStr());
//			// Read the next token
//			tr.nextToken();
//			// If that token is the EOL, return the number
//			if (tr.tk == Tk.EOL) {
//				return num;
//			}
//			// Otherwise if that token is a math operator, read and eval the expression
//			else if (Tk.isMathOp(tr.tk)) {
//				// Read the expression
//				ArrayList<Object> infix = readExpr(num, tr.tk);
//				// Evaluation the expression
//				return evaluateExpr(infix);
//			}
//			// Otherwise the token does not belong in an expression
//			else {
//				p.ep.expectedError("arithmetic expression or number", tr.lastTokenStr());
//				return null;
//			}
//		}
//		// If the next token is a -
//		else if (tr.tk == Tk.SUB_OP) {
//			// Read the expression with negation operator
//			ArrayList<Object> infix = readExpr(Tk.NEG_OP);
//			// Evaluate and return
//			return evaluateExpr(infix);
//		}
//		// If the next token is a (
//		else if (tr.tk == Tk.LPAREN) {
//			// Read the expression with negation operator
//			ArrayList<Object> infix = readExpr(Tk.LPAREN);
//			// Evaluate and return
//			return evaluateExpr(infix);
//		}
//		// Assignment should not have something else after it
//		else {
//			ep.expectedError("( or number", tr.lastTokenStr());
//			return null;
//		}
	}
	
	/**
	 * Reads an arithmetic expression from tokens in infix form
	 * @param first The first scalar of the expression
	 * @return The expression in infix form
	 */
	public ArrayList<Object> readExpr(Object...prevTokens) {
		// The infix form of the expression
		ArrayList<Object> infix = new ArrayList<>();
		// Adds tokens into the expression that were already read
		for (Object token : prevTokens) {
			infix.add(token);
		}
		// Read the expression in infix form
		tr.nextToken();
		// Index in infix
		int i = infix.size() - 1;
		while (tr.tk != Tk.EOL) {
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
			// Adds in tokens that are math operators
			else if (Tk.isMathOp(tr.tk) || tr.tk == Tk.LPAREN || tr.tk == Tk.RPAREN) {
				infix.add(tr.tk);
			}
			// Adds in scalars
			else if (tr.tk == Tk.NUMLIT) {
				Scl num = new Scl(tr.lastTokenStr());
				infix.add(num);
			}
			// Otherwise error
			else {
				ep.expectedError("arithmetic symbol", tr.lastTokenStr());
				return null;
			}
			tr.nextToken();
			i++;
//			System.out.println("Infix array: " + infix);
		}
		return infix;
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
			// If the expression contains a scalar
			if (o instanceof Scl) {
				// Add it to the postfix expression
				postfix.add((Scl) o);
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
						ep.customError("Invalid arithmetic expression");
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
	public Scl evaluateExpr(ArrayList<Object> infix) {
		// Make sure expressions were valid
		if (infix == null)
			return null;
		
		ArrayList<Object> postfix = toPostfix(infix);
		if (postfix == null)
			return null;
		
		// If they were perform the described operations
		Deque<Scl> opStack = new LinkedList<>();
		
		for (Object o : postfix) {
			if (o instanceof Scl) {
				opStack.push((Scl) o);
			}
			else if (o instanceof Tk) {
				Tk token = (Tk) o;
				Scl b = opStack.pop();
				Scl a = null;
				// There might only be one operator for negate, plus, etc;
				if (!opStack.isEmpty())
					a = opStack.pop();
				Scl res;
				switch (token) {
				
				case EXP_OP:
					res = Scl.EXP(a, b);
					break;
				case MULT_OP:
					res = Scl.MULT(a, b);
					break;
				case DIV_OP:
					res = Scl.DIV(a, b);
					break;
				case ADD_OP:
					if (a != null)
						res = Scl.ADD(a, b);
					else
						res = b;
					break;
				case SUB_OP:
					if (a != null)
						res = Scl.SUB(a, b);
					else
						res = Scl.NEG(b);
					break;
				default:
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
			return null;
		}
	}
	
	/**
	 * Creates a new scalar and assigns a value to it
	 */
	public void SCLASSIGN() {
		
		String sclName = tr.lastTokenStr();
		tr.nextToken();
		
		// If there is no next token, print out the value of that scalar
		if (tr.tk == Tk.EOL) {
			p.print(sclName);
		}
		
		// Otherwise, make a new scalar and assign it the result of the next expression
		else if (tr.tk == Tk.ASSIGNMENT) {
			Scl var = SCLEXPR();
			p.sclReg.put(sclName, var);
		}
		
//		// If the next token is a command, perform that command and return result
//		else if (tr.tk == Tk.CMD) {
//			// Stores the cmd used
//			String cmdName = tr.lastTokenStr();
//			tr.nextToken();
//			// Must have '(' after cmd
//			if (tr.tk == Tk.LPAREN) {
//				switch(cmdName) {
//				
//				case "new":
//					p.cmdReader.NEW();
//					break;
//				
//				default:
//					//TODO: Program other commands and expressions
//					System.out.println(cmdName + " not programmed yet.");
//				}
//			}
//			else {
//				ep.expectedError("(", tr.lastTokenStr());
//			}
//		}
		
		// Invalid token
		else {
			ep.expectedError("=", tr.lastTokenStr());
		}
	}
}
