package tokens;

public class ReadToken {
	/**
	 * Index in the line the token was found
	 */
	public int idx;
	/**
	 * The token found
	 */
	public Tk token;
	/**
	 * The string of the token (can be null)
	 */
	private String _tokenStr;
	
	public ReadToken(Tk token) {
		this(-1, token);
	}
	
	public ReadToken(Tk token, String _tokenStr) {
		this(-1, token, _tokenStr);
	}
	
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
		return (token == Tk.ERROR || token == Tk.VAR_NAME ||
				token == Tk.NUM_LIT || token == Tk.FUNC_NAME);
	}
}
