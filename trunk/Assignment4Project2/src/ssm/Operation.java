package ssm;

import ssm.messages.GeneralMsg;
import ssm.messages.Message;
import ssm.messages.Ping;

public class Operation {

	enum OpCode {
		PING,
		GET,
		PUT
	}
	
	OpCode opCode;
	int callId;
	Message message;
	boolean error;
	String errorMsg;
	
	private static final String OP_SEP = "_";

	public Operation(int callId, OpCode opCode, Message message) {
		this.opCode = opCode;
		this.callId = callId;
		this.message = message;
		this.error = false;
	}
	
	public OpCode getOpCode() {
		return opCode;
	}
	
	public int getCallId() {
		return callId;
	}
	
	public Message getMessage() {
		return message;
	}
	
	public void setMessage(Message message) {
		this.message = message;
	}
	
	public boolean isError() {
		return error;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.error = true;
		this.errorMsg = errorMsg;
	}
	
	/**
	 * to unroll the operation from a string sent by request.
	 * @param opString
	 * @return
	 */
	public static Operation fromString(String opString) {
		String[] strings = opString.split(OP_SEP);
		int callId = Integer.parseInt(strings[0]);
		String msgString = opString.substring((strings[0]+OP_SEP+strings[1]+OP_SEP).length());
		if(strings[1].equalsIgnoreCase(OpCode.PING.toString())) {
			Message message = new Ping(msgString);
			return new Operation(callId, OpCode.PING, message);
		}
		else if (strings[1].equalsIgnoreCase(OpCode.GET.toString())) {
			Message message = GeneralMsg.fromString(msgString);
			return new Operation(callId, OpCode.GET, message);
		}
		else if(strings[1].equalsIgnoreCase(OpCode.PUT.toString())) {
			Message message = GeneralMsg.fromString(msgString);
			return new Operation(callId, OpCode.PUT, message);
		}
		
		return null;
	}
	
	public String toString() {
		return callId + OP_SEP + opCode + OP_SEP + message;
	}
}
