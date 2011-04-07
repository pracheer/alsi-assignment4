package ssm;

import java.util.ArrayList;
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
	
	synchronized void changeMemberList(Member m, boolean addMember)
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
	
	public void addNewMember(Member m)
	{
		changeMemberList(m, true);
	}
	
	public void removeMember(Member m)
	{
		changeMemberList(m, false);
	}
	
	public static Members fromString(String locationString) {
		Members members = new Members();
		String[] strings = locationString.split(LOCATION_SEPARATOR);
		for(int l = 0; l < strings.length; l=l+2) {
			String ipAddress = strings[l];
			int port = Integer.parseInt(strings[l+1]);
			Member member = new Member(ipAddress, port);
			members.addNewMember(member);
		}
		return members;
	}
	
	public boolean search(Member mem) {
		for (Member member : members) {
			if(member.isEqualTo(mem))
				return true;
		}
		return false;
	}
}

