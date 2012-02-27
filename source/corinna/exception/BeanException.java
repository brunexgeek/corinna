package corinna.exception;


public class BeanException extends GenericException
{

	private static final long serialVersionUID = -3456178693913609793L;

	public BeanException() 
	{
		super();
	}

	public BeanException( String message, Throwable cause ) 
	{
		super(message, cause);
	}

	public BeanException( Throwable cause ) 
	{
		super(cause);
	}

	public BeanException( String message ) 
	{
		super(message);
	}
	
}
