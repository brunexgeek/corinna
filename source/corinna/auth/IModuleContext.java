package corinna.auth;


public interface IModuleContext
{

	public ISubject getSubject();
	
	public ICallbackHandler getCallbackHandler();
	
	//public Map<String,?> getState();
	
	public boolean isAuthenticated();
	
	public boolean isCommited();
	
	public Object getAttibute( String name );
	
	public void setAttribute( String name, Object value );
	
	public void removeAttribute( String name );
	
}
