package vars.scl;

public class SimpleScl extends Scl {
	/**
	 * The variable being stored
	 */
	private double val;
	
	/**
	 * The number of decimal places (preserved) in the number
	 */
	private int decimalPlaces;
	/**
	 * Returns the result of a + b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static SimpleScl addSimple(SimpleScl a, SimpleScl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new SimpleScl(a.val + b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Returns the result of a - b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static SimpleScl subSimple(SimpleScl a, SimpleScl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new SimpleScl(a.val - b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Negates a scalar
	 * @param a The scalar
	 * @return The negated scalar
	 */
	public static SimpleScl negSimple(SimpleScl a) {
		// Returns a new scalar representing the result with preserved decimal places
		return new SimpleScl(-a.val, a.decimalPlaces);
	}
	
	/**
	 * Returns the result of a * b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static SimpleScl multSimple(SimpleScl a, SimpleScl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new SimpleScl(a.val * b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Returns the result of a / b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static SimpleScl divSimple(SimpleScl a, SimpleScl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new SimpleScl(a.val / b.val, Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Returns the result of a ^ b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static SimpleScl expSimple(SimpleScl a, SimpleScl b) {
		// Returns a new scalar representing the result with preserved decimal places
		return new SimpleScl(Math.pow(a.val, b.val), Math.max(a.decimalPlaces, b.decimalPlaces));
	}
	
	/**
	 * Parses the value as a double and preserves the number of decimal places
	 * @param val The string of the number
	 */
	public SimpleScl(String val) {
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
	 * Creates a new scalar from a double and # of decimal places, will automatically extend or reduce decimals as needed
	 * @param val The double value
	 * @param decimalPlaces The number of decimal places to preserve
	 */
	SimpleScl(double val, int decimalPlaces) {
		this.val = val;
		if (this.isInt()) {
			this.decimalPlaces = 0;
		}
		else {
			this.decimalPlaces = Math.max(decimalPlaces, 1);
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
