package corinna.exception;


public class JSPParserException extends GenericException
{

	private int row, col;
	
	public JSPParserException( String message, int row, int col )
	{
		super(message + " [" + row + ":" + col + "]");
		this.row = row;
		this.col = col;
	}

	private static final long serialVersionUID = -3981183161444998148L;

	public int getRow()
	{
		return row;
	}

	public int getColumn()
	{
		return col;
	}

}
