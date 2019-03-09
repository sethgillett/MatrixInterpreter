package parser;

import java.util.ArrayList;

import tokens.Tk;
import vars.FullMtx;
import vars.Mtx;
import vars.Scl;

public class MtxReader extends ParserType {

	public MtxReader(Parser primary) {
		super(primary);
	}
	
	public void MTXASSIGN() {
		// A
		tr.nextToken();
		String mtxName = tr.tokenStr();
		// =
		tr.nextToken();
		// cmd?
		tr.nextToken();
		Mtx var;
		if (tr.tk == Tk.CMD) {
			switch(tr.tokenStr()) {
				
			case "id":
				int[] sizeId = readMtxSizeParams();
				var = Mtx.identity(sizeId[0], sizeId[1]);
				break;
			
			case "zero":
				int[] sizeZ = readMtxSizeParams();
				var = Mtx.zero(sizeZ[0], sizeZ[1]);
				break;
				
			default:
				ep.customError("Command %s hasn't been coded yet", tr.tokenStr());
				return;
			}
		}
		else if (tr.tk == Tk.EOL) {
			var = readMtxInputTerminal();
		}
		else {
			var = this.exprReader.<Mtx>EXPR();
		}
		mtxReg.put(mtxName, var);
	}
	
	/**
	 * Reads in two parameters for size from a function call
	 * @return [rows, cols]
	 */
	public int[] readMtxSizeParams() {
		tr.nextToken();
		if (tr.tk == Tk.LPAREN) {
			tr.nextToken();
			int rows = this.readPositiveIntParam();
			if (rows == -1)
				return null;
			tr.nextToken();
			if (tr.tk == Tk.COMMA) {
				tr.nextToken();
				int cols = this.readPositiveIntParam();
				if (cols == -1)
					return null;
				return new int[] {rows, cols};
			}
			else {
				ep.expectedError(",");
			}
		}
		else {
			ep.expectedError("(");
		}
		return null;
	}
	
	/**
	 * Reads in a matrix via the terminal
	 * @return The matrix read in
	 */
	public Mtx readMtxInputTerminal() {
		ArrayList<ArrayList<Scl>> mtx = new ArrayList<>();
		String lineStr;
		Integer lineLen = null;
		do {
			System.out.print("[");
			lineStr = scanNewLine();
			tr.readLine(lineStr);
			ArrayList<Scl> line = new ArrayList<>();
			while (true) {
				tr.nextToken();
				if (tr.tk == Tk.EOL) {
					break;
				}
				else if (tr.tk == Tk.NUM_LIT) {
					line.add(new Scl(tr.tokenStr()));
				}
				else if (tr.tk == Tk.SCL_NAME) {
					Scl s = getScl(tr.tokenStr());
					if (s == null) {
						// No error message needed since getScl will already print one
						return null;
					}
					else {
						line.add(s);
					}
				}
				else {
					ep.expectedError("scalar", tr.tokenStr());
					return null;
				}
			}
			
			if (!line.isEmpty()) {
				if (lineLen != null && line.size() != lineLen) {
					int dif = line.size() - lineLen;
					// Matrix row has [number] too [few | many] parameters
					ep.customError("Matrix row has %d too %s parameters", Math.abs(dif), dif<0? "few":"many");
					return null;
				}
				else {
					mtx.add(line);
				}
			}
			
			if (lineLen == null)
				lineLen = line.size();
			
		} while (!lineStr.matches("^(\\s*)$"));
		
		
		if (mtx.isEmpty()) {
			return null;
		}
		
		Scl [][] mtxArray = new Scl[mtx.size()][mtx.get(0).size()];
		
		int r=0;
		for (ArrayList<Scl> row : mtx) {
			int c=0;
			for (Scl cell : row) {
				try {
					mtxArray[r][c] = cell;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					ep.customError("Matrix is not rectangular");
					return null;
				}
				c++;
			}
			r++;
		}
		
		return new FullMtx(mtxArray);
	}
	
}
