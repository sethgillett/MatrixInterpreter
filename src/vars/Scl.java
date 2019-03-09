package vars;
/**
 * Variables created in MI
 * @author Seth Gillett
 *
 */
public class Scl {
	/**
	 * The variable being stored
	 */
	private double val;
	
	/**
	 * The number of decimal places (preserved) in the number
	 */
	private int decimalPlaces;
	
	/**
	 * Scalar representing zero
	 */
	public static Scl ZERO = new Scl(0.0, 0);
	
	/**
	 * Scalar representing one
	 */
	public static Scl ONE = new Scl(1.0, 0);
	
	/**
	 * Returns the result of a + b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl ADD(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(a.val + b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Returns the result of a - b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl SUB(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(a.val - b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Negates a scalar
	 * @param a The scalar
	 * @return The negated scalar
	 */
	public static Scl NEG(Scl a) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(-a.val, a.decimalPlaces);
	}
	
	/**
	 * Returns the result of a * b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl MULT(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(a.val * b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Returns the result of a / b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl DIV(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(a.val / b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Returns the result of a ^ b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl EXP(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(Math.pow(a.val, b.val), Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Parses the value as a double and preserves the number of decimal places
	 * @param val The string of the number
	 */
	public Scl(String val) {
		int dotIdx = val.indexOf('.');
		if (dotIdx != -1) {
			this.decimalPlaces = val.length() - 1 - dotIdx;
		}
		else {
			this.decimalPlaces = 2;
		}
		
		this.val = Double.parseDouble(val);
	}
	
	/**
	 * Creates a new scalar from a double and # of decimal places
	 * @param val The double value
	 * @param decimalPlaces The number of decimal places to preserve
	 */
	protected Scl(double val, int decimalPlaces) {
		this.val = val;
		this.decimalPlaces = decimalPlaces;
	}
	
	/**
	 * Checks if the current scalar is an int
	 * @return True or false
	 */
	public boolean isInt() {
		return (val <= Integer.MAX_VALUE && val >= Integer.MIN_VALUE && val == Math.floor(val));
	}
	
	/**
	 * Returns the current scalar as an int
	 * @return The scalar as an int
	 */
	public int valueAsInt() {
		return (int) Math.floor(val);
	}
	
	@Override
	public String toString() {
		return String.format("%." + decimalPlaces + "f", val);
	}
}
