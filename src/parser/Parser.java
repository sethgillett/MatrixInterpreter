package parser;

import io.Output;
import parser.VarContainer;
import tokens.Tk;
import tokens.TokenReader;
import vars.Var;
import vars.bool.Bool;

/**
 * Primary parser/interpreter for the program 
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
  public static VarContainer vars = new VarContainer(null);

  /**
   * Attempts to get a var by name   
   * @param name The name of the var
   * @return The var
   */
  public static Var getVar(String name) {
    return vars.getLocalVar(name);
  }

  /**
   * Attempts to set a var by name and valud   
   * @param name The name of the var
   * @param val  The value of the var
   */
  public static void setVar(String name, Var val) {
    vars.setLocalVar(name, val);
  }

  /**
   * Checks for the existence of a var   
   * @param name The name of the var
   * @return True or false
   */
  public static boolean hasVar(String name) {
    return vars.hasLocalVar(name);
  }

  /**
   * Reads in a new line   
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
      Output.println(result);
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