package tokens;

public class ReadToken {
  /**
   * The token found
   */
  public Tk token;
  /**
   * The string of the token (can be null)
   */
  private String tokenStr;

  public ReadToken(Tk token) {
    this.token = token;
  }

  public ReadToken(Tk token, String tokenStr) {
    this(token);
    // Tokens with custom naming
    if (token == Tk.ERROR || token == Tk.VAR_NAME || token == Tk.NUM_LIT) {
      this.tokenStr = tokenStr;
    }
  }

  public String tokenStr() {
    if (this.tokenStr == null) {
      return token.toString();
    } else {
      return this.tokenStr;
    }
  }
}
