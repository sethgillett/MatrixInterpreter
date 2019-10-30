package parser;

import tokens.Tk;
import tokens.TokenReader;
import vars.Var;

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