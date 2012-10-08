package corinna.json.bindlet;

import javax.bindlet.exception.BindletException;

import corinna.rpc.IProcedureCall;


public class DefaultJSONBindlet extends JSONBindlet
{

	private static final long serialVersionUID = -7776689284640470657L;

	public DefaultJSONBindlet() throws BindletException
	{
		super();
	}

	@Override
	protected Object doCall( IProcedureCall request ) throws BindletException
	{
		return 0;
	}

}
