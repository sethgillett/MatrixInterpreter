package vars.mtx;

import vars.scl.Scl;

public class ZeroMtx extends Mtx {

  ZeroMtx(int rCount, int cCount) {
    super(rCount, cCount);
  }

  @Override
  public Scl get(int row, int col) {
    return Scl.ZERO;
  }

  @Override
  public Mtx set(int row, int col, Scl s) {
    if (boundsCheck(row, col)) {
      return new FullMtx(rCount, cCount).set(row, col, s);
    } else {
      return null;
    }
  }
}
