package ssm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

import ssm.messages.Message;
import ssm.messages.GeneralMsg;

public class RPCServer implements Runnable {

	DatagramSocket rpcSocket;
	byte[] buffer;
	private HashMap<String, SessionInfo> sessionMap;
	public static String INVALID_VERSION = "Invalid Version found";
	private int port;

	public RPCServer(HashMap<String, SessionInfo> sessionMap) {
		this.sessionMap = sessionMap;
		try {
			rpcSocket = new DatagramSocket();
			port = rpcSocket.getPort();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public int getPort() {
		return port;
	}

	@Override
	public void run() {
		try {
			while(true) {
				byte[] inBuf = new byte[1000];
				DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
				rpcSocket.receive(recvPkt);
				InetAddress returnAddr = recvPkt.getAddress();
				int returnPort = recvPkt.getPort();
				// here inBuf contains the callID and operationCode

				byte[] outBuf = computeResponse(recvPkt.getData());
				// here outBuf should contain the callID
				DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length,
						returnAddr, returnPort);
				rpcSocket.send(sendPkt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Operation computeResponseOperation(Operation operation) {

		Message message = operation.getMessage();

		switch(operation.getOpCode()) {
		case PING:
			// return the same thing again.
			return operation;

		case GET:

			if(message instanceof GeneralMsg) {
				GeneralMsg getMsg = (GeneralMsg)message;
				if(sessionMap.containsKey(getMsg.getSessionId())) {
					SessionInfo sessionInfo = sessionMap.get(getMsg.getSessionId());
					synchronized (sessionInfo) {
						if(sessionInfo.getVersion() == getMsg.getVersion()) {

							// Timeout
							if(sessionInfo.getTimestamp() < System.currentTimeMillis()) {
								operation.setErrorMsg("Data is quite old. So, \"TIMED OUT\"");
								sessionMap.remove(getMsg.getSessionId());
								return operation;
							}

							Value value = sessionInfo.getValue();
							getMsg.setValue(value);
							operation.setMessage(getMsg);
							return operation;
						}
						else {
							operation.setErrorMsg(INVALID_VERSION);
							return operation;
						}
					}
				}
				else {
					operation.setErrorMsg("Session not found");
					return operation;
				}
			}
			break;

		case PUT:
			if(message instanceof GeneralMsg) {
				GeneralMsg putMsg = (GeneralMsg)message;
				SessionInfo newSessionInfo = SessionInfo.create(putMsg.getValue(), putMsg.getVersion());
				if(sessionMap.containsKey(putMsg.getSessionId())) {
					SessionInfo sessionInfo = sessionMap.get(putMsg.getSessionId());
					synchronized(sessionInfo) {
						sessionMap.put(putMsg.getSessionId(), newSessionInfo);
					}
				}
				else {
					sessionMap.put(putMsg.getSessionId(), newSessionInfo);
				}
				putMsg.removeValue();
				operation.setMessage(putMsg);
				return operation;
			}
			break;

		case REMOVE:
			if(message instanceof GeneralMsg) {
				GeneralMsg putMsg = (GeneralMsg)message;
				if(sessionMap.containsKey(putMsg.getSessionId())) {
					SessionInfo sessionInfo = sessionMap.get(putMsg.getSessionId());
					synchronized(sessionInfo) {
						sessionMap.remove(putMsg.getSessionId());
					}
				}
				else {
					operation.setErrorMsg("Session not found");
					return operation;
				}
			}
			break;
		}

		return null;

	}
	private byte[] computeResponse(byte[] data) {
		Operation operation = Operation.fromString(new String(data));
		Operation operationOut = computeResponseOperation(operation);
		return operationOut.toString().getBytes();
	}

}
