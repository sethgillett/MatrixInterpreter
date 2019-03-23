package files;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import parser.primary.Parser;

public class FileReader {
	public static void runFile(String path) throws IOException {
		InputStream i = FileReader.class.getResourceAsStream(path);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(i));
		
		Parser p = new Parser(null);
		
		String input = "";
		
		while (true) {
			input = br.readLine();
			
			// End of file
			if (input == null) {
				break;
			}
			
			// Line of whitespace
			else if (input.matches("^(\\s*)$")) {
				continue;
			}
			
			// Line with any exit command
			else if (input.matches("^\\s*(end|exit|quit)\\s*$")) {
				break;
			}
			
			// Read and execute input
			p.read(input);
		}
		
		br.close();
	}
}
