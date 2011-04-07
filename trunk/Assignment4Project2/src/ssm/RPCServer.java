package ssm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import ssm.Operation.OpCode;
import ssm.messages.Get;
import ssm.messages.Message;
import ssm.messages.Put;

public class RPCServer implements Runnable {

	DatagramSocket rpcSocket;
	byte[] buffer;
	private HashMap<String, SessionInfo> sessionMap;
	public static String INVALID_VERSION = "Invalid Version found";

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
			e.printStackTrace();
		}

	}

	private Operation computeResponseOperation(byte[] data, int length) {

		Operation operation = Operation.fromString(new String(data));
		Message message = operation.getMessage();

		if(operation.getOpCode() == OpCode.PING) {
			// return the same thing again.
			return operation;
		}

		if(operation.getOpCode() == OpCode.GET) {
			if(message instanceof Get) {
				Get getMsg = (Get)message;
				if(sessionMap.containsKey(getMsg.getSessionId())) {
					SessionInfo sessionInfo = sessionMap.get(getMsg.getSessionId());
					synchronized (sessionInfo) {
						if(sessionInfo.getVersion() == getMsg.getVersion()) {
							Value value = sessionInfo.getValue();
							
							return value.toString().getBytes();
						}
						else {
							return INVALID_VERSION.getBytes();
						}
					}
				}
			}
		}

		if(operation.getOpCode() == OpCode.PUT) {
			if(message instanceof Put) {
				Put putMsg = (Put)message;
				SessionInfo newSessionInfo = new SessionInfo(putMsg.getValue(), putMsg.getVersion());
				if(sessionMap.containsKey(putMsg.getSessionId())) {
					SessionInfo sessionInfo = sessionMap.get(putMsg.getSessionId());
					synchronized(sessionInfo) {
						sessionMap.put(putMsg.getSessionId(), newSessionInfo);
					}
				}
				else {
					sessionMap.put(putMsg.getSessionId(), newSessionInfo);
				}
				
				
			}
		}
		return null;
	
	}
	private byte[] computeResponse(byte[] data, int length) {
		Operation operation = computeResponseOperation(data, length);
		return operation.toString().getBytes();
	}

}
