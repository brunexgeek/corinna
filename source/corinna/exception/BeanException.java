package corinna.exception;


public class BeanException extends GenericException
{


	/**
	 * Identificador único para serialização.
	 */
	private static final long serialVersionUID = -3456178693913609793L;
	
	/**
	 * Código numérico que representa a exceção.
	 */
	private static final int ERROR_CODE = 0x32;

	
	/**
	 * Constroi uma exceção sem uma descrição.
	 */
	public BeanException() 
	{
		super();
	}
	
	/**
	 * Constroi uma nova exceção especificando a descricao e a referência da causa.
	 * 
	 * @param message Descrição da exceção.
	 * @param cause Referência ao objeto <code>Throwable</code> que causou a exceção.
	 */
	public BeanException( String message, Throwable cause ) 
	{
		super(message, cause);
	}
	
	/**
	 * Constroi uma nova exceção especificando a referência da causa. A descrição da exceção recém 
	 * criada será a mesma da causa especificada. 
	 * 
	 * @param cause Referência ao objeto <code>Throwable</code> que causou a exceção.
	 */
	public BeanException( Throwable cause ) 
	{
		super(cause);
	}

	/**
	 * Constroi uma nova exceção especificando a descrição.
	 * 
	 * @param message Descrição da exceção.
	 */
	public BeanException( String message ) 
	{
		super(message);
	}

	@Override
	public int getErrorCode() 
	{
		return ERROR_CODE;
	}
	
}
