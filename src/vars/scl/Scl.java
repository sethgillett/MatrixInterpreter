package vars.scl;

import vars.scl.complex.ComplexScl;

/**
 * Scalars of any type, abstract or literal
 * @author Seth Gillett
 *
 */
public abstract class Scl {
	/**
	 * If a subclass doesn't implement the toString method, it will not compile
	 * @return The string representation of the scalar
	 */
	@Override
	public abstract String toString();
	/**
	 * Returns the length in characters of the scalar
	 * @return The length in characters of the scalar
	 */
	public int strLength() {
		return toString().length();
	}
	/**
	 * Returns the result of a + b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl add(Scl a, Scl b) {
		if (a instanceof SimpleScl && b instanceof SimpleScl)
			return SimpleScl.addSimple((SimpleScl) a,(SimpleScl) b);
		else
			return ComplexScl.add(a, b);
	}
	
	/**
	 * Returns the result of a - b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl sub(Scl a, Scl b) {
		if (a instanceof SimpleScl && b instanceof SimpleScl)
			return SimpleScl.subSimple((SimpleScl) a, (SimpleScl) b);
		else
			return ComplexScl.sub(a, b);
	}
	
	/**
	 * Returns the result of a * b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl mult(Scl a, Scl b) {
		if (a instanceof SimpleScl && b instanceof SimpleScl)
			return SimpleScl.multSimple((SimpleScl) a, (SimpleScl) b);
		else
			return ComplexScl.mult(a, b);
		
	}
	
	/**
	 * Returns the result of a / b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl div(Scl a, Scl b) {
		if (a instanceof SimpleScl && b instanceof SimpleScl)
			return SimpleScl.divSimple((SimpleScl) a, (SimpleScl) b);
		else
			return ComplexScl.div(a, b);
	}
	
	/**
	 * Returns the result of a ^ b
	 * @param a The 1st scalar
	 * @param b The 2nd scalar
	 * @return The resulting scalar
	 */
	public static Scl exp(Scl a, Scl b) {
		if (a instanceof SimpleScl && b instanceof SimpleScl)
			return SimpleScl.expSimple((SimpleScl) a, (SimpleScl) b);
		else
			return null;
	}
	
	/**
	 * Returns -a
	 * @param a The scalar
	 * @return The negated scalar
	 */
	public static Scl neg(Scl a) {
		if (a instanceof SimpleScl)
			return SimpleScl.negSimple((SimpleScl) a);
		else
			return null; 
	}
	
	/**
	 * Scalar representing zero
	 */
	public static Scl ZERO = new SimpleScl(0.0, 0);
	
	/**
	 * Scalar representing one
	 */
	public static Scl ONE = new SimpleScl(1.0, 0);
}
