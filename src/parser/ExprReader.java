package parser;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import io.Output;
import tokens.Tk;
import tokens.TokenReader;
import vars.Var;
import vars.bool.Bool;
import vars.mtx.Mtx;
import vars.scl.Scl;

abstract class ExprReader {
  /**
   * Reads, converts, and evaluates an expression
   * 
   * @return
   */
  public static Var expr() {
    return evaluateExpr(convertExpr(readExpr()));
  }

  /**
   * Reads an arithmetic expression from tokens in infix form
   * 
   * @param first The first scalar of the expression
   * @return The expression in infix form
   */
  static List<Object> readExpr() {
    // The infix form of the expression
    List<Object> infix = new ArrayList<Object>();
    // Used to make sure all parantheses match
    int parenCount = 0;

    Tk nextTk = TokenReader.peekNextToken();
    /*
     * The expression stops reading if: - The end of line is reached - A colon is
     * encountered (if statements) - An arrow is encountered (for loops) - A by
     * symbol is encountered (for loops) - A comma is encountered (argument lists)
     */
    while (nextTk != Tk.EOL && nextTk != Tk.COLON && nextTk != Tk.ARROW && nextTk != Tk.BY && nextTk != Tk.COMMA) {
      TokenReader.nextToken();
      // If token is an "=" it is an equality operator, NOT assignment operator
      if (TokenReader.tk == Tk.ASSIGNMENT_OP) {
        infix.add(Tk.EQUAL_OP);
      }
      // Adds in and counts parantheses
      else if (Tk.isParen(TokenReader.tk)) {
        // Add 1 if (
        if (TokenReader.tk == Tk.LPAREN) {
          parenCount += 1;
        }
        // Subtract 1 if )
        else {
          parenCount -= 1;
        }
        // Adds paren to expression
        infix.add(TokenReader.tk);
      }
      // Adds in tokens that are math operators
      else if (Tk.isMathOp(TokenReader.tk)) {
        infix.add(TokenReader.tk);
      }
      // Adds in tokens that are boolean operators
      else if (Tk.isBoolOp(TokenReader.tk)) {
        infix.add(TokenReader.tk);
      }
      // Adds in numerical literals and implicitly adds * if necessary
      else if (TokenReader.tk == Tk.NUM_LIT) {
        Scl num = new Scl(TokenReader.tokenStr());
        // Adds the numerical literal to expression
        infix.add(num);
        // Adds a * if followed by ( or a var
        switch (TokenReader.peekNextToken()) {
        case LPAREN:
        case VAR_NAME:
          infix.add(Tk.MULT_OP);
        default:
          break;
        }
      }
      // Adds in boolean literals
      else if (TokenReader.tk == Tk.TRUE || TokenReader.tk == Tk.FALSE) {
        infix.add(TokenReader.tk == Tk.TRUE ? Bool.True : Bool.False);
      }
      // Implicity adds * if necesssary after a var name
      else if (TokenReader.tk == Tk.VAR_NAME) {
        // Adds the var's name to the infix expression
        infix.add(TokenReader.tokenStr());
        // Adds * if followed by (
        switch (TokenReader.peekNextToken()) {
        case LPAREN:
          infix.add(Tk.MULT_OP);
        default:
          break;
        }
      }
      // Otherwise error
      else {
        Output.expectedError("arithmetic symbol or command", TokenReader.tk);
        return null;
      }
      nextTk = TokenReader.peekNextToken();
    }
    // All parantheses matched
    if (parenCount == 0) {
      return infix;
    }
    // One unmatched right paranthesis at the END
    else if (parenCount == -1 && infix.get(infix.size() - 1) == Tk.RPAREN) {
      // Go back a token
      TokenReader.prevToken();
      // Remove the extra paranthesis
      infix.remove(infix.size() - 1);
      return infix;
    }
    // Unmatched parantheses
    else {
      if (parenCount < 0) {
        Output.customError("Need %d more ( in expression", -parenCount);
      } else {
        Output.customError("Need %d more ) in expression", parenCount);
      }
      return null;
    }
  }

  /**
   * Converts an arithmetic expression from infix to postfix form
   * 
   * @param infix The expression in infix form
   * @return The expression in postfix form
   */
  static List<Object> convertExpr(List<Object> infix) {
    // Convert to postfix using a stack
    Deque<Tk> exprStack = new LinkedList<>();
    // Postfix arraylist
    List<Object> postfix = new ArrayList<>();

    for (Object o : infix) {
      // If the expression contains a value to be operated on
      if (o instanceof Scl || o instanceof Mtx || o instanceof String || o instanceof Bool) {
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
        } else {
          // There's another operator on the stack
          if (exprStack.peek() != null && Tk.isOp(exprStack.peek())) {
            // If token is greater in precedence than the operator on the stack
            if (token.prec(exprStack.peek()) > 0) {
              exprStack.push(token);
            }
            // Otherwise, pull all higher or equal priority operators off the stack and push
            // this one on
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
   * 
   * @param postfix The arithmetic expression in infix form
   * @return The resulting scalar
   */
  static Var evaluateExpr(List<Object> postfix) {
    // Perform operations described in postfix
    Deque<Object> opStack = new LinkedList<>();

    for (Object o : postfix) {
      if (o instanceof Var) {
        // Push if it's a variable
        opStack.push(o);
      } else if (o instanceof String) {
        // Get the var
        Var var = Parser.getVar((String) o);
        // If it doesn't exist, return null
        if (var == null)
          return null;
        // Push the variable to opstack if it exists
        opStack.push(var);
      } else if (o instanceof Tk) {
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
        } else if (a == null && b instanceof Scl) {
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
        } else if (a == null && b instanceof Mtx) {
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
        } else if (a instanceof Mtx && b instanceof Mtx) {
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
        } else if (a instanceof Scl && b instanceof Mtx) {
          switch (token) {

          case MULT_OP:
            res = Mtx.SCALE((Scl) a, (Mtx) b);
            break;
          default:
            Output.customError("Invalid operator between scalar and mtx: %s", token);
            return null;
          }
        } else if (a instanceof Scl && b instanceof Scl) {
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
        } else if (a instanceof Bool && b instanceof Bool) {
          switch (token) {
          case AND_OP:
            res = Bool.AND((Bool) a, (Bool) b);
            break;
          case OR_OP:
            res = Bool.OR((Bool) a, (Bool) b);
            break;
          default:
            Output.customError("Invalid token %s between true/false expressions", token);
            return null;
          }
        } else if (a == null && b instanceof Bool) {
          switch (token) {
          case NOT_OP:
            res = Bool.NOT((Bool) b);
            break;
          default:
            Output.customError("Invalid token %s for single true/false", token);
            return null;
          }
        } else {
          Output.customError("Invalid token %s for %s and %s", token, (a == null) ? Var.Null : a,
              (b == null) ? Var.Null : b);
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
      } catch (ClassCastException e) {
        Output.internalError("Last operator on the stack '%s' was not a var", opStack.peek());
        return null;
      }
    } else {
      Output.customError("Expression evaluation failed");
      return null;
    }
  }
}