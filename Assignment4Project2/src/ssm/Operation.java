package ssm;

public class Operation {

	enum OpCode {
		PING,
		GET,
		PUT
	}
	
	OpCode opCode;
	int callId;
//	String 
	private static final String OP_SEP = "_";

	public Operation(int callId, OpCode opCode) {
		this.opCode = opCode;
	}
	
	public OpCode getOpCode() {
		return opCode;
	}
	
	public int getCallId() {
		return callId;
	}
	/**
	 * to unroll the operation from a string sent by request.
	 * @param opString
	 * @return
	 */
	public static Operation fromString(String opString) {
		String[] strings = opString.split(OP_SEP);
		int callId = Integer.parseInt(strings[0]);
		if(strings[1].equalsIgnoreCase(OpCode.PING.toString())) {
			return new Operation(callId, OpCode.PING);
		}
		else if (strings[1].equalsIgnoreCase(OpCode.GET.toString())) {
			return new Operation(callId, OpCode.GET);
		}
		else if(strings[1].equalsIgnoreCase(OpCode.PUT.toString())) {
			return new Operation(callId, OpCode.PUT);
		}
		
		return null;
	}
	
	public String toString() {
		return callId + OP_SEP + opCode;
	}
}
