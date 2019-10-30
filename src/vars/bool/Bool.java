package vars.bool;

import vars.Var;

public class Bool extends Var {
  private boolean val;

  public static Bool False = new Bool(false);
  public static Bool True = new Bool(true);

  public Bool(boolean val) {
    this.val = val;
  }

  public boolean val() {
    return this.val;
  }

  @Override
  public String toString() {
    return this.val ? "True" : "False";
  }
}
