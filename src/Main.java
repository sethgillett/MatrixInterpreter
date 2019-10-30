import io.Input;
import io.Output;
import parser.Parser;

public class Main {

  public static void main(String[] args) {
    REPL();
  }

  public static void REPL() {
    // String for reading input
    String inputLine = "";

    while (true) {
      Output.print(">>> ");
      inputLine = Input.readLine();
      // Program exit condition
      if (inputLine.matches("^\\s*\\b(exit|quit)\\b\\s*$")) {
        break;
      }

      Parser.read(inputLine);
    }

    Input.close();
  }
}
