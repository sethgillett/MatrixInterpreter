package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import parser.primary.ParserType;
import tokens.Tk;
import vars.mtx.FullMtx;
import vars.mtx.Mtx;
import vars.scl.Scl;

/**
 * Reads parameter input and matrix input from the terminal
 * @author seth
 *
 */
public class InputReader extends ParserType {
	/**
	 * The primary input reader
	 */
	BufferedReader reader;

	public InputReader(ParserType primary, BufferedReader reader) {
		super(primary);
		this.reader = reader;
	}
	
	/**
	 * Reads in a <i>postitive</i> parameter and returns its value as an int
	 * @return The int value of the parameter <i>or -1</i>
	 */
	public Integer readPositiveIntParam() {
		tr.nextToken();
		if (tr.tk == Tk.NUM_LIT || tr.tk == Tk.SCL_NAME) {
			Scl rowCount;
			if (tr.tk == Tk.NUM_LIT) {
				rowCount = new Scl(tr.tokenStr());
			}
			else {
				rowCount = getScl(tr.tokenStr());
			}
			
			if (rowCount.isInt() && rowCount.valueAsInt() >= 0) {
				return rowCount.valueAsInt();
			}
			else {
				ep.expectedError("postive integer");
				return null;
			}
		}
		else {
			ep.expectedError("number or scalar");
			return null;
		}
	}
	
	/**
	 * Reads in a matrix as a parameter
	 * @return The matrix read or null and an error
	 */
	public Mtx readMtxParam() {
		tr.nextToken();
		if (tr.tk == Tk.MTX_NAME) {
			String name = tr.tokenStr();
			Mtx param = getMtx(name);
			if (param == null)
				return null;
			else
				return param;
		}
		else {
			ep.expectedError(Tk.MTX_NAME);
			return null;
		}
	}
	
	/**
	 * Reads in a scalar as a parameter
	 * @return The scalar read or null and an error
	 */
	public Scl readSclParam() {
		tr.nextToken();
		if (tr.tk == Tk.SCL_NAME) {
			String name = tr.tokenStr();
			Scl param = getScl(name);
			if (param == null)
				return null;
			else
				return param;
		}
		else {
			ep.expectedError(Tk.SCL_NAME);
			return null;
		}
	}
	
	/**
	 * Reads and returns the next parameter
	 * @return The next parameter in string form
	 */
	public String readStrParam() {
		tr.nextToken();
		return tr.tokenStr();
	}
	
	/**
	 * Reads a single parameter
	 * @param reader The funtion to read input parameters
	 * @return The parameter or null
	 */
	public <Term> Term readParam(Callable<Term> reader) {
		Term[] terms = readParams(1, reader);
		if (terms == null) {
			return null;
		}
		else {
			return terms[0];
		}
	}
	
	/**
	 * Reads multiple positive int parameters
	 * @param count The number of parameters
	 * @param reader The function to read input parameters
	 * @return Array with all parameters
	 */
	public <Term> Term[] readParams(int count, Callable<Term> reader) {
		@SuppressWarnings("unchecked")
		Term[] params = (Term[]) new Object[count];
		tr.nextToken();
		if (tr.tk == Tk.LPAREN) {
			for (int i=0; i<count; i++) {
				Term param;
				try {
					param = reader.call();
				}
				catch (Exception e) {
					ep.internalError("Cast failed when reading %d input parameters", count);
					return null;
				}
				if (param == null) {
					return null;
				}
				params[i] = param;
				tr.nextToken();
				if (tr.tk == Tk.COMMA) {
					continue;
				}
				else if (tr.tk == Tk.RPAREN) {
					return params;
				}
				else if (tr.tk == Tk.ERROR) {
					return null;
				}
				else {
					ep.expectedError(Tk.COMMA);
				}
			}
		}
		else {
			ep.expectedError(Tk.LPAREN);
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
					ep.expectedError(Tk.SCL_NAME, Tk.NUM_LIT);
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
	
	/**
	 * Scans a new line with the primary scanner
	 * @return The new line
	 */
	public String scanNewLine() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
