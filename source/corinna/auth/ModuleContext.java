package corinna.auth;

import java.util.HashMap;
import java.util.Map;


public class ModuleContext implements IModuleContext
{

	private ISubject subject;
	
	private ICallbackHandler callbackHandler;
	
	//private Map<String, Object> state;
	
	private Map<String, Object> attributes;

	private boolean isAuthenticated = false;

	private boolean isCommited = false;

	public ModuleContext( ISubject subject, ICallbackHandler callbackHandler, Map<String,Object> state )
	{
		if (subject == null)
			throw new IllegalArgumentException("The subject can not be null");
		if (callbackHandler == null)
			throw new IllegalArgumentException("The callback handler can not be null");
		
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		//this.state = state;
		this.attributes = new HashMap<String, Object>();
	}
	
	@Override
	public ISubject getSubject()
	{
		return subject;
	}

	@Override
	public ICallbackHandler getCallbackHandler()
	{
		return callbackHandler;
	}

	/*@Override
	public Map<String, ?> getState()
	{
		return state;
	}*/

	@Override
	public boolean isAuthenticated()
	{
		return isAuthenticated;
	}

	public void setAuthenticated( boolean value )
	{
		isAuthenticated = value;
	}
	
	@Override
	public boolean isCommited()
	{
		return isCommited ;
	}

	public void setCommited( boolean value )
	{
		isCommited = value;
	}

	@Override
	public Object getAttibute( String name )
	{
		return attributes.get(name);
	}

	@Override
	public void setAttribute( String name, Object value )
	{
		attributes.put(name, value);
	}

	@Override
	public void removeAttribute( String name )
	{
		attributes.remove(name);
	}
	
}
