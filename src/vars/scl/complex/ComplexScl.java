package vars.scl.complex;

import vars.scl.Scl;

/**
 * <ul>
 * 	<li>Scalar that CANNOT BE SIMPLIFIED because it is represented ABSTRACTLY (e.g. a/5 or a^6 + a^4)</li>
 * 	<li>Any scalar with no associated value automatically becomes abstract</li>
 * </ul>
 * 
 * <p><b>Not yet implemented.</b></p>
 * 
 * @author Seth Gillett
 *
 */
public class ComplexScl extends Scl {
	/**
	 * The representation of the scalar
	 */
	private String rep;
	/**
	 * Instantiates a complex scalar
	 * @param rep The original representation of the scalar
	 */
	public ComplexScl(String rep) {
		this.rep = rep;
	}
	/**
	 * Returns the result of a + b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl add(Scl a, Scl b) {
		return null;
	}
	/**
	 * Returns the result of a - b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl sub(Scl a, Scl b) {
		return null;
	}
	/**
	 * Returns the result of a * b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl mult(Scl a, Scl b) {
		return null;

	}
	/**
	 * Returns the result of a / b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl div(Scl a, Scl b) {
		return null;
	}
	/**
	 * Returns the result of a ^ b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl exp(Scl a, Scl b) {
		return null;
	}
	/**
	 * Returns -a
	 * @param a The scalar
	 * @return The negated scalar
	 */
	public static Scl neg(Scl a) {
		return null; 
	}

	@Override
	public String toString() {
		return rep;
	}
}
