package corinna.auth;

import java.util.Map;


public interface IModuleContext
{

	public Subject getSubject();
	
	public ICallbackHandler getCallbackHandler();
	
	public Map<String,?> getState();
	
	public boolean isAuthenticated();
	
	public boolean isCommited();
	
	public Object getAttibute( String name );
	
	public void setAttribute( String name, Object value );
	
	public void removeAttribute( String name );
	
}
