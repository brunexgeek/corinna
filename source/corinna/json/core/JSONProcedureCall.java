package corinna.json.core;

import corinna.rpc.ParameterList;
import corinna.rpc.ProcedureCall;


public class JSONProcedureCall extends ProcedureCall
{

	private Object id = null;
	
	public JSONProcedureCall( String prototype )
	{
		super(prototype);
	}

	public JSONProcedureCall( String prototype, ParameterList params )
	{
		super(prototype, params);
	}
	
	public void setId( Object id )
	{
		this.id = id;
	}
	
	public Object getId()
	{
		return id;
	}

}
