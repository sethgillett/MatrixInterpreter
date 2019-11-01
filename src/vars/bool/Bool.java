package vars.bool;

import vars.Var;

public class Bool extends Var {
  private boolean val;

  public static Bool False = new Bool(false);
  public static Bool True = new Bool(true);

  private Bool(boolean val) {
    this.val = val;
  }

  public static Bool bool(boolean val) {
    return (val) ? True : False;
  }

  public static Bool AND(Bool a, Bool b) {
    return (a.val() && b.val()) ? True : False;
  }

  public static Bool OR(Bool a, Bool b) {
    return (a.val() || b.val()) ? True : False;
  }

  public static Bool NOT(Bool a) {
    return (a.val()) ? True : False;
  }

  public boolean val() {
    return this.val;
  }

  @Override
  public String toString() {
    return (this.val) ? "True" : "False";
  }
}
