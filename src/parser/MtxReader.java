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
			
			case "inp":
				var = MTXINP();
				break;
				
			case "id":
				var = MTXID();
				break;
				
			default:
				ep.customError("Command %s hasn't been coded yet", tr.tokenStr());
				return;
			}
		}
		else {
			var = MTXEXPR();
		}
		mtxReg.put(mtxName, var);
	}
	
	/**
	 * Function to handle creation of an identity matrix
	 */
	public Mtx MTXID() {
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
				return Mtx.identity(rows, cols);
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
	 * Function to handle entering matrices into terminal
	 * @return The matrix result
	 */
	public Mtx MTXINP() {
		tr.nextToken();
		if (tr.tk == Tk.LPAREN) {
			tr.nextToken();
			if (tr.tk == Tk.RPAREN) {
				return readMtxInput();
			}
			else {
				ep.expectedError(")", tr.tokenStr());
			}
		}
		else {
			ep.expectedError("(", tr.tokenStr());
		}
		return null;
	}
	
	/**
	 * Reads in a matrix via the terminal
	 * @return The matrix read in
	 */
	public Mtx readMtxInput() {
		ArrayList<ArrayList<Scl>> mtx = new ArrayList<>();
		String lineStr;
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
				tr.nextToken();
			}
			
			if (!line.isEmpty()) {
				mtx.add(line);
			}
			
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
	
	public Mtx MTXEXPR() {
		//TODO
		return null;
	}
}
