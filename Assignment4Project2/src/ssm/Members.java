package ssm;

import java.util.ArrayList;

public class Members {
	
	ArrayList<Member> groupMembers;
	
	public Members() {
		groupMembers = new ArrayList<Member>();
	}
	
	public ArrayList<Member> getGroupMembers() {
		return groupMembers;
	}
}

class Member {
	String ipAddress;
	String port;
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public String getPort() {
		return port;
	}
}