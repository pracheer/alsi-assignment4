package ssm;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Contains Session Information.
 * It increments version number whenever a change is made.
 * For Creating Session Id, a combination of Servers Ip Address and Current timestamp is used.
 * By Default server keeps a particular session information for SESSION_VALIDITY ms. (kept at 1 minute).
 */

public class SessionInfo {
	
	private static final String LOCALHOST = "127.0.0.1";
	private String sessionId;
	private int version;
	private long timestamp;
	private Value value;
	private String location;
	
	public static final long SESSION_VALIDITY = 60*1000; // 1 minute

	private void incrementVersion() {
		version = version + 1;
	}

	public void setValue(Value value) {
		this.value = value;
		incrementVersion();
		timestamp = System.currentTimeMillis() + SESSION_VALIDITY; 
	}
	
	public SessionInfo(Value value) {
		super();
		try {
		InetAddress addr = InetAddress.getLocalHost();
		this.sessionId = addr.getHostAddress() + System.nanoTime();
		this.version = 1;
		timestamp = System.currentTimeMillis() + SESSION_VALIDITY; 
		this.value = value;
		this.location = LOCALHOST;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public String getSessionId() {
		return sessionId;
	}
	public int getVersion() {
		return version;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public Value getValue() {
		return value;
	}

	public String getLocation() {
		return location;
	}

}
