package vars.scl;

import java.math.BigDecimal;
import java.math.MathContext;

import io.Output;
import vars.Var;
import vars.bool.Bool;

/**
 * Scalars of any type, abstract or literal 
 * @author Seth Gillett
 *
 */
public class Scl extends Var {
  /**
   * The variable being stored
   */
  private BigDecimal val;

  /**
   * Scalar representing zero
   */
  public static Scl ZERO = new Scl(BigDecimal.ZERO);

  /**
   * Scalar representing one
   */
  public static Scl ONE = new Scl(BigDecimal.ONE);

  /**
   * Scalar representing ten
   */
  public static Scl TEN = new Scl(BigDecimal.TEN);

  /**
   * Returns the result of a + b   
   * @param a The 1st scalar
   * @param b The 2nd scalar
   * @return The resulting scalar
   */
  public static Scl add(Scl a, Scl b) {
    // Optimized cases:
    if (a == Scl.ZERO)
      return b;
    if (b == Scl.ZERO)
      return a;
    // Returns a new scalar representing the result with preserved decimal places
    return new Scl(a.val.add(b.val, maxPrecision(a, b)));
  }

  /**
   * Returns the result of a - b   
   * @param a The 1st scalar
   * @param b The 2nd scalar
   * @return The resulting scalar
   */
  public static Scl sub(Scl a, Scl b) {
    // Optimized cases:
    if (a == Scl.ZERO)
      return b;
    if (b == Scl.ZERO)
      return a;
    // Returns a new scalar representing the result with preserved decimal places
    return new Scl(a.val.subtract(b.val, maxPrecision(a, b)));
  }

  /**
   * Negates a scalar   
   * @param a The scalar
   * @return The negated scalar
   */
  public static Scl neg(Scl a) {
    // Optimized cases:
    if (a == Scl.ZERO)
      return a;
    // Returns a new scalar representing the result with preserved decimal places
    return new Scl(a.val.negate(maxPrecision(a, a)));
  }

  /**
   * Returns the result of a * b   
   * @param a The 1st scalar
   * @param b The 2nd scalar
   * @return The resulting scalar
   */
  public static Scl mult(Scl a, Scl b) {
    // Optimized cases:
    if (a == Scl.ZERO)
      return Scl.ZERO;
    if (b == Scl.ZERO)
      return Scl.ZERO;
    if (a == Scl.ONE)
      return b;
    if (b == Scl.ONE)
      return a;
    if (a == Scl.TEN)
      return new Scl(b.val.scaleByPowerOfTen(1));
    if (b == Scl.TEN)
      return new Scl(a.val.scaleByPowerOfTen(1));
    // Returns a new scalar representing the result with preserved decimal places
    return new Scl(a.val.multiply(b.val, maxPrecision(a, b)));
  }

  /**
   * Returns the result of a / b   
   * @param a The 1st scalar
   * @param b The 2nd scalar
   * @return The resulting scalar
   */
  public static Scl div(Scl a, Scl b) {
    // Optimized cases:
    if (a == Scl.ZERO)
      return Scl.ZERO;
    if (b == Scl.ZERO) {
      Output.customError("Cannot divide by 0"); 
      return null;
    }
    if (a == Scl.ONE)
      return b;
    if (b == Scl.ONE)
      return a;
    if (a == Scl.TEN)
      return new Scl(b.val.scaleByPowerOfTen(-1));
    if (b == Scl.TEN)
      return new Scl(a.val.scaleByPowerOfTen(-1));
    // Returns a new scalar representing the result with preserved decimal places
    return new Scl(a.val.divide(b.val, maxPrecisionPlus(a, b)));
  }

  /**
   * Returns the result of a ^ b (WILL ONLY HAVE DOUBLE PRECISION)
   * @param a The 1st scalar
   * @param b The 2nd scalar
   * @return The resulting scalar
   */
  public static Scl exp(Scl a, Scl b) {
    // Optimized cases:
    if (a == Scl.ZERO && b != Scl.ZERO)
      return Scl.ZERO;
    if (b == Scl.ZERO && a != Scl.ZERO)
      return Scl.ONE;
    // Returns a new scalar representing the result with preserved decimal places
    return new Scl(Math.pow(a.val.doubleValue(), b.val.doubleValue()), maxPrecisionPlus(a, b).getPrecision());
  }

  public static Bool great_or_equal(Scl a, Scl b) {
    return Bool.bool(a.val.compareTo(b.val) >= 0);
  }

  public static Bool less_or_equal(Scl a, Scl b) {
    return Bool.bool(a.val.compareTo(b.val) <= 0);
  }

  public static Bool greater(Scl a, Scl b) {
    return Bool.bool(a.val.compareTo(b.val) > 0);
  }

  public static Bool lesser(Scl a, Scl b) {
    return Bool.bool(a.val.compareTo(b.val) < 0);
  }

  public static Bool equal(Scl a, Scl b) {
    return Bool.bool(a.val.compareTo(b.val) == 0);
  }

  /**
   * Parses the value as a double and preserves the number of decimal places   
   * @param val The string of the number
   */
  public Scl(String val) {
    this.val = new BigDecimal(val);
  }

  /**
   * Creates a new scalar from a double and precision
   * @param val The double value
   * @param precision The precision
   */
  private Scl(double val, int precision) {
    this.val = new BigDecimal(val, new MathContext(precision));
  }

  /**
   * Creates a scalar from a BigDecimal
   * @param val The BigDecimal
   */
  private Scl(BigDecimal val) {
    this.val = val;
  }

  /**
   * Returns the maximum precision of two scalars (at least the number of non-decimal digits)
   * @param a The first scalar
   * @param b The second scalar
   * @return The maximum precision as a MathContext object
   */
  private static MathContext maxPrecision(Scl a, Scl b) {
    return new MathContext(Math.max(Math.abs(a.val.scale()), Math.abs(b.val.scale())));
  }

  /**
   * Returns the maximum precision of two scalars PLUS two
   * @param a The first scalar
   * @param b The second scalar
   * @return The maximum precision as a MathContext object
   */
  private static MathContext maxPrecisionPlus(Scl a, Scl b) {
    if (Math.abs(a.val.scale()) <= 1 || Math.abs(b.val.scale()) == 1) {
      return new MathContext(maxPrecision(a, b).getPrecision() + 2);
    }
    else {
      return maxPrecision(a, b);
    }
  }

  @Override
  public String toString() {
    return this.val.toPlainString();
  }
}
