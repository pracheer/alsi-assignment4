/**
 * 
 */
package ssm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Group Membership Client.
 * 
 * @author prac
 *
 */
public class GMClient implements Runnable {

	private Members members;

	public GMClient(Members members) {
		this.members = members;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		DatagramSocket rpcSocket = null;
		try {
			rpcSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Member myInfo = SSMUtil.getInstance().getMySocketInfo();
		int  myPos = 0;
		Vector<Member> groupMembers = members.getMembers();
		for (Member member : groupMembers) {
			if(member.isEqualTo(myInfo))
				break;
			myPos++;
		}
		int size = groupMembers.size();
		if(myPos == groupMembers.size())
		{
			groupMembers.add(myInfo);
			members.add(myInfo);
			size++;
		}
		int rand;
		do{
			rand = SSMUtil.getInstance().getRandomNumberInRange(0, size);
		}while(rand != myPos);
		
		byte[] buf = new byte[256];
		InetAddress address = null;
		try {
			address = InetAddress.getByName(groupMembers.get(rand).getIpAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length, 
		                                           address, groupMembers.get(rand).getPort());
		try {
			rpcSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SSMUtil.getInstance().setWaitingMember(groupMembers.get(rand));
		try {
			Thread.sleep(SSMUtil.getInstance().getTimeOut());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(SSMUtil.getInstance().getWaitingMember()!=null)
		{
			members.remove(groupMembers.get(rand));
			SSMUtil.getInstance().cleanWaitingMember();
		}
	}

}
