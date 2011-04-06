package ssm;

/**
 * @author prac, @bby
 *
 */

public class Member {
	String ipAddress;
	int port;
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public int getPort() {
		return port;
	}

	public void setSocket(String ip, int inPort) {
		ipAddress = ip;
		port = inPort;	
	}
	
	public boolean isEqualTo(Member m)
	{
		return (m.getIpAddress().equals(ipAddress) &&
		(m.getPort()==port));
	}
}