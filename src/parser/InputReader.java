package parser;

import java.util.ArrayList;
import java.util.List;

import io.Input;
import io.Output;
import tokens.Tk;
import tokens.TokenReader;
import vars.Var;
import vars.mtx.FullMtx;
import vars.mtx.Mtx;
import vars.scl.Scl;

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