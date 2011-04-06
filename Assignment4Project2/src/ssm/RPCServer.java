package ssm;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class RPCServer implements Runnable {

	private HashMap<String, SessionInfo> sessionMap;

	public RPCServer(HashMap<String, SessionInfo> sessionMap) {
		this.sessionMap = sessionMap;
	}

	@Override
	public void run() {
		try {
			DatagramSocket rpcSocket = new DatagramSocket();
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

}
