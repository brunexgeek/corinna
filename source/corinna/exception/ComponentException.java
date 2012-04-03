package corinna.exception;


public class ComponentException extends GenericException
{

	private static final long serialVersionUID = -8872895865742413605L;

	public ComponentException()
	{
		super();
	}

	public ComponentException( String message )
	{
		super(message);
	}

	public ComponentException( String message, Throwable cause )
	{
		super(message, cause);
	}

	public ComponentException( Throwable cause )
	{
		super(cause);
	}
	
}
