/**
 * 
 */
package ssm;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * @author prac
 *
 */
public class RPCClient {
	
	static int callId = 0;
	
	Value get(String sessionId, String sessionVersion, Members members) {
		
		try {
			DatagramSocket rpcSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		ArrayList<Member> groupMembers = members.getGroupMembers();
		for (Member member : groupMembers) {
			
		}
		return null;
	}

}
