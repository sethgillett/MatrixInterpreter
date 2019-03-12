package vars.scl.complex;

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
