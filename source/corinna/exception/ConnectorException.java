package corinna.exception;


public class ConnectorException extends GenericException
{

	private static final long serialVersionUID = -8632554634198210186L;

	public ConnectorException() 
	{
		super();
	}

	public ConnectorException( String message, Throwable cause ) 
	{
		super(message, cause);
	}

	public ConnectorException( Throwable cause ) 
	{
		super(cause);
	}

	public ConnectorException( String message ) 
	{
		super(message);
	}
	
}
