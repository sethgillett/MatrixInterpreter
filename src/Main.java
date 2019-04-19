import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.Input;
import io.Output;
import parser.primary.Parser;
import vars.function.Function;

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
		Input input = terminalInput();
		// Primary parser
		Parser p = new Parser(input);
		// String for reading input
		String inputLine = "";
		// List of lines in the function
		List<String> lines = new ArrayList<String>();
		// REPL will be implemented as a function with lines added dynamically
		Function main = new Function(p, null, new ArrayList<String>(), lines);
		// Start up the function with no arguments
		main.start(new ArrayList<>());
		
		while (true) {
			Output.print(">>> ");
			inputLine = input.readLine();
			// Program exit condition
			if (inputLine.matches("^\\s*\\b(exit|quit)\\b\\s*$")) {
				break;
			}
			
			lines.add(inputLine);
			main.execNextLine();
		}
		
		input.close();
	}
}
