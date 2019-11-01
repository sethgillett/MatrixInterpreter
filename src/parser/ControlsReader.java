package parser;

import java.util.ArrayList;
import java.util.List;

import io.Input;
import io.Output;
import tokens.Tk;
import tokens.TokenReader;
import vars.Var;
import vars.bool.Bool;
import vars.scl.Scl;

abstract class ControlsReader {
  /**
   * 
   * Executes code inside an if statement if the condition contained is true
   * 
   * @return The var returned (if any)
   */
  public static Var ifStmt() {
    // IF token flagged
    // Read condition
    Bool ifCondition = (Bool) ExprReader.expr();
    if (ifCondition == null)
      return null;
    if (ifCondition == Bool.False)
      return Bool.Null;
    TokenReader.nextToken();
    // HARD check for a colon
    if (Output.hardCheck(Tk.COLON, TokenReader.tk)) {
      TokenReader.nextToken();
      // HARD check for EOL
      if (Output.hardCheck(Tk.EOL, TokenReader.tk)) {
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
      }
    }
    return null;
  }

  /**
   *
   * Executes a series of lines in a while statement while the condition is true
   * 
   * @return The var returned (if any)
   */
  public static Var whileStmt() {
    // WHILE token flagged
    List<Object> whileExpr = ExprReader.convertExpr(ExprReader.readExpr());
    TokenReader.nextToken();
    // HARD check for colon
    if (Output.hardCheck(Tk.COLON, TokenReader.tk)) {
      TokenReader.nextToken();
      // HARD check for EOL
      if (Output.hardCheck(Tk.EOL, TokenReader.tk)) {
        // Reads lines to a list
        List<String> stmts = new ArrayList<>();
        String newLine = Input.readLine();
        while (newLine != null && !newLine.matches("\\s*\\b(?:end)\\b") && !newLine.matches("\\s*\\b(?:return)\\b.*")) {
          stmts.add(newLine);
          newLine = Input.readLine();
        }
        // Look for an end statement
        if (newLine.matches("\\s*\\b(?:return)\\b.*")) {
          stmts.add(newLine); // Add the return statement
        }
        else if (newLine.matches("\\s*\\b(?:end)\\b")) {
          ; // fine
        }
        else {
          Output.customError("No end statement found after while statement");
          return null; // ERROR
        }
        // Evaluate the while conditional
        Bool whileCondition = (Bool) ExprReader.evaluateExpr(whileExpr);
        // While the condition is true execute the contained lines
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
          // Re-evaluate the while conditional
          whileCondition = (Bool) ExprReader.evaluateExpr(whileExpr);
        }
        return Bool.Null;
      }
    }
    return null;
  }

  public static Var forStmt() {
    // FOR token flagged
    // Components of for loop
    Scl start, end, inc;

    TokenReader.nextToken();
    // HARD check for iterator name
    if (Output.hardCheck(Tk.VAR_NAME, TokenReader.tk)) {
      String iterName = TokenReader.tokenStr();
      TokenReader.nextToken();
      // HARD check for IN token
      if (Output.hardCheck(Tk.IN, TokenReader.tk)) {
        start = (Scl) ExprReader.expr();
        if (start == null) {
          Output.expectedError(Tk.NUM_LIT, Tk.VAR_NAME);
          return null;
        }
        // HARD check for ARROW token
        TokenReader.nextToken();
        if (Output.hardCheck(Tk.ARROW, TokenReader.tk)) {
          end = (Scl) ExprReader.expr();
          if (end == null) {
            Output.expectedError(Tk.NUM_LIT, Tk.VAR_NAME);
            return null;
          }
          // SOFT check for BY token
          TokenReader.nextToken();
          if (TokenReader.tk == Tk.BY) {
            // Set the increment
            inc = (Scl) ExprReader.expr();
            if (inc == null) {
              Output.expectedError(Tk.NUM_LIT, Tk.VAR_NAME);
              return null;
            }
            TokenReader.nextToken();
          }
          else {
            // Default increment of one
            inc = Scl.ONE;
          }
          if (Output.hardCheck(Tk.COLON, TokenReader.tk)) {
            List<String> stmts = new ArrayList<>();
            String newLine = Input.readLine();
            // Read line by line
            while (newLine != null && !newLine.matches("\\s*\\b(?:end)\\b") && !newLine.matches("\\s*\\b(?:return)\\b.*")) {
              stmts.add(newLine);
              newLine = Input.readLine();
            }
            // Look for an end statement
            if (newLine.matches("\\s*\\b(?:return)\\b.*")) {
              stmts.add(newLine); // Add the return statement
            }
            else if (newLine.matches("\\s*\\b(?:end)\\b")) {
              ; // fine
            }
            else {
              Output.customError("No end statement found after while statement");
              return null;
            }
            // Create the iterator
            Scl iterator = new Scl(start);
            // Put it in the var reg so it can be accessed within code
            Parser.setVar(iterName, iterator);
            // Iterate while true
            while (Scl.lesser(iterator, end) == Bool.True) {
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