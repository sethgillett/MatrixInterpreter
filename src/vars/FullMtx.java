package vars;

public class FullMtx extends Mtx {
	private Scl [][] rows;
	public FullMtx(Scl [][] rows) {
		super(rows.length, rows[0].length);
		this.rows = rows;
	}
	public Scl get(int row, int col) {
		if (row >= 0 && row < rCount && col >= 0 && col < cCount) {
			return rows[row][col];
		}
		return null;
	}
	public boolean set(int row, int col, Scl s) {
		if (row >= 0 && row < rCount && col >= 0 && col < cCount) {
			this.rows[row][col] = s;
			return true;
		}
		return false;
	}
	/**
	 * Returns the identity matrix with provided dimensions
	 * @param rows The # of rows
	 * @param cols The # of cols
	 * @return The identity matrix
	 */
	public static FullMtx fullIdentity(int rows, int cols) {
		Scl [][] idRows = new Scl[rows][cols];
		for (int r=0; r<rows; r++) {
			for (int c=0; c<cols; c++) {
				if (r == c) {
					idRows[r][c] = Scl.ONE;
				}
				else {
					idRows[r][c] = Scl.ZERO;
				}
			}
		}
		return new FullMtx(idRows);
	}
}