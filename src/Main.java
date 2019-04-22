import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import io.Input;
import io.Output;
import parser.primary.Parser;
import parser.primary.ParserType;
import vars.function.DynamicFunction;

public class Main {
	
	public static void main(String[] args) {
		REPL();
	}
	
	private static Input terminalInput() {
		// New input reader
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		return new Input(
				() -> {return br.readLine();},	// Method to read input
				() -> {							// Method to close input stream
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}
	
	private static Consumer<String> terminalOutput() {
		// Outputs to the console
		return (value -> System.out.print(value));
	}
	
	public static void REPL() {
		// Module for user output
		Output.output = terminalOutput();
		// Module for user input
		ParserType.input = terminalInput();
		// Primary parser
		Parser p = new Parser();
		// String for reading input
		String inputLine = "";
		// REPL will be implemented as a function with lines added dynamically
		DynamicFunction main = new DynamicFunction(p, null);
		// Start up the function with no arguments
		main.start(null);
		
		while (true) {
			Output.print(">>> ");
			inputLine = ParserType.input.readLine();
			// Program exit condition
			if (inputLine.matches("^\\s*\\b(exit|quit)\\b\\s*$")) {
				break;
			}
			
			main.read(inputLine);
			main.run();
		}
		
		ParserType.input.close();
	}
}
