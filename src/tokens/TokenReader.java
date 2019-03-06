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
		// Must be at least 2 tokens
		if (tkHstIdx >= 2) {
			// Token history is 1 ahead of current token
			tk = tkHst.get(tkHstIdx - 2).token;
			// Decrement token history index
			tkHstIdx -= 1;
		}
	}
	
	/**
	 * Attempts to match the next token closest to the current line index
	 */
	private void matchNextToken() {
		if (lineIdx == currentLine.length()) {
			// Add token to history
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
			// Attempts to capture the token string by advancing line idx past non-whitespace
			int lastLineIdx = lineIdx;
			errorAdvanceNonWhitespace();
			
			String tokenStr = currentLine.substring(lastLineIdx, lineIdx);
			// Adds error to token history
			tkHst.add(new ReadToken(lastLineIdx, Tk.ERROR, tokenStr));
			// Return
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
	
	/**
	 * Advances past non-whitespace to discover a token that caused an error
	 */
	private void errorAdvanceNonWhitespace() {
		while (lineIdx < currentLine.length() && currentLine.charAt(lineIdx) != ' ') {
			lineIdx ++;
		}
	}
}
