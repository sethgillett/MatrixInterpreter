package vars;

public abstract class Mtx {
	public int rCount, cCount;
	public abstract Scl get(int row, int col);
	public abstract boolean set(int row, int col, Scl s);
	
	public Mtx(int rCount, int cCount) {
		this.rCount = rCount;
		this.cCount = cCount;
	}
	
	public static Mtx identity(int rows, int cols) {
		return new IdMtx(rows, cols);
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
