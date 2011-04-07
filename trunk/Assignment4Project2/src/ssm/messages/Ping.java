package ssm.messages;

public class Ping implements Message{

	public Ping(String msgString) {
		if(msgString.length()!=0)
			System.err.println("PING message length should be zero");
	}

	public static Message fromString(String string) {
		return new Ping("");
	}
	
	@Override
	public String toString() {
		return "";
	}
	
}
