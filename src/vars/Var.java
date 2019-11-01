package vars;

public abstract class Var {
  /**
   * The null value
   */
  public static Var Null = new NULL();
}

class NULL extends Var {
  @Override
  public String toString() {
    return "Null";
  }
}
