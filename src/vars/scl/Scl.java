package vars.scl;

/**
 * Scalars of any type, abstract or literal
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
	public static Scl add(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(a.val + b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Returns the result of a - b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl sub(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(a.val - b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Negates a scalar
	 * @param a The scalar
	 * @return The negated scalar
	 */
	public static Scl neg(Scl a) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(-a.val, a.decimalPlaces);
	}
	
	/**
	 * Returns the result of a * b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl mult(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(a.val * b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Returns the result of a / b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl div(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(a.val / b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Returns the result of a ^ b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl exp(Scl a, Scl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new Scl(Math.pow(a.val, b.val), Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	public static Boolean great_or_equal(Scl a, Scl b) {
		return a.val >= b.val;
	}
	
	public static Boolean less_or_equal(Scl a, Scl b) {
		return a.val <= b.val;
	}
	
	public static Boolean greater(Scl a, Scl b) {
		return a.val > b.val;
	}
	
	public static Boolean lesser(Scl a, Scl b) {
		return a.val < b.val;
	}
	
	public static Boolean equal(Scl a, Scl b) {
		return a.val == b.val;
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
			this.decimalPlaces = 0;
		}
		
		this.val = Double.parseDouble(val);
	}
	
	/**
	 * Copies a scalar from another scalar
	 * @param other The other scalar
	 */
	public Scl(Scl other) {
		this.val = other.val;
		this.decimalPlaces = other.decimalPlaces;
	}
	
	/**
	 * Creates a new scalar from a double and # of decimal places, will automatically extend or reduce decimals as needed
	 * @param val The double value
	 * @param decimalPlaces The number of decimal places to preserve
	 */
	Scl(double val, int decimalPlaces) {
		this.val = val;
		if (this.isInt()) {
			this.decimalPlaces = 0;
		}
		else {
			this.decimalPlaces = Math.max(decimalPlaces, 2);
		}
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
	
	/**
	 * Finds the character length of the scalar
	 * @return The character length of the scalar
	 */
	public int strLength() {
		return this.toString().length();
	}
	
	@Override
	public String toString() {
		return String.format("%." + decimalPlaces + "f", val);
	}
}
