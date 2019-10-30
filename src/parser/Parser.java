package parser;

import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import io.Input;
import io.Output;
import parser.VarContainer;
import tokens.Tk;
import tokens.TokenReader;
import vars.Var;
import vars.bool.Bool;
import vars.mtx.FullMtx;
import vars.mtx.Mtx;
import vars.scl.Scl;

/**
 * Primary parser/interpreter for the program
 * 
 * @author Seth Gillett
 *
 */
public abstract class Parser {
  /**
   * The primary parser
   */
  public static Parser primary;
  /**
   * The variable container in lieu of function scoping
   */
  public static VarContainer vars = new VarContainer();

  /**
   * Attempts to get a var by name
   * 
   * @param name The name of the var
   * @return The var
   */
  public static Var getVar(String name) {
    return vars.getLocalVar(name);
  }

  /**
   * Attempts to set a var by name and valud
   * 
   * @param name The name of the var
   * @param val  The value of the var
   */
  public static void setVar(String name, Var val) {
    vars.setLocalVar(name, val);
  }

  /**
   * Checks for the existence of a var
   * 
   * @param name The name of the var
   * @return True or false
   */
  public static boolean hasVar(String name) {
    return vars.hasLocalVar(name);
  }

  /**
   * Reads in a new line
   * 
   * @param line The new line read in
   * @return Whether the run was successful <b>or a returned variable</b>
   */
  public static Var read(String line) {
    TokenReader.readLine(line);
    TokenReader.nextToken();
    // Blank line - does nothing
    if (TokenReader.tk == Tk.EOL) {
      return Bool.True;
    }
    // Look for a control statement
    else if (Tk.isControlTk(TokenReader.tk)) {
      switch (TokenReader.tk) {
      case IF:
        return ControlsReader.ifStmt();
      case FOR:
        return ControlsReader.forStmt();
      case WHILE:
        return ControlsReader.whileStmt();
      default:
        return null;
      }
    }
    // If an expression has a scl or mtx name
    else if (TokenReader.tk == Tk.VAR_NAME) {
      // Next token
      TokenReader.nextToken();
      // If name is followed by assignment, send to appropriate assignment
      if (TokenReader.tk == Tk.ASSIGNMENT_OP) {
        TokenReader.restartLine();
        return VarReader.varAssign() ? Var.Null : null;
      } else if (Tk.isExprTk(TokenReader.tk)) {
        // If it's an expression print out the value of the expression
        TokenReader.restartLine();
        Output.println(ExprReader.expr());
        return Bool.Null;
      } else if (TokenReader.tk == Tk.EOL) {
        // If there is no next token, print out the value of that var
        TokenReader.prevToken();
        return vars.printVar(TokenReader.tokenStr()) ? Bool.Null : null;
      } else {
        Output.expectedError("assignment or arithmetical expression", TokenReader.tk);
        return Bool.False;
      }
    }
    // The statement is an expression
    else if (Tk.isExprTk(TokenReader.tk)) {
      // If it's an expression print out the value of the expression
      TokenReader.restartLine();
      Var result = ExprReader.expr();
      Output.print(result);
      return (result == null) ? Var.Null : null;
    }
    // The statement is a return statement
    else if (TokenReader.tk == Tk.RETURN) {
      return ExprReader.expr();
    }
    // Otherwise, print an error
    else {
      Output.expectedError("arithmetic expression, command, or assignment", TokenReader.tk);
      return Bool.False;
    }
  }

}

