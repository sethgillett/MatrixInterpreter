package tokens;

public class ReadToken {
	public int idx;
	public Tk token;
	private String _tokenStr;
	
	public ReadToken(int idx, Tk token) {
		this.idx = idx;
		this.token = token;
		this._tokenStr = null;
	}
	
	public ReadToken(int idx, Tk token, String _tokenStr) {
		this.idx = idx;
		this.token = token;
		if (tokenStrNeeded(token)) {
			this._tokenStr = _tokenStr;
		}
		else {
			this._tokenStr = null;
		}
	}
	
	public String tokenStr() {
		if (this._tokenStr == null) {
			return token.toString();
		}
		else {
			return this._tokenStr;
		}
	}
	
	public static boolean tokenStrNeeded(Tk token) {
		return (token == Tk.ERROR || token == Tk.MTX_NAME ||
				token == Tk.SCL_NAME || token == Tk.NUM_LIT ||
				token == Tk.NULL_CMD || token == Tk.VAR_CMD);
	}
}
