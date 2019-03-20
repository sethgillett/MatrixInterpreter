import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import parser.primary.Parser;

public class Main {
	
	public static void main(String[] args) {
		try {
			REPL();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void REPL() throws IOException {
		//Scanner s = new Scanner(System.in);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		Parser p = new Parser(br);
		
		String input = "";
		
		while (true) {
			System.out.print(">>> ");
			input = br.readLine();
			
			if (input.matches("^(\\s*)$")) {
				continue;
			}
			
			if (input.matches("^\\s*(end|exit|quit)\\s*$")) {
				break;
			}
			
			p.read(input);
		}
		
		br.close();
	}
}
