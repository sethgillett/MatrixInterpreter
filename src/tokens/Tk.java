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
	// PRIORITY 2-7: Math operators
	EXP_OP(2), MULT_OP(3), DIV_OP(4), NEG_OP(5), ADD_OP(6), SUB_OP(7),
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
}
