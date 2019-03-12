package vars.scl.complex;
/**
 * A single abstract variable
 * @author seth
 *
 */
public class Symbol {
	private String repr;
	public Symbol(String repr) {
		this.repr = repr;
	}
	@Override
	public String toString() {
		return repr;
	}
}
