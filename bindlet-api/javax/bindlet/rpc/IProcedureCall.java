package javax.bindlet.rpc;


public interface IProcedureCall
{

	public abstract String getMethodPrototype();

	public abstract Object getParameter( String name );

	public Object getParameter( int index );
	
	public String getParameterName( int index );
	
	public Object[] getParameterValues();
	
	public String[] getParameterNames();
	
}
