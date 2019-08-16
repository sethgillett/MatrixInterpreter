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

  public VarContainer() {
    vars = new HashMap<>();
  }

  public boolean printVar(String name) {
    if (vars.containsKey(name))
      Output.println(vars.get(name));
    return vars.containsKey(name);
  }

  public Var getLocalVar(String name) {
    return vars.get(name);
  }
  
  public void setLocalVar(String name, Var val) {
    vars.put(name, val);
  }

  public boolean hasLocalVar(String name) {
    return vars.containsKey(name);
  }
}