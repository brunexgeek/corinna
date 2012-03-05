package corinna.auth;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;


public class ModuleContext implements IModuleContext
{

	private Subject subject;
	
	private CallbackHandler callbackHandler;
	
	private Map<String, Object> state;

	private boolean isAuthenticated = false;

	private boolean isCommited = false;

	public ModuleContext( Subject subject, CallbackHandler callbackHandler, Map<String,Object> state )
	{
		if (subject == null)
			throw new IllegalArgumentException("The subject can not be null");
		if (callbackHandler == null)
			throw new IllegalArgumentException("The callback handler can not be null");
		
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.state = state;
	}
	
	@Override
	public Subject getSubject()
	{
		return subject;
	}

	@Override
	public CallbackHandler getCallbackHandler()
	{
		return callbackHandler;
	}

	@Override
	public Map<String, ?> getState()
	{
		return state;
	}

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
	
}
