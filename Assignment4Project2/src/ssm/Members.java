package ssm;

import java.net.InetSocketAddress;
import java.util.Vector;

public class Members {
	
	Vector<Member> members;
	
	private static final String LOCATION_SEPARATOR = ";";

	public Members() {
		members = new Vector<Member>();
	}
	
	public Vector<Member> getMembers() {
		//Send data to the server
		return members;
	}
	
	synchronized void changeList(Member m, boolean addMember)
	{
		int  myPos = 0;
		for (Member member : members) {
			if(member.isEqualTo(m))
				break;
			myPos++;
		}
		if(addMember && myPos == members.size())
			members.add(m);
		if(!addMember && myPos < members.size())
			members.remove(myPos);
		//Send data to the server 
	}
	
	public void add(Member m)
	{
		changeList(m, true);
	}
	
	public void remove(Member m)
	{
		changeList(m, false);
	}
	
	public static Members fromString(String locationString) {
		Members members = new Members();
		String[] strings = locationString.split(LOCATION_SEPARATOR);
		for(int l = 0; l < strings.length; l=l+2) {
			String hostname = strings[l];
			int port = Integer.parseInt(strings[l+1]);
			InetSocketAddress socket = new InetSocketAddress(hostname, port);
			Member member = new Member(socket);
			members.add(member);
		}
		return members;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int m = 0; m < size(); m++) {
			Member member = members.get(m);
			InetSocketAddress socket = member.getSocket();
			buffer.append(socket.getHostName()+ LOCATION_SEPARATOR + socket.getPort());
			if(m < (size()-1))
				buffer.append(LOCATION_SEPARATOR);
		}
		return buffer.toString();
	}
	
	public boolean search(Member mem) {
		for (Member member : members) {
			if(member.isEqualTo(mem))
				return true;
		}
		return false;
	}
	
	public int size() {
		return members.size();
	}
	
	public Member get(int i) {
		return members.get(i);
	}
}

