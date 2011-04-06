package ssm;

import java.net.DatagramSocket;
import java.net.SocketException;

public class RPCServer implements Runnable {

	@Override
	public void run() {
		try {
			DatagramSocket rpcSocket = new DatagramSocket();
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

}
