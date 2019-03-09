package tokens;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token recognizer class, recognizes tokens in the input line
 * @author Seth Gillett
 *
 */
public class TokenMatcher implements Comparable<TokenMatcher> {
	
	/**
	 * The pattern used to match the token
	 */
	private Pattern p;
	
	/**
	 * The matcher object of the current input line
	 */
	private Matcher m;
	
	/**
	 * The current input line
	 */
	private String inputLine;
	
	/**
	 * The token that this pattern is equivalent to
	 */
	public Tk token;
	
	/**
	 * Creates a class to recognize a given token
	 * @param pattern The regex pattern to determine matches
	 * @param token The token this recognizer corresponds to
	 * @param priority The priority of the token compared to other tokens
	 */
	public TokenMatcher(String pattern, Tk token) {
		this.p = Pattern.compile(pattern);
		this.token = token;
	}
	
	/**
	 * Reads in an input line
	 * @param line The input line
	 */
	public void read(String line) {
		// If a reference to the input line already exists, then it has already been read
		if (inputLine == line) {
			return;
		}
		else {
			this.inputLine = line;
			m = p.matcher(line);
		}
	}
	
	/**
	 * Determines whether the token can be found (again) in the given input string
	 * @param start The starting index to look
	 * @return The index the token was found at or -1
	 */
	public int nextOccurrence(int start) {
		if (m.find(start)) {
			//System.out.println("Matching: <" + inputLine.substring(m.start(), m.end()) + ">");
			return m.start();
		}
		else {
			return -1;
		}
	}
	
	/**
	 * Returns the ending index of the last found occurrence
	 * @return The ending index of the last found occurrence
	 */
	public int occurrenceEnd() {
		return m.end();
	}
	
	@Override
	public int compareTo(TokenMatcher other) {
		return this.token.priority - other.token.priority;
	}
	
	@Override
	public String toString() {
		return p + " - " + token;
	}
}
