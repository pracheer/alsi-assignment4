package ssm;

public class Operation {

	enum OpCode {
		PING,
		GET,
		PUT
	}
	
	OpCode opCode;
	
	private static final String OP_SEP = "_";

	public Operation(OpCode opCode) {
		this.opCode = opCode;
	}
	
	/**
	 * to unroll the operation from a string sent by request.
	 * @param opString
	 * @return
	 */
	public static Operation fromString(String opString) {
		String[] strings = opString.split(OP_SEP);
		if(strings[0].equalsIgnoreCase(OpCode.PING.toString())) {
			return new Operation(OpCode.PING);
		}
		else if (strings[0].equalsIgnoreCase(OpCode.GET.toString())) {
			return new Operation(OpCode.GET);
		}
		else if(strings[0].equalsIgnoreCase(OpCode.PUT.toString())) {
			return new Operation(OpCode.PUT);
		}
		
		return null;
	}
}
