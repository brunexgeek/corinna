package corinna.json.exception;

import corinna.exception.GenericException;


public class JSONRPCException extends GenericException
{

	private static final long serialVersionUID = -6880420379503992817L;
	
	int code;
	
	public JSONRPCException( JSONRPCErrorCode code )
	{
		this(code.getCode(), code.getMessage());
	}
	
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically
	 * incorporated in this exception's detail message.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link #getCause()} method).
	 *            (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent
	 *            or unknown.)
	 */
	public JSONRPCException( int code, String message, Throwable cause )
	{
		super(message, cause);
		this.code = code;
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message of
	 * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and
	 * detail message of <tt>cause</tt>). This constructor is useful for exceptions that are little
	 * more than wrappers for other throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link #getCause()} method).
	 *            (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent
	 *            or unknown.)
	 */
	public JSONRPCException( int code, Throwable cause )
	{
		super(cause);
		this.code = code;
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause is not initialized,
	 * and may subsequently be initialized by a call to {@link #initCause}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later retrieval by the
	 *            {@link #getMessage()} method.
	 */
	public JSONRPCException( int code, String message )
	{
		super(message);
		this.code = code;
	}
	
	public int getCode()
	{
		return code;
	}
	
}
