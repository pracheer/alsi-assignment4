package ssm;

import java.util.ArrayList;

public class Members {
	
	ArrayList<Member> groupMembers;
	
	public Members() {
		groupMembers = new ArrayList<Member>();
	}
	
	public ArrayList<Member> getGroupMembers() {
		//Send data to the server
		return groupMembers;
	}
	
	synchronized void changeMemberList(Member m, boolean addMember)
	{
		int  myPos = 0;
		for (Member member : groupMembers) {
			if(member.isEqualTo(m))
				break;
			myPos++;
		}
		if(addMember && myPos == groupMembers.size())
			groupMembers.add(m);
		if(!addMember && myPos < groupMembers.size())
			groupMembers.remove(myPos);
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
}

