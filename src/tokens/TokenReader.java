package tokens;
import java.util.Arrays;

public class TokenReader {
	
	/**
	 * The next token
	 */
	public Tk tk;
	/**
	 * The current line the parser is interpreting
	 */
	private String currentLine;
	
	/**
	 * The location in the current line the parser is reading
	 */
	private int currentLineIdx;
	
	/**
	 * The starting index of the last matched token
	 */
	private int lastTokenBeginIdx;
	/**
	 * The ending index of the last matched token
	 */
	private int lastTokenEndIdx;
	
	/**
	 * The array of tokens that can possibly be recognized
	 */
	private TokenMatcher[] tokens;
	
	/**
	 * Constructs a token reader from the provided tokens
	 * @param tokens The provided list of tokens
	 */
	public TokenReader(TokenMatcher...tokens) {
		// Sort tokens by priority - lower numbers checked first
		Arrays.sort(tokens);
		// Stores list of tokens
		this.tokens = tokens;
	}
	
	/**
	 * Finds the next token of highest priority that is matched
	 * @param input The input line
	 * @return The token that was found
	 */
	public void readLine(String input) {
		this.currentLine = input;
		this.currentLineIdx = 0;
	}
	
	/**
	 * Attempts to find the next token closest to the current line index
	 * @return The next token found or null if none found or end of line
	 */
	public void nextToken() {
		this.advanceWhitespace();
		if (currentLineIdx == currentLine.length()) {
			// Indicates the last token is the EOL token
			lastTokenEndIdx = -1;
			// End of line token
			tk = Tk.EOL;
			return;
		}
		Tk nxt = null;
		for (TokenMatcher t : tokens) {
			
			// Finds the next occurrence of a token and checks to see if it's earlier than other tokens
			t.read(currentLine);
			int idx = t.nextOccurrence(currentLineIdx);
			
			// Moves on if no matches were found
			if (idx == -1) {
				continue;
			}
			// Only accepts the match if it starts at the current line index
			else if (idx == currentLineIdx) {
				nxt = t.token;
				lastTokenBeginIdx = idx;
				lastTokenEndIdx = t.occurrenceEnd();
				break;
			}
			else {
				continue;
			}
		}
		if (nxt == null) {
			// Prints a pointer to the unrecognized token
			System.out.printf("%" + (currentLineIdx+5) + "s", "^");
			System.out.println(" unrecognized token");
			
			// Attempts to capture the token that caused the error
			lastTokenBeginIdx = currentLineIdx;
			errorAdvanceNonWhitespace();
			lastTokenEndIdx = currentLineIdx;
			
			tk = Tk.ERROR;
			return;
		}
		currentLineIdx = lastTokenEndIdx;
		tk = nxt;
	}
	
	/**
	 * Returns the string of the last token's match
	 * @return The string of the last token's match
	 */
	public String lastTokenStr() {
		if (lastTokenBeginIdx == -1) {
			return "EOL";
		}
		return currentLine.substring(lastTokenBeginIdx, lastTokenEndIdx);
	}
	
	/**
	 * Prints a pointer to the last token
	 */
	public void printLastTokenPointer() {
		System.out.printf("%" + (lastTokenBeginIdx+5) + "s", "^");
	}
	
	/**
	 * Advances past whitespace in the current line
	 */
	private void advanceWhitespace() {
		while (currentLineIdx < currentLine.length() && currentLine.charAt(currentLineIdx) == ' ') {
			currentLineIdx ++;
		}
	}
	
	/**
	 * Advances past non-whitespace to discover a token that caused an error
	 */
	private void errorAdvanceNonWhitespace() {
		while (currentLineIdx < currentLine.length() && currentLine.charAt(currentLineIdx) != ' ') {
			currentLineIdx ++;
		}
	}
}
