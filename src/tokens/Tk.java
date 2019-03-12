package tokens;
/**
 * Token class
 * @author Seth Gillett
 *
 */
public enum Tk {
	// PRIORITY 0: System
	ERROR(0), EOL(0),
	// PRIORITY 1: Reserved symbols
	CMD(1), TYPE(1), LPAREN(1), RPAREN(1), ASSIGNMENT(1),
	LBRACKET(1), RBRACKET(1), COMMA(1),
	// PRIORITY 2-6: Math operators
	EXP_OP(2), MULT_OP(3), DIV_OP(4), ADD_OP(5), SUB_OP(6), NEG_OP(7),
	// PRIORITY 20+: User defined symbols
	MTX_NAME(20), SCL_NAME(20), NUM_LIT(20);
	
	final int priority;
	
	private Tk(int priority) {
		this.priority = priority;
	}
	
	public static boolean isMathOp(Tk tk) {
		return (tk == Tk.MULT_OP || tk == Tk.DIV_OP 
				|| tk == Tk.ADD_OP || tk == Tk.SUB_OP
				|| tk == Tk.EXP_OP);
	}
	
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
		case ASSIGNMENT:
			return "=";
		case CMD:
			return "command";
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
			return "matrix name";
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
			return "scalar name";
		case SUB_OP:
			return "-";
		case TYPE:
			return "matrix or scalar";
		default:
			return null;
		}
	}
}
