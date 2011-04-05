package ssm;

/**
 * This class contains the value (count & message) associated with a given Session.
 * @author prac
 *
 */
public class Value {
	
	public static String DEFAULT_MSG = "Hello, User!";
	private int count;
	private String msg;
	
	public Value(int count) {
		super();
		this.count = count;
		this.msg = DEFAULT_MSG;
	}
	
	public Value(int count, String msg) {
		super();
		this.count = count;
		this.msg = msg;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
