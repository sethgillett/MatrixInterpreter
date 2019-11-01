package parser;

import java.util.HashMap;
import java.util.Map;

import io.Output;
import vars.Var;

/**
 * @author Seth Gillett
 * A substitute for a function and scoped variables
 */
public class VarContainer {
  private Map<String, Var> vars;
  private VarContainer parent;

  public VarContainer(VarContainer parent) {
    this.vars = new HashMap<>();
    this.parent = parent;
  }

  public boolean printVar(String name) {
    if (hasLocalVar(name)) {
      Output.println(getLocalVar(name));
      return true;
    }
    else {
      Output.customError("Var %s cannot be printed because it doesn't exist", name);
      return false;
    }
  }

  public Var getLocalVar(String name) {
    if (vars.containsKey(name)) {
      return vars.get(name);
    }
    else {
      if (this.parent != null) {
        return parent.getLocalVar(name);
      }
      else {
        return null;
      }
    }
  }
  
  public void setLocalVar(String name, Var val) {
    vars.put(name, val);
  }

  public boolean hasLocalVar(String name) {
    return getLocalVar(name) != null;
  }

  public VarContainer getParent() {
    return this.parent;
  }
}