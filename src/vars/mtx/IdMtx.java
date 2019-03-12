package vars.mtx;

import vars.scl.Scl;

public class IdMtx extends Mtx {
	IdMtx(int rows, int cols) {
		super(rows, cols);
	}
	@Override
	public Scl get(int row, int col) {
		if (row == col) {
			return Scl.ONE;
		}
		else {
			return Scl.ZERO;
		}
	}
	@Override
	public boolean set(int row, int col, Scl s) {
		return false;
	}
}

