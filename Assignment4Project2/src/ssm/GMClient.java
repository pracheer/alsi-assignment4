/**
 * 
 */
package ssm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;

import ssm.Operation.OpCode;
import ssm.messages.Ping;

/**
 * Group Membership Client.
 * 
 * @author prac
 *
 */
public class GMClient implements Runnable {

	private Members members;
	private Member me;
	private Random randomGenerator;

	public GMClient(Members members, Member me) {
		this.members = members;
		this.me = me;
		randomGenerator = new Random();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {
			while (true) {
				int callId = randomGenerator.nextInt();
				Random randomGenerator = new Random();
				DatagramSocket rpcSocket = null;
				rpcSocket = new DatagramSocket();

				// getmembers for simpleDB.
				SimpleDBInterface instance = SimpleDBInterface.getInstance();
				Members dbMembers = instance.getMembers();
				boolean found = false;
				boolean timeout = false;
				if(dbMembers.size()!=0) {
					found = dbMembers.search(me);
					int rand = randomGenerator.nextInt(dbMembers.size());
					Member testMember = dbMembers.get(rand);
					InetAddress address = InetAddress.getByName(testMember.getIp());
					Operation operation = new Operation(callId, OpCode.PING, new Ping());
					byte[] buf = operation.toString().getBytes();
					DatagramPacket packet = new DatagramPacket(buf, Constants.DATAGRAM_SIZE, address, testMember.getPort());
					try {

						rpcSocket.send(packet);

						rpcSocket.setSoTimeout(Constants.TIMEOUT);

						DatagramPacket recvPkt = new DatagramPacket(buf, buf.length);
						rpcSocket.receive(recvPkt);

						timeout = false;

					} catch (SocketTimeoutException e) {
						timeout = true;
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if (timeout) {
						dbMembers.remove(testMember);
						instance.removeMember(testMember.getIp(), testMember.getPort());
					}
					
					members.updateList(dbMembers);
				}
				
				if (!found) {
					instance.addMember(me.getIp(), me.getPort());
				}
				
				int sleepTime = Constants.roundTime/2 + randomGenerator.nextInt(Constants.roundTime);
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}