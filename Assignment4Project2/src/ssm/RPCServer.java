package ssm;

import java.io.IOException;
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

			rpcSocket = new DatagramSocket();
			int port = rpcSocket.getPort();

			while(true) {
				byte[] inBuf = new byte[1000];
				DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
				rpcSocket.receive(recvPkt);
				InetAddress returnAddr = recvPkt.getAddress();
				int returnPort = recvPkt.getPort();
				
				// here inBuf contains the callID and operationCode
			    byte[] outBuf = computeResponse(recvPkt.getData(), recvPkt.getLength());
			    // here outBuf should contain the callID
			    DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length,
			    	returnAddr, returnPort);
			    rpcSocket.send(sendPkt);
			  
//				                        					byte[] buf;
//					Member m = getMemberFromBuffer(buffer);
//					DatagramPacket packet = new DatagramPacket(buf, buf.length, 
//							InetAddress.getByName(m.getIpAddress()), m.getPort());
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private byte[] computeResponse(byte[] data, int length) {
		
	}

}
