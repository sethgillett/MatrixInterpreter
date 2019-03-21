package vars.mtx;

import vars.scl.Scl;

public class FullMtx extends Mtx {
	private Scl [][] rows;
	public FullMtx(Scl [][] rows) {
		super(rows.length, rows[0].length);
		this.rows = rows;
	}
	/**
	 * Instantiates a new array to represent the matrix
	 * @param rCount # of rows
	 * @param cCount # of cols
	 */
	public FullMtx(int rCount, int cCount) {
		super(rCount, cCount);
		this.rows = new Scl[rCount][cCount];
	}
	/**
	 * Instantiates a new full matrix as a copy of an existing matrix
	 * @param other The other matrix
	 */
	public FullMtx(Mtx other) {
		super(other.rCount, other.cCount);
		this.rows = new Scl[rCount][cCount];
		for (int r=0; r<rCount; r++) {
			for (int c=0; c<cCount; c++) {
				this.set(r, c, other.get(r, c));
			}
		}
	}
	@Override
	public Scl get(int row, int col) {
		if (boundsCheck(row,col)) {
			return rows[row][col];
		}
		return null;
	}
	@Override
	public Mtx set(int row, int col, Scl s) {
		if (boundsCheck(row,col)) {
			this.rows[row][col] = s;
			return this;
		}
		return null;
	}
}