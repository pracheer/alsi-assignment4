package ssm.messages;

import ssm.Value;

public class Put implements Message{

	private String sessionId;
	private String version;
	private Value value;

	public Put(String sessionId, String version, Value value) {
		this.sessionId = sessionId;
		this.version = version;
		this.value = value;
	}

	public static Put fromString(String string) {
		String[] strings = string.split(SEPARATOR);
		return new Put(strings[0], strings[1], Value.fromString(strings[3]));
	}

	@Override
	public String toString() {
		return sessionId + SEPARATOR + version + SEPARATOR + value;
	}
}
