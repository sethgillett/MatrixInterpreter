package tokens;

public class ReadToken {
	int idx;
	Tk token;
	String tokenStr;
	
	public ReadToken(int idx, Tk token, String tokenStr) {
		this.idx = idx;
		this.token = token;
		this.tokenStr = tokenStr;
	}
}
