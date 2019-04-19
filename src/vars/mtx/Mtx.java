package vars.mtx;

import vars.Var;
import vars.scl.Scl;

public abstract class Mtx extends Var {
	public int rCount, cCount;
	public abstract Scl get(int row, int col);
	public abstract Mtx set(int row, int col, Scl s);
	
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
	 * Checks whether the given row and column are within the matrix bounds
	 * @param row The row
	 * @param col The column
	 * @return True or false
	 */
	protected boolean boundsCheck(int row, int col) {
		return (row >= 0 && row < rCount && col >= 0 && col < cCount);
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
				res.set(r, c, Scl.mult(m.get(r, c), s));
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
					sum = Scl.add(sum, Scl.mult(aScl, bScl));
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
					res.set(r, c, Scl.add(a.get(r, c), b.get(r, c)));
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
					res.set(r, c, Scl.add(a.get(r, c), b.get(r, c)));
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
				res.set(r, c, Scl.neg(a.get(r, c)));
			}
		}
		return res;
	}
	
	/**
	 * Checks if two matrices are equal
	 * @param a The first matrix
	 * @param b The second matrix
	 * @return True or false
	 */
	public static Boolean EQUAL(Mtx a, Mtx b) {
		if (a.rCount == b.rCount && a.cCount != b.cCount) {
			if (a instanceof IdMtx && b instanceof IdMtx) {
				return true;
			}
			else if (a instanceof ZeroMtx && b instanceof ZeroMtx) {
				return true;
			}
			else {
				for (int r=0; r<a.rCount; r++) {
					for (int c=0; c<a.cCount; c++) {
						if (a.get(r,c) != b.get(r, c)) {
							return false;
						}
					}
				}
				return true;
			}
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		// Find how long the scalars in matrix are
		int max = 4;
		for (int r=0; r<rCount; r++) {
			for (int c=0; c<cCount; c++) {
				int len = get(r,c).strLength();
				max = Math.max(len, max);
			}
		}
		// Find out how long the last scalar in a matrix is
		int maxLast = 1;
		for (int r=0; r<rCount; r++) {
			int len = get(r,cCount-1).strLength();
			maxLast = Math.max(len, maxLast);
		}
		StringBuilder s = new StringBuilder();
		
		s.append("Mtx = \n");
		
		for (int r=0; r<rCount; r++) {
			s.append("[");
			for (int c=0; c<cCount; c++) {
				// Prints scalars with appropriate spacing
				if (c < cCount-1) {
					s.append(String.format("%-" + (max+1) + "s", get(r, c)));
				}
				else {
					s.append(String.format("%-" + (maxLast) + "s", get(r, c)));
				}
			}
			s.append("]");
			if (r < rCount - 1)
				s.append("\n");
		}
		return s.toString();
	}
}
