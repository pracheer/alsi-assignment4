package ssm;

import java.net.UnknownHostException;
import java.util.Random;

/**
 * @author @bby
 *
 */

public class SSMUtil {

	Member myInfo;
	public static final int port = 1000;
	Random randomGenerator = new Random();
	int timeOut = 1000;	//ms
	int gossipTimeGap = 1100; //ms
	Member waitingForMember = null;
	
	
	static SSMUtil instance = new SSMUtil();
	
	SSMUtil(){
		try {
			myInfo = new Member(java.net.InetAddress.getLocalHost().getAddress().toString(), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public Member getMySocketInfo()
	{
		return myInfo;
	}
	
	public static SSMUtil getInstance()
	{
		return instance;
	}
	
	public int getRandomNumberInRange(int start, int end) //start inclusive
	{
		return (randomGenerator.nextInt(end-start) + start);
	}
	
	public int getTimeOut()
	{
		return timeOut;
	}
	
	public int getGossipTimeOut()
	{
		return gossipTimeGap;
	}
	
	synchronized Member internalSetWaitingMember(boolean set, Member m)
	{
		Member oldM = waitingForMember;
		if(set)
			waitingForMember = m;
		return oldM; 
	}
	
	public boolean setWaitingMember(Member m)
	{
		return (internalSetWaitingMember(true, m) == null);
	}
	
	public Member getWaitingMember()
	{
		return internalSetWaitingMember(false, null);
	}
	
	public boolean cleanWaitingMember()
	{
		return (internalSetWaitingMember(true, null) != null);
	}
}
