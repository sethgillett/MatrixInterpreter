package tokens;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token class
 * @author Seth Gillett
 *
 */
public enum Tk implements Comparable<Tk> {
	// PRIORITY 0: System
	ERROR(0, null), EOL(0, null),
	// PRIORITY 1: Symbols
	TYPE(1, "\\b(?:mat|scl)\\b"), LPAREN(1, "\\("), RPAREN(1, "\\)"),
	ASSIGNMENT_OP(1, "="), LBRACKET(1, "\\["), RBRACKET(1, "\\]"),
	COMMA(1, "\\,"), ARROW(1, "\\-\\>"), NOT(1, "\\!"),
	// PRIORITY 1: Keywords
	IF(1, "\\b(?:if)\\b"), FOR(1, "\\b(?:for)\\b"), IN(1, "\\b(?:in)\\b"),
	BY(1, "\\b(?:by)\\b"), WHILE(1, "\\b(?:while)\\b"),
	// PRIORITY 2: Language defined commands
	NULL_CMD(2, "\\b(?:del|prn|print)(?=\\(.*?\\))\\b"),
	VAR_CMD(2, "\\b(?:id|zero)(?=\\(.*?\\))\\b"),
	MTX_CMD(2, "\\b(?:[A-Z][a-z]*)\\.(?:.+)(?=\\(.*?\\))\\b"),
	// PRIORITY 10-15: Math operators
	EXP_OP(10, "\\^"), MULT_OP(11, "\\*"), DIV_OP(12, "\\/"),
	ADD_OP(13, "\\+"), SUB_OP(14, "\\-"), NEG_OP(15, null),
	// PRIORITY 16: Boolean operators
	LESS_OR_EQUAL(16,"\\<\\="), GREAT_OR_EQUAL(16,"\\>\\="),
	GREATER_OP(17, "\\>"), LESSER_OP(17, "\\<"), EQUAL_OP(17, null),
	AND(18, "\\&\\&"), OR(19, "\\|\\|"),
	// PRIORITY 50+: User defined symbols
	MTX_NAME(50, "\\b(?:[A-Z][a-z]*)\\b"),
	SCL_NAME(50, "\\b(?:[a-z]+)\\b"),
	NUM_LIT(50, "(?:\\d+)?(?:\\.?\\d+)(?:[Ee][+-]?\\d+)?");
	/**
	 * The order of priority for tokens
	 */
	final int priority;
	/**
	 * The pattern used to match the token
	 */
	private Pattern pattern;
	/**
	 * Initializes a new token
	 * @param priority The priority of the token over other tokens
	 * @param pattern The regex pattern used to match this token
	 */
	private Tk(int priority, String strPattern) {
		this.priority = priority;
		if (strPattern != null) {
			this.pattern = Pattern.compile(strPattern);
		}
		else {
			this.pattern = null;
		}
	}
	/**
	 * Attempts to find this token in a string
	 * @param line The string
	 * @param start The starting index
	 * @return {start, end} or null
	 */
	public int[] find(String line, int start) {
		if (this.pattern == null)
			return null;
		Matcher m = pattern.matcher(line);
		if (m.find(start)) {
			int beg = m.start();
			int end = m.end();
			return new int[] {beg, end};
		}
		else {
			return null;
		}
	}
	/**
	 * Is this token * + ^ / or -
	 * @param tk The token
	 * @return True or false
	 */
	public static boolean isMathOp(Tk tk) {
		return (tk == Tk.MULT_OP || tk == Tk.DIV_OP 
				|| tk == Tk.ADD_OP || tk == Tk.SUB_OP
				|| tk == Tk.EXP_OP);
	}
	
	public static boolean isBoolOp(Tk tk) {
		return (tk == Tk.EQUAL_OP || tk == Tk.GREAT_OR_EQUAL
				|| tk == Tk.LESS_OR_EQUAL || tk == Tk.LESSER_OP
				|| tk == Tk.GREATER_OP || tk == Tk.AND || tk == Tk.OR);
	}
	/**
	 * Is this token a ( or a )
	 * @param tk
	 * @return True or false
	 */
	public static boolean isParen(Tk tk) {
		return (tk == Tk.LPAREN || tk == Tk.RPAREN);
	}
	
	/**
	 * If this token is greater than other
	 * @param other The other math token
	 * @return True if this is greater, false otherwise
	 */
	public boolean higherPrec(Tk other) {
		return (this.priority < other.priority);
	}
	
	@Override
	public String toString() {
		switch(this) {
		case ADD_OP:
			return "+";
		case ASSIGNMENT_OP:
			return "=";
		case NULL_CMD:
			return "command without return value";
		case VAR_CMD:
			return "command with return value";
		case COMMA:
			return ",";
		case DIV_OP:
			return "/";
		case EOL:
			return "EOL";
		case ERROR:
			return "ERROR";
		case EXP_OP:
			return "^";
		case LBRACKET:
			return "[";
		case LPAREN:
			return "(";
		case MTX_NAME:
			return "mtx_name";
		case MULT_OP:
			return "*";
		case NEG_OP:
			return "-";
		case NUM_LIT:
			return "number";
		case RBRACKET:
			return "]";
		case RPAREN:
			return ")";
		case SCL_NAME:
			return "scl_name";
		case SUB_OP:
			return "-";
		case TYPE:
			return "mtx or scl";
		case AND:
			return "&&";
		case BY:
			return "by";
		case EQUAL_OP:
			return "=";
		case FOR:
			return "for";
		case GREATER_OP:
			return ">";
		case GREAT_OR_EQUAL:
			return ">=";
		case IF:
			return "if";
		case IN:
			return "in";
		case LESSER_OP:
			return "<";
		case LESS_OR_EQUAL:
			return "<=";
		case MTX_CMD:
			return "mtx_name.cmd(...)";
		case OR:
			return "||";
		case WHILE:
			return "while";
		case ARROW:
			return "->";
		case NOT:
			return "!";
		default:
			return null;
		}
	}
}
