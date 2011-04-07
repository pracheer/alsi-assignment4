package ssm.messages;

public class Get implements Message{

	private String sessionId;
	private int version;
	
	public String getSessionId() {
		return sessionId;
	}
	public int getVersion() {
		return version;
	}
	
	public Get(String sessionId, int version) {
		this.sessionId = sessionId;
		this.version = version;
	}
	
	public static Get fromString(String string) {
		String[] strings = string.split(SEPARATOR);
		return new Get(strings[0], Integer.parseInt(strings[1]));
	}
	
	@Override
	public String toString() {
		return sessionId + SEPARATOR + version;
	}
}
