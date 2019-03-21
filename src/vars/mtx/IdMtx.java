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
	public Mtx set(int row, int col, Scl s) {
		if (boundsCheck(row,col)) {
			return new FullMtx(rCount, cCount).set(row, col, s);
		}
		else {
			return null;
		}
	}
}

