package tokens;
import java.util.ArrayList;

public abstract class TokenReader {
	/**
	 * The current token
	 */
	public static Tk tk;
	/**
	 * Where in the list of tokens are we
	 */
	private static int tkHstIdx;
	/**
	 * The current line the parser is interpreting
	 */
	private static String currentLine;
	/**
	 * The current index in the line being parsed
	 */
	private static int lineIdx;
	/**
	 * The history of tokens that have been read
	 */
	static ArrayList<ReadToken> tkHst = new ArrayList<>();
	/**
	 * Reads in a new line from the parser
	 * @param input The input line
	 */
	public static void readLine(String input) {
		currentLine = input;
		lineIdx = 0;
		tkHstIdx = 0;
		tk = null;
		tkHst.clear();
	}
	/**
	 * Attempts to find the next token closest to the current line index
	 */
	public static void nextToken() {
		if (tk != null && tk == Tk.EOL) {
			// Clears the current line from memory
			currentLine = null;
			// Returns immediately
			return;
		}
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
	 * Peeks the next token without moving the reader forward
	 * @return The next token
	 */
	public static Tk peekNextToken() {
		nextToken();
		Tk next = tk;
		prevToken();
		return next;
  }
	/**
	 * Moves the current token BACK
	 */
	public static void prevToken() {
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
	public static void restartLine() {
		tk = null;
		tkHstIdx = 0;
	}
	/**
	 * Attempts to match the next token closest to the current line index
	 */
	private static void matchNextToken() {
		if (lineIdx == currentLine.length()) {
			// Add EOL token to history
			tkHst.add(new ReadToken(Tk.EOL));
			// Return
			return;
		}
		
		boolean found = false;
		for (Tk token : Tk.values()) {
			
			// Finds the next occurrence of a token
			int[] idx = token.find(currentLine, lineIdx);
			
			// Only accepts the match if it was found at the current line index
			if (idx != null && idx[0] == lineIdx) {
				// Get the token string
				String tokenStr = currentLine.substring(idx[0], idx[1]);
				// Add token to history
				tkHst.add(new ReadToken(token, tokenStr));
				// Moves the line index forward
				lineIdx = idx[1];
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
			// Attempts to read the error causing token
			String errorToken = currentLine.substring(lineIdx, endOfErrorToken());
			// Adds the error token to the history
			tkHst.add(new ReadToken(Tk.ERROR, errorToken));
			return;
		}
	}
	/**
	 * Returns the string of the current token
	 * @return The string of the current token
	 */
	public static String tokenStr() {
		return tkHst.get(tkHstIdx - 1).tokenStr();
	}
	/**
	 * Advances past whitespace in the current line
	 */
	private static void advanceWhitespace() {
		while (lineIdx < currentLine.length() && currentLine.charAt(lineIdx) == ' ') {
			lineIdx++;
		}
	}
	/**
	 * Attempts to find the end of an error causing token
	 * @return The ending index of the error causing token
	 */
	private static int endOfErrorToken() {
		int errorEndIdx = lineIdx;
		while (errorEndIdx < currentLine.length() && 
				currentLine.charAt(errorEndIdx) != ' ' &&
				currentLine.charAt(errorEndIdx) != '(' &&
				currentLine.charAt(errorEndIdx) != ')') {
			errorEndIdx ++;
		}
		return errorEndIdx;
	}
}
