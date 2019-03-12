package tokens;
import java.util.ArrayList;
import java.util.Arrays;

public class TokenReader {
	/**
	 * The current token
	 */
	public Tk tk;
	
	/**
	 * Where in the list of tokens are we
	 */
	private int tkHstIdx;
	
	/**
	 * The current line the parser is interpreting
	 */
	private String currentLine;
	
	/**
	 * The current index in the line being parsed
	 */
	private int lineIdx;
	
	/**
	 * The history of tokens that have been read
	 */
	ArrayList<ReadToken> tkHst;
	
	/**
	 * The array of token matchers that can possibly be matched
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
		// Creates token history arraylist
		this.tkHst = new ArrayList<>();
	}
	
	/**
	 * Reads in a new line from the parser
	 * @param input The input line
	 */
	public void readLine(String input) {
		this.currentLine = input;
		this.lineIdx = 0;
		this.tkHstIdx = 0;
		this.tk = null;
		this.tkHst.clear();
	}
	
	/**
	 * Attempts to find the next token closest to the current line index
	 */
	public void nextToken() {
		// Token hasn't already been read
		if (tkHstIdx == tkHst.size()) {
			// Match to provided regex
			matchNextToken();
		}
		// Sets the tk variable equal to the last token in the arraylist
		tk = tkHst.get(tkHstIdx).token;
		// Increments index variable
		tkHstIdx += 1;
		// Advances past whitespace
		advanceWhitespace();
	}
	
	/**
	 * Moves the current token BACK
	 */
	public void prevToken() {
		// At least 2 tokens present
		if (tkHstIdx >= 2) {
			// Token history is 1 ahead of current token
			tk = tkHst.get(tkHstIdx - 2).token;
			// Decrement token history index
			tkHstIdx -= 1;
		}
		// If only 1 token is present, tk will be set to null
		else if (tkHstIdx == 1) {
			tk = null;
			tkHstIdx = 0;
		}
	}
	
	/**
	 * Restarts the reader at the beginning of the line
	 */
	public void restartLine() {
		tk = null;
		tkHstIdx = 0;
	}
	
	/**
	 * Attempts to match the next token closest to the current line index
	 */
	private void matchNextToken() {
		if (lineIdx == currentLine.length()) {
			// Add EOL token to history
			tkHst.add(new ReadToken(-1, Tk.EOL, "EOL"));
			// Return
			return;
		}
		
		boolean found = false;
		for (TokenMatcher pattern : tokens) {
			
			// Finds the next occurrence of a token
			pattern.read(currentLine);
			int idx = pattern.nextOccurrence(lineIdx);
			
			// Only accepts the match if it was found at the current line index
			if (idx != -1 && idx == lineIdx) {
				// Get the token string
				String tokenStr = currentLine.substring(idx, pattern.occurrenceEnd());
				// Add token to history
				tkHst.add(new ReadToken(idx, pattern.token, tokenStr));
				// Moves the line index forward
				lineIdx = pattern.occurrenceEnd();
				// Mark that we found a token
				found = true;
				// Return
				break;
			}
			else {
				continue;
			}
		}
		if (found == false) {
			// Prints a pointer to the unrecognized token
			System.out.printf("%" + (lineIdx+5) + "s", "^");
			System.out.println(" unrecognized token");
			tkHst.add(new ReadToken(lineIdx, Tk.ERROR, "ERROR"));
			return;
		}
	}
	
	/**
	 * Returns the string of the current token
	 * @return The string of the current token
	 */
	public String tokenStr() {
		return tkHst.get(tkHstIdx - 1).tokenStr;
	}
	
	/**
	 * Prints a pointer to the last token
	 */
	public void tokenPtr() {
		// Prints a pointer to the beginning index of the previous token
		System.out.printf("%" + (tkHst.get(tkHstIdx - 1).idx + 5) + "s", "^");
	}
	
	/**
	 * Advances past whitespace in the current line
	 */
	private void advanceWhitespace() {
		while (lineIdx < currentLine.length() && currentLine.charAt(lineIdx) == ' ') {
			lineIdx++;
		}
	}
	
}
