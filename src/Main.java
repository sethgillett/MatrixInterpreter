import io.Input;
import io.Output;
import parser.ParserType;
import parser.primary.Parser;

public class Main {
	
	public static void main(String[] args) {
		REPL();
	}
	
	public static void REPL() {
		// Primary parser
		ParserType.primary = new Parser();
		// String for reading input
		String inputLine = "";
		
		while (true) {
			Output.print(">>> ");
			inputLine = Input.readLine();
			// Program exit condition
			if (inputLine.matches("^\\s*\\b(exit|quit)\\b\\s*$")) {
				break;
			}
			
			ParserType.primary.read(inputLine);
		}
		
		Input.close();
	}
}
