package vars;

public abstract class Var {
  /**
   * The user chosen name of the variable
   */
  public String name;
  /**
   * The null value
   */
  public static Var Null = new NULL();
  /**
   * Assign a user chosen name to this variable
   * @param name The name to assign
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * Returns this var with its name cleared
   * @return This
   */
  public Var clearName() {
    this.name = null;
    return this;
  }
}

class NULL extends Var {
  @Override
  public String toString() {
    return "Null";
  }
}
