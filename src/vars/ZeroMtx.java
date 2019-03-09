package vars;

public class ZeroMtx extends Mtx {
	
	ZeroMtx(int rCount, int cCount) {
		super(rCount, cCount);
	}

	@Override
	public Scl get(int row, int col) {
		return Scl.ZERO;
	}

	@Override
	public boolean set(int row, int col, Scl s) {
		return false;
	}
}
