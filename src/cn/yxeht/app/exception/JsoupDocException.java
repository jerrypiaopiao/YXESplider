package cn.yxeht.app.exception;

public class JsoupDocException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5410325193831191325L;

	public JsoupDocException(String message) {
		super(message);
	}
	
	public JsoupDocException() {
		super("JsoupDocException: create Jsoup Document failed...");
	}

}
