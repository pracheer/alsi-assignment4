package ssm;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author prac, @bby
 *
 */

public class Member {
private InetSocketAddress socket;

//	String hostName;
//	int port;
	
	
//	public Member(String hostName, int port) {
//		super();
//		this.hostName = hostName;
//		this.port = port;
//	}

	public Member(InetSocketAddress socket) {
		this.socket = socket;
	}

//	public String gethostName() {
//		return hostName;
//	}
//	
//	public int getPort() {
//		return port;
//	}

	public boolean isEqualTo(Member m)
	{
		return m.getSocket().equals(socket);
//		return (m.gethostName().equals(hostName) &&
//		(m.getPort()==port));
	}
	
	public InetSocketAddress getSocket() {
		return socket;
	}
}