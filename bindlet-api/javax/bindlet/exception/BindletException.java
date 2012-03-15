package javax.bindlet.exception;


public class BindletException extends Exception
{

	private static final long serialVersionUID = 933290522610959630L;

	public BindletException()
	{
		super();
	}

	public BindletException( String message )
	{
		super(message);
	}

	public BindletException( String message, Throwable rootCause )
	{
		super(message, rootCause);
	}

	public BindletException( Throwable rootCause )
	{
		super(rootCause);
	}
	
}
