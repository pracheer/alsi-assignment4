package ssm.messages;

public class Get implements Message{

	private String sessionId;
	private String version;

	public Get(String sessionId, String version) {
		this.sessionId = sessionId;
		this.version = version;
	}
	
	public static Get fromString(String string) {
		String[] strings = string.split(SEPARATOR);
		return new Get(strings[0], strings[1]);
	}
	
	@Override
	public String toString() {
		return sessionId + SEPARATOR + version;
	}
}
