package ssm;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

public class RPCServer implements Runnable {

	DatagramSocket rpcSocket;
	byte[] buffer;
	private HashMap<String, SessionInfo> sessionMap;
	
	public RPCServer(HashMap<String, SessionInfo> sessionMap) {
		this.sessionMap = sessionMap;
	}

	@Override
	public void run() {
		try {
			
			rpcSocket = new DatagramSocket(SsmUtil.getInstance().getMySocketInfo().getPort());   //assumes one ip address of the machine
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
		if(buffer.equals(S))
		{
			byte[] buf;
			Member m = getMemberFromBuffer(buffer);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, 
				InetAddress.getByName(m.getIpAddress()), m.getPort());
		}

	}

}
