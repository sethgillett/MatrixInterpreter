import java.util.Scanner;

import parser.Parser;

public class Main {
	
	public static void main(String[] args) {
		
		Scanner s = new Scanner(System.in);
		
		Parser p = new Parser(s);
		
		String input = "";
		
		while (true) {
			System.out.print(">>> ");
			input = s.nextLine();
			
			if (input.matches("^(\\s*)$")) {
				continue;
			}
			
			if (input.matches("^\\s*(end|exit|quit)\\s*$")) {
				break;
			}
			
			p.read(input);
		}
		
		s.close();
		
		System.exit(0);
	}
}
