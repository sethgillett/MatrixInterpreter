import java.util.Scanner;

import parser.Parser;

public class Main {
	
	public static void main(String[] args) {
		
		Parser p = new Parser();
		
		Scanner s = new Scanner(System.in);
		
		String input = "";
		
		while (true) {
			System.out.print(">>> ");
			input = s.nextLine();
			
			if (input.matches("^\\s*(end)\\s*$")) {
				break;
			}
			
			p.read(input);
		}
		
		s.close();
		
		System.exit(0);
	}
}
