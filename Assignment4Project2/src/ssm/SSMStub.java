/**
 * 
 */
package ssm;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.Vector;

import ssm.Operation.OpCode;
import ssm.messages.GeneralMsg;
import ssm.messages.Message;


/**
 * @author prac, @bby
 *
 */
public class SSMStub {

	static int callId = 0;
	private Members members;

	Random randomGenerator;

	public SSMStub(Members members) {
		this.members = members;
		randomGenerator = new Random();
	}

	Value get(String sessionId, int version, Members members) {
		int callId = randomGenerator.nextInt();

		DatagramSocket rpcSocket;
		Operation operation;
		try {
			rpcSocket = new DatagramSocket(); 
			GeneralMsg getMsg = new GeneralMsg(sessionId, version);
			operation = new Operation(callId, OpCode.GET, getMsg);

			byte[] outBuf = operation.toString().getBytes(); 
			for (Member member : members.getMembers()) {
				InetSocketAddress socketAddress = new InetSocketAddress(member.getIpAddress(), member.getPort());
				DatagramPacket sendPkt = new DatagramPacket(outBuf, Constants.DATAGRAM_SIZE, socketAddress);
				rpcSocket.send(sendPkt);
			}

			boolean replied = false;
			byte [] inBuf = new byte[Constants.DATAGRAM_SIZE];
			Operation operationIn = null;
			DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
			do {
				try {
					replied = false;
					recvPkt.setLength(inBuf.length);
					rpcSocket.setSoTimeout(Constants.TIMEOUT);
					rpcSocket.receive(recvPkt);
					operationIn = Operation.fromString(new String(recvPkt.getData()));
					replied = true;
				} catch(SocketTimeoutException e) {
					break;
				} catch(InterruptedIOException iioe) {
					recvPkt = null;
					iioe.printStackTrace();
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			} while( !replied || operationIn.getCallId() != callId);

			if(operationIn!=null && operationIn.getCallId() == callId) {
				GeneralMsg msg = (GeneralMsg)operation.getMessage();
				return msg.getValue();
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void remove(String sessionId, int version, Members members) {
		// TODO Auto-generated method stub
		
	}

}
