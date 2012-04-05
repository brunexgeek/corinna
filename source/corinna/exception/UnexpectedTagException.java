package corinna.exception;

import org.w3c.dom.Element;


public class UnexpectedTagException extends ParseException
{

	private static final long serialVersionUID = 181050241205051493L;

	public UnexpectedTagException( String wrongTag )
	{
		super("Unexpected tag '" + wrongTag + "'");
	}
	
	public UnexpectedTagException( Element wrongElement )
	{
		super("Unexpected tag '" + wrongElement.getLocalName() + "'");
	}
	
	public UnexpectedTagException( String wrongTag, String correctTag )
	{
		super("Unexpected tag '" + wrongTag + "' -- should be '" + correctTag + "'");
	}
	
	public UnexpectedTagException( Element wrongElement, String correctTag )
	{
		super("Unexpected tag '" + wrongElement.getLocalName() + "' -- should be '" + correctTag + "'");
	}
	
}
