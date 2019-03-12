package vars.scl;

/**
 * @deprecated
 * @author Seth Gillett
 *
 */
public interface SclType {
	/**
	 * Returns the result of a + b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public abstract Scl add(Scl a, Scl b);
	
	/**
	 * Returns the result of a - b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public abstract Scl sub(Scl a, Scl b);
	
	/**
	 * Returns the result of a * b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public abstract Scl mult(Scl a, Scl b);
	
	/**
	 * Returns the result of a / b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public abstract Scl div(Scl a, Scl b);
	
	/**
	 * Returns the result of a ^ b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public abstract Scl exp(Scl a, Scl b);
	
	/**
	 * Returns -a
	 * @param a The scalar
	 * @return The negated scalar
	 */
	public abstract Scl neg(Scl a);
}
