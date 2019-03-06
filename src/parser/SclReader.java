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
	 * @param prevTokens Any previous tokens in the expression that were already read over
	 * @return The scalar result of the following expression
	 */
	public Scl SCLEXPR(Object...prevTokens) {
		// If any step in the process fails, return null
		// Read the expression
		ArrayList<Object> infix = readExpr(prevTokens);
		// If the expression can't be read return null
		if (infix == null) return null;
		// Convert the expression from infix to postfix
		ArrayList<Object> postfix = toPostfix(infix);
		// If the expression can't be converted return null
		if (postfix == null) return null;
		// Evaluate the expression
		Scl result = evaluateExpr(postfix);
		// Return the result
		return result;
	}
	
	/**
	 * Reads an arithmetic expression from tokens in infix form
	 * @param first The first scalar of the expression
	 * @return The expression in infix form
	 */
	public ArrayList<Object> readExpr(Object...prevTokens) {
		// The infix form of the expression
		ArrayList<Object> infix = new ArrayList<>();
		// Used to make sure all parantheses match
		int parenCount = 0;
		// Adds tokens into the expression that were already read
		for (Object token : prevTokens) {
			// Adds in and counts parantheses
			if (tr.tk == Tk.LPAREN || tr.tk == Tk.RPAREN) {
				// Add 1 if (
				if (tr.tk == Tk.LPAREN) {
					parenCount += 1;
				}
				// Subtract 1 if )
				else {
					parenCount -= 1;
				}
			}
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
			// Adds in and counts parantheses
			else if (tr.tk == Tk.LPAREN || tr.tk == Tk.RPAREN) {
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
			// Adds in scalars
			else if (tr.tk == Tk.NUMLIT) {
				Scl num = new Scl(tr.tokenStr());
				infix.add(num);
			}
			// Otherwise error
			else {
				ep.expectedError("arithmetic symbol", tr.tokenStr());
				return null;
			}
			tr.nextToken();
			i++;
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
	public Scl evaluateExpr(ArrayList<Object> postfix) {		
		// Perform operations described in postfix
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
					ep.customError("Invalid token in arithmetic expression");
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
		
		String sclName = tr.tokenStr();
		tr.nextToken();
		
		// If there is no next token, print out the value of that scalar
		if (tr.tk == Tk.EOL) {
			print(sclName);
		}
		
		// Otherwise, make a new scalar and assign it the result of the next expression
		else if (tr.tk == Tk.ASSIGNMENT) {
			Scl var = SCLEXPR();
			sclReg.put(sclName, var);
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
			ep.expectedError("=", tr.tokenStr());
		}
	}
}
