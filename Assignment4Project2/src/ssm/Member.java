package ssm;

/**
 * @author prac, @bby
 *
 */

public class Member {
	String ipAddress;
	int port;
	
	public Member(String ipAddress, int port) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getIpAddress() {
		return ipAddress;
	}
	
	public int getPort() {
		return port;
	}

	public boolean isEqualTo(Member m)
	{
		return (m.getIpAddress().equals(ipAddress) &&
		(m.getPort()==port));
	}
}