abstract class ControlsReader {
  /**
   * @return The var returned (if any)
   */
  public static Var ifStmt() {
    // IF token flagged
    Bool ifCondition = (Bool) ExprReader.expr();
    if (ifCondition == null)
      return null;
    if (ifCondition == Bool.False)
      return Bool.Null;
    TokenReader.nextToken();
    if (TokenReader.tk == Tk.COLON) {
      TokenReader.nextToken();
      if (TokenReader.tk == Tk.EOL) {
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
          Var result = Parser.read(stmt);
          // ERROR
          if (result == null) {
            return null;
          }
          // no return value
          else if (Parser.read(stmt) == Var.Null) {
            continue;
          }
          // return value
          else {
            return result;
          }
        }
        return Bool.True;
      } else {
        Output.expectedError(Tk.EOL, TokenReader.tk);
      }
    } else {
      Output.customError("Expected : after if statement");
    }
    return null;
  }

  /**
   * <p>
   * while condition:
   * <li>stmt</li>
   * <li>stmt</li>
   * <li>stmt</li>
   * </p>
   * 
   * @return The var returned (if any)
   */
  public static Var whileStmt() {
    // WHILE token flagged
    List<Object> whileExpr = ExprReader.convertExpr(ExprReader.readExpr());
    Bool whileCondition = (Bool) ExprReader.evaluateExpr(whileExpr);
    if (whileCondition == null)
      return null;
    if (whileCondition == Bool.False)
      return null;
    TokenReader.nextToken();
    if (TokenReader.tk == Tk.COLON) {
      TokenReader.nextToken();
      if (Output.hardCheck(Tk.EOL, TokenReader.tk)) {
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
            Var result = Parser.read(stmt);
            // ERROR
            if (result == null) {
              return null;
            }
            // no return value
            else if (Parser.read(stmt) == Var.Null) {
              continue;
            }
            // return value
            else {
              return result;
            }
          }
          whileCondition = (Bool) ExprReader.evaluateExpr(whileExpr);
        }
        return Bool.Null;
      }
    } else {
      Output.customError("Expected : after while statement");
    }
    return null;
  }

  public static Var forStmt() {
    // FOR token flagged
    TokenReader.nextToken();
    if (Output.hardCheck(Tk.VAR_NAME, TokenReader.tk)) {
      String iterName = TokenReader.tokenStr();
      TokenReader.nextToken();
      if (Output.hardCheck(Tk.IN, TokenReader.tk)) {
        Scl start;
        start = (Scl) ExprReader.expr();
        if (start == null) {
          Output.expectedError(Tk.NUM_LIT, Tk.VAR_NAME);
          return null;
        }
        TokenReader.nextToken();
        if (Output.hardCheck(Tk.ARROW, TokenReader.tk)) {
          Scl end;
          end = (Scl) ExprReader.expr();
          if (end == null) {
            Output.expectedError(Tk.NUM_LIT, Tk.VAR_NAME);
            return null;
          }
          TokenReader.nextToken();
          if (Output.hardCheck(Tk.COLON, TokenReader.tk)) {
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
            Parser.setVar(iterName, iterator);
            while (Scl.lesser(iterator, end)) {
              for (String stmt : stmts) {
                if (Parser.read(stmt) == null) {
                  return null;
                }
              }
              iterator = Scl.add(iterator, Scl.ONE);
              Parser.setVar(iterName, iterator);
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
            res = ((Bool) a).val() && ((Bool) b).val();
            break;
          case OR_OP:
            res = ((Bool) a).val() || ((Bool) b).val();
            break;
          default:
            Output.customError("Invalid token %s between true/false expressions", token);
            return null;
          }
        } else if (a == null && b instanceof Bool) {
          switch (token) {
          case NOT_OP:
            res = !((Bool) b).val();
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

abstract class InputReader {
  /**
   * Reads parameters given to a function in a function call
   * 
   * @return A list of the parameters
   */
  public static List<Var> readCallParams() {
    List<Var> params = new ArrayList<Var>();
    // Always ends the token before the next section
    TokenReader.nextToken();
    if (Output.hardCheck(Tk.LPAREN, TokenReader.tk)) {
      do {
        TokenReader.nextToken();
        if (TokenReader.tk == Tk.RPAREN)
          break;
        TokenReader.prevToken(); // Start the expression reader on the token before the expression
        Var var = ExprReader.expr();
        params.add(var);
        TokenReader.nextToken();
      } while (TokenReader.tk == Tk.COMMA);

      if (Output.hardCheck(Tk.RPAREN, TokenReader.tk)) {
        return params;
      }
    }
    return null;
  }

  /**
   * Reads a list of parameter names when defining a function
   * 
   * @return The list of parameter names
   */
  public static List<String> readDefinedParams() {
    List<String> paramNames = new ArrayList<String>();
    // Always ends the token before the next section
    TokenReader.nextToken();
    if (Output.hardCheck(Tk.LPAREN, TokenReader.tk)) {
      do {
        TokenReader.nextToken();
        if (TokenReader.tk == Tk.RPAREN)
          break;
        String name = TokenReader.tokenStr();
        paramNames.add(name);
        TokenReader.nextToken();
      } while (TokenReader.tk == Tk.COMMA);

      if (Output.hardCheck(Tk.RPAREN, TokenReader.tk)) {
        return paramNames;
      }
    }
    return null;
  }

  /**
   * Reads in a matrix via the terminal or file io
   * 
   * @param fromTerminal True if reading input from the terminal
   * @return The matrix read in
   */
  public static Mtx readMtxInput(boolean fromTerminal) {
    ArrayList<ArrayList<Scl>> mtx = new ArrayList<>();
    String lineStr;
    Integer lineLen = null;
    do {
      // Will only print brackets if reading input from terminal
      if (fromTerminal)
        Output.print("[");
      lineStr = Input.readLine();
      TokenReader.readLine(lineStr);
      ArrayList<Scl> line = new ArrayList<>();
      while (true) {
        TokenReader.nextToken();
        if (TokenReader.tk == Tk.EOL) {
          break;
        } else if (TokenReader.tk == Tk.COMMA) {
          continue;
        } else {
          TokenReader.prevToken();
          Scl s = (Scl) ExprReader.expr();
          if (s == null)
            return null;
          line.add(s);
        }
      }

      if (!line.isEmpty()) {
        if (lineLen != null && line.size() != lineLen) {
          int dif = line.size() - lineLen;
          // Matrix row has [number] too [few | many] parameters
          Output.customError("Matrix row has %d too %s parameters", Math.abs(dif), dif < 0 ? "few" : "many");
          return null;
        } else {
          mtx.add(line);
        }
      }

      if (lineLen == null)
        lineLen = line.size();

    } while (!lineStr.matches("^(\\s*)$"));

    if (mtx.isEmpty()) {
      return null;
    }

    Scl[][] mtxArray = new Scl[mtx.size()][mtx.get(0).size()];

    int r = 0;
    for (ArrayList<Scl> row : mtx) {
      int c = 0;
      for (Scl cell : row) {
        try {
          mtxArray[r][c] = cell;
        } catch (ArrayIndexOutOfBoundsException e) {
          Output.customError("Matrix is not rectangular");
          return null;
        }
        c++;
      }
      r++;
    }

    return new FullMtx(mtxArray);
  }
}

abstract class VarReader {
  /**
   * Creates a new var and assigns a value to it
   * 
   * @return Whether the assignment was successful
   */
  public static boolean varAssign() {
    // a
    TokenReader.nextToken();
    String varName = TokenReader.tokenStr();
    // =
    TokenReader.nextToken();
    // cmd?
    TokenReader.nextToken();
    Var var = null;
    if (TokenReader.tk == Tk.EOL) {
      var = InputReader.readMtxInput(true);
    } else {
      TokenReader.prevToken();
      var = ExprReader.expr();
    }
    if (var == null)
      return false;
    Parser.setVar(varName, var);
    return true;
  }

}