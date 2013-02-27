package corinna.json.exception;


public enum JSONRPCErrorCode{
	
	PARSE_ERROR(-32700, "Parse Error"),
	
	INVALID_REQUEST(-32600, "Invalid Request"),
	
	METHOD_NOT_FOUND(-32601, "Method not found"),
	
	INVALID_PARAMS(-32602, "Invalid params"),
	
	INTERNAL_ERROR(-32603, "Internal error"),
	
	SERVER_ERROR(-32000, "Generic Server error");
	
	int code;
	
	String message;
	
	private JSONRPCErrorCode( int code, String message )
	{
		this.code = code;
		this.message = (message == null)? "" : message;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public int getCode()
	{
		return code;
	}
	
};