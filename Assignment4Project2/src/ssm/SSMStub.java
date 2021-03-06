/**
 * 
 */
package ssm;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Random;

import ssm.Operation.OpCode;
import ssm.messages.GeneralMsg;


/**
 * @author prac, @bby
 *
 */
public class SSMStub {

	static int callId = 0;

	Random randomGenerator;

	public SSMStub() {
		randomGenerator = new Random();
	}

	Value get(String sessionId, int version, Members members) {
		int callId = randomGenerator.nextInt();

		try {
			DatagramSocket rpcSocket = new DatagramSocket(); 
			GeneralMsg getMsg = new GeneralMsg(sessionId, version);
			Operation operation = new Operation(callId, OpCode.GET, getMsg);

			byte[] outBuf = operation.toString().getBytes(); 
			for (Member member : members.getMembers()) {
				InetAddress address = InetAddress.getByName(member.getIp());
				DatagramPacket sendPkt = new DatagramPacket(outBuf, 
						Math.min(outBuf.length, Constants.DATAGRAM_SIZE), address, member.getPort());
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
			} while( !replied || operationIn.getCallId() != callId || operationIn.isError());

			if(operationIn!=null && operationIn.getCallId() == callId && !operationIn.isError()) {
				GeneralMsg msg = (GeneralMsg)operationIn.getMessage();
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
		int callId = randomGenerator.nextInt();

		DatagramSocket rpcSocket;
		Operation operation;
		try {
			rpcSocket = new DatagramSocket(); 
			GeneralMsg remMsg = new GeneralMsg(sessionId, version);
			operation = new Operation(callId, OpCode.REMOVE, remMsg);

			byte[] outBuf = operation.toString().getBytes(); 
			for (Member member : members.getMembers()) {
				InetAddress address = InetAddress.getByName(member.getIp());
				DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, address, member.getPort());
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
			} while( !replied || operationIn.getCallId() != callId || operation.isError());

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}

	/**
	 * Send data to W members out of all the members and wait for WQ responses.
	 * Returns the details of servers corresponding to WQ responses.
	 * 
	 * @param sessionId
	 * @param version
	 * @param locationMembers
	 * @param w
	 * @param WQ 
	 * @param sessionInfo 
	 * @return replied: W members who have replied.
	 */
	public Members put(String sessionId, int version, Members members,
			int W, int WQ, Value value) {
		if(W == 0)
			return new Members();
		if(W < WQ || WQ > members.size() || W > members.size()) {
			System.err.println("W="+W + " WQ="+WQ + " members.size()="+members.size());
		}

		HashSet<Integer> setW = new HashSet<Integer>();  
		while(setW.size() < W) {
			int nextInt = randomGenerator.nextInt(members.size());
			setW.add(nextInt);
		}

		int callId = randomGenerator.nextInt();

		try {
			DatagramSocket rpcSocket = new DatagramSocket(); 
			GeneralMsg putMsg = new GeneralMsg(sessionId, version, value);
			Operation operation = new Operation(callId, OpCode.PUT, putMsg);

			byte[] outBuf = operation.toString().getBytes(); 
			for (int i = 0; i < members.size(); i++) {
				if(!setW.contains(i))
					continue;
				Member member = members.get(i);
				InetAddress address = InetAddress.getByName(member.getIp());
				DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, address, member.getPort());
				rpcSocket.send(sendPkt);
			}

			Members replied = new Members();
			byte [] inBuf = new byte[Constants.DATAGRAM_SIZE];
			Operation operationIn = null;
			DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
			do {
				try {
					recvPkt.setLength(inBuf.length);
					rpcSocket.setSoTimeout(Constants.TIMEOUT);
					rpcSocket.receive(recvPkt);
					operationIn = Operation.fromString(new String(recvPkt.getData()));

					if(!operation.isError() && operationIn.getCallId() == callId) {
						String ip = recvPkt.getAddress().getHostAddress();
						Member member = new Member(ip, recvPkt.getPort());
						replied.add(member);
					}
				} catch(SocketTimeoutException e) {
					System.err.println("Not enough servers are up and running. Need at least " + W + 
							" servers to reply \n but no one has replied in last " + Constants.TIMEOUT +".\nSo exiting.");
				} catch(InterruptedIOException iioe) {
					recvPkt = null;
					iioe.printStackTrace();
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			} while(replied.size() < W);

			return replied;

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
