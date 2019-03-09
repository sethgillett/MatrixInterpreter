package vars;

public abstract class Mtx {
	public int rCount, cCount;
	public abstract Scl get(int row, int col);
	public abstract boolean set(int row, int col, Scl s);
	
	/**
	 * Abstract constructor instantiates without creating an actual array
	 * @param rCount Row count
	 * @param cCount Column count
	 */
	public Mtx(int rCount, int cCount) {
		this.rCount = rCount;
		this.cCount = cCount;
	}
	/**
	 * Returns the identity matrix with specified dimensions
	 * @param rows # of rows
	 * @param cols # of cols
	 * @return Identity matrix
	 */
	public static Mtx identity(int rows, int cols) {
		return new IdMtx(rows, cols);
	}
	/**
	 * Returns the zero matrix with specified dimensions
	 * @param rows # of rows
	 * @param cols # of cols
	 * @return Zero matrix
	 */
	public static Mtx zero(int rows, int cols) {
		return new ZeroMtx(rows, cols);
	}
	/**
	 * Scales a matrix by a scalar
	 * @param s The scalar
	 * @param m The matrix
	 * @return The scaled matrix
	 */
	public static Mtx SCALE(Scl s, Mtx m) {
		Mtx res = new FullMtx(m.rCount, m.cCount);
		for (int r=0; r<m.rCount; r++) {
			for (int c=0; c<m.cCount; c++) {
				res.set(r, c, Scl.MULT(m.get(r, c), s));
			}
		}
		return res;
	}
	/**
	 * Multiplies two matrices and returns the result
	 * @param a Matrix A
	 * @param b Matrix B
	 * @return Result
	 */
	public static Mtx MULT(Mtx a, Mtx b) {
		if (a.cCount != b.rCount)
			return null;
		if (a instanceof IdMtx)
			return b;
		if (b instanceof IdMtx)
			return a;
		if (a instanceof ZeroMtx || b instanceof ZeroMtx)
			return new ZeroMtx(a.rCount, b.cCount);
		
		Mtx res = new FullMtx(a.rCount, b.cCount);
		
		for (int r=0; r<a.rCount; r++) {
			for (int c=0; c<b.cCount; c++) {
				// Each cell is dot product of a's row r and b's col c
				Scl sum = Scl.ZERO;
				for (int s=0; s<a.cCount; s++) {
					Scl aScl = a.get(r, s);
					Scl bScl = b.get(s, c);
					sum = Scl.ADD(sum, Scl.MULT(aScl, bScl));
				}
				res.set(r, c, sum);
			}
		}
		
		return res;
	}
	/**
	 * Adds two matrices together and returns the result
	 * @param a The first matrix
	 * @param b The second matrix
	 * @return The sum
	 */
	public static Mtx ADD(Mtx a, Mtx b) {
		if (a instanceof ZeroMtx) {
			return b;
		}
		if (b instanceof ZeroMtx) {
			return a;
		}
		
		if (a.rCount == b.rCount && a.cCount == b.cCount) {
			Mtx res = new FullMtx(a.rCount, a.cCount);
			for (int r=0; r<a.rCount; r++) {
				for (int c=0; c<a.cCount; c++) {
					res.set(r, c, Scl.ADD(a.get(r, c), b.get(r, c)));
				}
			}
			return res;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Subtracts a-b and returns the result
	 * @param a The first matrix
	 * @param b The second matrix
	 * @return The sum
	 */
	public static Mtx SUB(Mtx a, Mtx b) {
		if (a instanceof ZeroMtx) {
			return b;
		}
		if (b instanceof ZeroMtx) {
			return a;
		}
		
		if (a.rCount == b.rCount && a.cCount == b.cCount) {
			Mtx res = new FullMtx(a.rCount, a.cCount);
			for (int r=0; r<a.rCount; r++) {
				for (int c=0; c<a.cCount; c++) {
					res.set(r, c, Scl.ADD(a.get(r, c), b.get(r, c)));
				}
			}
			return res;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Negates all the scalars in a matrix
	 * @param a The original matrix
	 * @return The negated matrix
	 */
	public static Mtx NEG(Mtx a) {
		if (a instanceof ZeroMtx) {
			return a;
		}
		Mtx res = new FullMtx(a.rCount, a.cCount);
		for (int r=0; r<a.rCount; r++) {
			for (int c=0; c<a.cCount; c++) {
				res.set(r, c, Scl.NEG(a.get(r, c)));
			}
		}
		return res;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int r=0; r<rCount; r++) {
			s.append("[");
			for (int c=0; c<cCount; c++) {
				s.append(get(r, c));
				if (c < cCount - 1)
					s.append("\t");
			}
			s.append("]");
			if (r < rCount - 1)
				s.append("\n");
		}
		return s.toString();
	}
}
