package corinna.exception;


public class ServerInUseException extends Exception
{

	private static final long serialVersionUID = 7870618539406121553L;


	public ServerInUseException( String message )
	{
		super(message);
	}
	
}
