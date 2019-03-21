package parser.primary;
import java.io.BufferedReader;
import java.io.IOException;

import parser.CmdReader;
import parser.ControlsReader;
import parser.ExprReader;
import parser.InputReader;
import parser.VarReader;
import tokens.Tk;

/**
 * Primary parser/interpreter for the program
 * @author Seth Gillett
 *
 */
public class Parser extends ParserType {	
	/**
	 * The primary input reader
	 */
	private BufferedReader reader;
	/**
	 * Instantiates the token reader, error printer, and all other classes
	 * @param s The primary scanner
	 */
	public Parser(BufferedReader br) {
		// Initializes token reader, registries, and error printer
		super();
		// Initialize sub parsers
		cmdReader = new CmdReader(this);
		varReader = new VarReader(this);
		exprReader = new ExprReader(this);
		inpReader = new InputReader(this);
		controlsReader = new ControlsReader(this);
		// Connect all the parsers
		cmdReader.connect(this);
		varReader.connect(this);
		exprReader.connect(this);
		inpReader.connect(this);
		controlsReader.connect(this);
		// Set reference to primary reader
		this.reader = br;
	}
	
	/**
	 * Reads in a new line
	 * @param line The new line read in
	 * @return Whether the run was successful
	 */
	public boolean read(String line) {
		tr.readLine(line);
		tr.nextToken();
		// Blank line - does nothing
		if (tr.tk == Tk.EOL) {
			return true;
		}
		// Look for a command
		if (tr.tk == Tk.NULL_CMD) {
			tr.prevToken();
			return cmdReader.nullCmd();
		}
		// Look for a control statement
		else if (tr.tk == Tk.IF || tr.tk == Tk.WHILE || tr.tk == Tk.FOR) {
			switch(tr.tk) {
			case IF:
				return controlsReader.if_stmt();
			case FOR:
				return controlsReader.for_stmt();
			case WHILE:
				return controlsReader.while_stmt();
			default:
				return false;
			}
		}
		// If an expression has a scl or mtx name
		else if (tr.tk == Tk.SCL_NAME || tr.tk == Tk.MTX_NAME) {
			// Record the type for reference
			Tk type = tr.tk;
			// Next token
			tr.nextToken();
			// If name is followed by assignment, send to appropriate assignment
			if (tr.tk == Tk.ASSIGNMENT_OP) {
				tr.nextToken();
				if (type == Tk.SCL_NAME) {
					tr.restartLine();
					return varReader.sclAssign();
				}
				else {
					tr.restartLine();
					return varReader.mtxAssign();
				}
			}
			else if (tr.tk == Tk.EOL) {
				// If there is no next token, print out the value of that scalar
				tr.prevToken();
				return print(tr.tokenStr());
			}
			else {
				ep.expectedError("assignment or arithmetical expression");
				return false;
			}
		}
		// The statement is an expression
		else if (tr.tk == Tk.NOT_OP || tr.tk == Tk.ADD_OP
				|| tr.tk == Tk.SUB_OP || tr.tk == Tk.NUM_LIT
				|| tr.tk == Tk.LPAREN) {
			// If it's an expression print out the value of the expression
			tr.restartLine();
			return print(exprReader.expr(null));
		}
		// Otherwise, print an error
		else {
			ep.expectedError("arithmetic expression, command, or assignment");
			return false;
		}
	}
	
	public String readNewLine() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			ep.internalError("Reading of new line failed");
			return null;
		}
	}
	
}
