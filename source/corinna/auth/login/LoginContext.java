package corinna.auth.login;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import sun.security.util.ResourcesMgr;
import corinna.auth.ICallbackHandler;
import corinna.auth.ILoginModule;
import corinna.auth.IModuleContext;
import corinna.auth.IModulePipeline;
import corinna.auth.ISubject;
import corinna.auth.ModuleContext;
import corinna.auth.ModulePipeline;
import corinna.auth.Subject;


public class LoginContext implements ILoginContext
{

	private static final String METHOD_LOGIN = "login";

	private static final String METHOD_COMMIT = "commit";

	private static final String METHOD_ABORT = "abort";

	private static final String METHOD_LOGOUT = "logout";

	private ISubject subject = null;

	private Map<String,Object> state = new HashMap<String,Object>();

	private IModulePipeline pipeline;

	private static final Class<?>[] MODULE_PARAMS = { IModuleContext.class };
	
	private ModuleContext moduleContext = null;

	protected LoginContext( String profileName, ICallbackHandler callbackHandler ) throws LoginException
	{
		this(profileName, null, callbackHandler);
	}

	protected LoginContext( String pipelineName, ISubject subject, ICallbackHandler callbackHandler )
		throws LoginException
	{
		this( ModulePipeline.getModulePipeline(pipelineName), subject, callbackHandler );
	}

	protected LoginContext( IModulePipeline pipeline, ISubject subject, ICallbackHandler callbackHandler ) 
		throws LoginException
	{
		if (callbackHandler == null)
			throw new LoginException("The callback handler can not be null");
		if (pipeline == null)
			throw new LoginException("The pipeline object can not be null");
		if (subject == null)
			subject = new Subject();
		
		this.pipeline = pipeline;
		this.moduleContext = new ModuleContext(subject, callbackHandler, state);
	}

	/**
	 * Perform the authentication.
	 * 
	 * <p>
	 * This method invokes the <code>login</code> method for each LoginModule configured for the
	 * <i>name</i> specified to the <code>LoginContext</code> constructor, as determined by the
	 * login <code>Configuration</code>. Each <code>LoginModule</code> then performs its respective
	 * type of authentication (username/password, smart card pin verification, etc.).
	 * 
	 * <p>
	 * This method completes a 2-phase authentication process by calling each configured
	 * LoginModule's <code>commit</code> method if the overall authentication succeeded (the
	 * relevant REQUIRED, REQUISITE, SUFFICIENT, and OPTIONAL LoginModules succeeded), or by calling
	 * each configured LoginModule's <code>abort</code> method if the overall authentication failed.
	 * If authentication succeeded, each successful LoginModule's <code>commit</code> method
	 * associates the relevant Principals and Credentials with the <code>Subject</code>. If
	 * authentication failed, each LoginModule's <code>abort</code> method removes/destroys any
	 * previously stored state.
	 * 
	 * <p>
	 * If the <code>commit</code> phase of the authentication process fails, then the overall
	 * authentication fails and this method invokes the <code>abort</code> method for each
	 * configured <code>LoginModule</code>.
	 * 
	 * <p>
	 * If the <code>abort</code> phase fails for any reason, then this method propagates the
	 * original exception thrown either during the <code>login</code> phase or the
	 * <code>commit</code> phase. In either case, the overall authentication fails.
	 * 
	 * <p>
	 * In the case where multiple LoginModules fail, this method propagates the exception raised by
	 * the first <code>LoginModule</code> which failed.
	 * 
	 * <p>
	 * Note that if this method enters the <code>abort</code> phase (either the <code>login</code>
	 * or <code>commit</code> phase failed), this method invokes all LoginModules configured for the
	 * application regardless of their respective <code>Configuration</code> flag parameters.
	 * Essentially this means that <code>Requisite</code> and <code>Sufficient</code> semantics are
	 * ignored during the <code>abort</code> phase. This guarantees that proper cleanup and state
	 * restoration can take place.
	 * 
	 * <p>
	 * 
	 * @exception LoginException
	 *                if the authentication fails.
	 */
	@Override
	public void login() throws LoginException
	{
		moduleContext.setAuthenticated(false);

		try
		{
			invoke(METHOD_LOGIN);
			invoke(METHOD_COMMIT);
			moduleContext.setAuthenticated(true);
		} catch (LoginException le)
		{
			try
			{
				invoke(METHOD_ABORT);
			} catch (LoginException le2)
			{
				throw le;
			}
			throw le;
		}
	}

	/**
	 * Logout the <code>Subject</code>.
	 * 
	 * <p>
	 * This method invokes the <code>logout</code> method for each <code>LoginModule</code>
	 * configured for this <code>LoginContext</code>. Each <code>LoginModule</code> performs its
	 * respective logout procedure which may include removing/destroying <code>Principal</code> and
	 * <code>Credential</code> information from the <code>Subject</code> and state cleanup.
	 * 
	 * <p>
	 * Note that this method invokes all LoginModules configured for the application regardless of
	 * their respective <code>Configuration</code> flag parameters. Essentially this means that
	 * <code>Requisite</code> and <code>Sufficient</code> semantics are ignored for this method.
	 * This guarantees that proper cleanup and state restoration can take place.
	 * 
	 * <p>
	 * 
	 * @exception LoginException
	 *                if the logout fails.
	 */
	@Override
	public void logout() throws LoginException
	{
		if (subject == null)
		{
			throw new LoginException(
				ResourcesMgr.getString("null subject - logout called before login"));
		}

		invoke(METHOD_LOGOUT);
	}

	/**
	 * Return the authenticated Subject.
	 * 
	 * <p>
	 * 
	 * @return the authenticated Subject. If the caller specified a Subject to this LoginContext's
	 *         constructor, this method returns the caller-specified Subject. If a Subject was not
	 *         specified and authentication succeeds, this method returns the Subject instantiated
	 *         and used for authentication by this LoginContext. If a Subject was not specified, and
	 *         authentication fails or has not been attempted, this method returns null.
	 */
	@Override
	public ISubject getSubject()
	{
		return subject;
	}

	private void invoke( String methodName ) throws LoginException
	{
		for (ILoginModule module : pipeline.getModules())
		{
			try
			{
				Method method = module.getClass().getMethod(methodName, MODULE_PARAMS);
				Boolean status = (Boolean) method.invoke(module, moduleContext);
				if (status) return;
			} catch (NoSuchMethodException nsme)
			{
				throw new LoginException("Unable to instantiate the LoginModule, because "
						+ "it does not provide a no-argument constructor");
			} catch (IllegalAccessException iae)
			{
				throw new LoginException("Unable to instantiate the LoginModule, because "
					+ "security restrictions");
			} catch (InvocationTargetException ite)
			{
				// supress all errors
			}
		}

		
	}

	@Override
	public void addListener( ILoginContextListener listener )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener( ILoginContextListener listener )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener( String listenerName )
	{
		// TODO Auto-generated method stub
		
	}


}
