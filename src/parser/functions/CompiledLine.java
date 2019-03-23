package parser.functions;

import java.util.ArrayList;

import tokens.ReadToken;
import tokens.Tk;

public class CompiledLine {
	private ArrayList<Object> line;
	private int lineIdx;
	public Tk tk;
	
	public CompiledLine(ArrayList<Object> line) {
		this.line = line;
		this.restartLine();
	}
	
	public void restartLine() {
		this.lineIdx = 0;
	}
	
	public boolean nextToken() {
		if (line.get(lineIdx) instanceof ReadToken) {
			this.tk = ((ReadToken) line.get(lineIdx)).token;
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean prevToken() {
		if (lineIdx > 0) {
			lineIdx -= 1;
			return true;
		}
		else {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Object> nextExpr() {
		if (line.get(lineIdx) instanceof ArrayList) {
			return (ArrayList<Object>) line.get(lineIdx);
		}
		else {
			return null;
		}
	}
}
