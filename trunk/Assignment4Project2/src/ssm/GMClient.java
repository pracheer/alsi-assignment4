/**
 * 
 */
package ssm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Vector;

/**
 * Group Membership Client.
 * 
 * @author prac
 *
 */
public class GMClient implements Runnable {

	private Members members;
	private Member me;

	public GMClient(Members members, Member me) {
		this.members = members;
		this.me = me;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {
			Random randomGenerator = new Random();
			DatagramSocket rpcSocket = null;
			rpcSocket = new DatagramSocket();

			// keep track if dbmembers have been modified.
			boolean modified = false;
			// getmembers for simpleDB.
			Members dbMembers = new Members();
			boolean found = dbMembers.search(me);
			int rand = randomGenerator.nextInt(dbMembers.size());

			boolean timeout = false;
			byte[] buf = new byte[256];
			Member testMember = dbMembers.get(rand);
			InetSocketAddress socketAddress = new InetSocketAddress(testMember.getIpAddress(), testMember.getPort());
			DatagramPacket packet = new DatagramPacket(buf, buf.length, socketAddress);
			try {
				
			rpcSocket.send(packet);
			
			rpcSocket.setSoTimeout(Constants.TIMEOUT);
			
			DatagramPacket recvPkt = new DatagramPacket(buf, buf.length);
			rpcSocket.receive(recvPkt);
			
			timeout = false;
			
			} catch(SocketTimeoutException e) {
				timeout = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(!found) {
				dbMembers.add(me);
				modified = true;
			}
			if(timeout) {
				dbMembers.remove(testMember);
				modified = true;
			}
			
			if(modified) {
				// update dbMembers to simpleDB
			}

		} catch (SocketException e) {
			e.printStackTrace();
		}

	}

}
