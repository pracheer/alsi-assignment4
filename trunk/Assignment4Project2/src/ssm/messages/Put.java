package ssm.messages;

import ssm.Value;

public class Put implements Message{

	private String sessionId;
	private int version;
	private Value value;

	public Put(String sessionId, int version, Value value) {
		this.sessionId = sessionId;
		this.version = version;
		this.value = value;
	}

	public static Put fromString(String string) {
		String[] strings = string.split(SEPARATOR);
		return new Put(strings[0], Integer.parseInt(strings[1]), Value.fromString(strings[3]));
	}

	@Override
	public String toString() {
		return sessionId + SEPARATOR + version + SEPARATOR + value;
	}

	public String getSessionId() {
		return sessionId;
	}

	public int getVersion() {
		return version;
	}

	public Value getValue() {
		return value;
	}
	
	
}
