package corinna.auth.login;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;

import sun.security.util.PendingException;
import sun.security.util.ResourcesMgr;
import corinna.auth.ILoginModule;
import corinna.auth.IModuleContext;
import corinna.auth.IProfileEntry.ProfileEntryFlags;
import corinna.auth.ISubject;
import corinna.auth.ModuleContext;
import corinna.auth.Profile;
import corinna.auth.ProfileEntry;


public class LoginContext implements ILoginContext
{

	private static final String LOGIN_METHOD = "login";

	private static final String COMMIT_METHOD = "commit";

	private static final String ABORT_METHOD = "abort";

	private static final String LOGOUT_METHOD = "logout";

	private ISubject subject = null;

	private boolean subjectProvided = false;

	private boolean loginSucceeded = false;

	private CallbackHandler callbackHandler;

	private Map<String,Object> state = new HashMap<String,Object>();

	private Configuration config;

	private boolean configProvided = false;

	private AccessControlContext creatorAcc = null;

	private ProfileEntry[] moduleStack;
	
	private ILoginModule[] moduleInstances;

	private ClassLoader contextClassLoader = null;

	private static final Class<?>[] PARAMS = {};
	
	private static final Object[] CTOR_ARGS = {};

	// state saved in the event a user-specified asynchronous exception
	// was specified and thrown

	private int moduleIndex = 0;

	private LoginException firstError = null;

	private LoginException firstRequiredError = null;

	private boolean success = false;
	
	private IModuleContext moduleContext = null;

	/**
	 * Instantiate a new <code>LoginContext</code> object with a name and a
	 * <code>CallbackHandler</code> object.
	 * 
	 * <p>
	 * 
	 * @param name
	 *            the name used as the index into the <code>Configuration</code>.
	 *            <p>
	 * 
	 * @param callbackHandler
	 *            the <code>CallbackHandler</code> object used by LoginModules to communicate with
	 *            the user.
	 * 
	 * @exception LoginException
	 *                if the caller-specified <code>name</code> does not appear in the
	 *                <code>Configuration</code> and there is no <code>Configuration</code> entry
	 *                for "<i>other</i>", or if the caller-specified <code>callbackHandler</code> is
	 *                <code>null</code>.
	 *                <p>
	 * @exception SecurityException
	 *                if a SecurityManager is set and the caller does not have
	 *                AuthPermission("createLoginContext.<i>name</i>"), or if a configuration entry
	 *                for <i>name</i> does not exist and the caller does not additionally have
	 *                AuthPermission("createLoginContext.other")
	 */
	protected LoginContext( String profileName, CallbackHandler callbackHandler ) throws LoginException
	{
		this(profileName, null, callbackHandler);
	}

	/**
	 * Instantiate a new <code>LoginContext</code> object with a name, a <code>Subject</code> to be
	 * authenticated, and a <code>CallbackHandler</code> object.
	 * 
	 * <p>
	 * 
	 * @param name
	 *            the name used as the index into the <code>Configuration</code>.
	 *            <p>
	 * 
	 * @param subject
	 *            the <code>Subject</code> to authenticate.
	 *            <p>
	 * 
	 * @param callbackHandler
	 *            the <code>CallbackHandler</code> object used by LoginModules to communicate with
	 *            the user.
	 * 
	 * @exception LoginException
	 *                if the caller-specified <code>name</code> does not appear in the
	 *                <code>Configuration</code> and there is no <code>Configuration</code> entry
	 *                for "<i>other</i>", or if the caller-specified <code>subject</code> is
	 *                <code>null</code>, or if the caller-specified <code>callbackHandler</code> is
	 *                <code>null</code>.
	 *                <p>
	 * @exception SecurityException
	 *                if a SecurityManager is set and the caller does not have
	 *                AuthPermission("createLoginContext.<i>name</i>"), or if a configuration entry
	 *                for <i>name</i> does not exist and the caller does not additionally have
	 *                AuthPermission("createLoginContext.other")
	 */
	protected LoginContext( String profileName, Subject subject, CallbackHandler callbackHandler )
		throws LoginException
	{
		this( Profile.getProfile(profileName), subject, callbackHandler );
	}

	/**
	 * Instantiate a new <code>LoginContext</code> object with a name, a <code>Subject</code> to be
	 * authenticated, a <code>CallbackHandler</code> object, and a login <code>Configuration</code>.
	 * 
	 * <p>
	 * 
	 * @param name
	 *            the name used as the index into the caller-specified <code>Configuration</code>.
	 *            <p>
	 * 
	 * @param subject
	 *            the <code>Subject</code> to authenticate, or <code>null</code>.
	 *            <p>
	 * 
	 * @param callbackHandler
	 *            the <code>CallbackHandler</code> object used by LoginModules to communicate with
	 *            the user, or <code>null</code>.
	 *            <p>
	 * 
	 * @param config
	 *            the <code>Configuration</code> that lists the login modules to be called to
	 *            perform the authentication, or <code>null</code>.
	 * 
	 * @exception LoginException
	 *                if the caller-specified <code>name</code> does not appear in the
	 *                <code>Configuration</code> and there is no <code>Configuration</code> entry
	 *                for "<i>other</i>".
	 *                <p>
	 * @exception SecurityException
	 *                if a SecurityManager is set, <i>config</i> is <code>null</code>, and either
	 *                the caller does not have AuthPermission("createLoginContext.<i>name</i>"), or
	 *                if a configuration entry for <i>name</i> does not exist and the caller does
	 *                not additionally have AuthPermission("createLoginContext.other")
	 * 
	 * @since 1.5
	 */
	protected LoginContext( Profile profile, Subject subject, CallbackHandler callbackHandler ) 
		throws LoginException
	{
		if (callbackHandler == null)
			throw new LoginException("The callback handler can not be null");
		if (profile == null)
			throw new LoginException("The profile object can not be null");

		moduleStack = profile.getEntries();
		if (moduleStack == null)
			throw new LoginException("Profile not found");
		
		moduleInstances = new ILoginModule[moduleStack.length];
		for (int i = 0; i < moduleStack.length; ++i)
		{
			ILoginModule module = moduleStack[i].getModule();
			if (module != null) 
				moduleInstances[i] = module;
			else
				moduleInstances[i] = null;
		}
		
		this.contextClassLoader = (ClassLoader) Thread.currentThread().getContextClassLoader();
		
		moduleContext = new ModuleContext(subject, callbackHandler, state);
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
		loginSucceeded = false;

		if (subject == null)
			subject = new Subject();

		try
		{
			// module invoked in doPrivileged
			invokePriv(LOGIN_METHOD);
			invokePriv(COMMIT_METHOD);
			loginSucceeded = true;
		} catch (LoginException le)
		{
			try
			{
				invokePriv(ABORT_METHOD);
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

		// module invoked in doPrivileged
		invokePriv(LOGOUT_METHOD);
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
	public Subject getSubject()
	{
		if (!loginSucceeded && !subjectProvided) return null;
		return subject;
	}

	private void clearState()
	{
		moduleIndex = 0;
		firstError = null;
		firstRequiredError = null;
		success = false;
	}

	private void throwException( LoginException originalError, LoginException le )
		throws LoginException
	{

		// first clear state
		clearState();

		// throw the exception
		LoginException error = (originalError != null) ? originalError : le;
		throw error;
	}

	/**
	 * Invokes the login, commit, and logout methods from a LoginModule inside a doPrivileged block.
	 * 
	 * This version is called if the caller did not instantiate the LoginContext with a
	 * Configuration object.
	 */
	private void invokePriv( final String methodName ) throws LoginException
	{
		invoke(methodName);
	}


	private void invoke( String methodName ) throws LoginException
	{

		// start at moduleIndex
		// - this can only be non-zero if methodName is LOGIN_METHOD

		for (int i = moduleIndex; i < moduleStack.length; i++, moduleIndex++)
		{
			try
			{
				int mIndex = 0;
				Method[] methods = null;

				methods = moduleStack[i].getModuleClass().getMethods();
				
				if (moduleInstances[i] == null)
				{
					// instantiate the statefull LoginModule
					Class<?> classRef = moduleStack[i].getModuleClass();
					Constructor<?> ctor = classRef.getConstructor(PARAMS);
					moduleInstances[i] = (ILoginModule) ctor.newInstance(CTOR_ARGS);
				}

				// find the requested method in the LoginModule
				for (mIndex = 0; mIndex < methods.length; mIndex++)
				{
					if (methods[mIndex].getName().equals(methodName)) break;
				}

				// invoke the LoginModule method
				boolean status = ((Boolean) methods[mIndex].invoke(moduleStack[i], moduleContext))
					.booleanValue();

				if (status == true)
				{

					// if SUFFICIENT, return if no prior REQUIRED errors
					if (!methodName.equals(ABORT_METHOD)
						&& !methodName.equals(LOGOUT_METHOD)
						&& moduleStack[i].getFlags() == ProfileEntryFlags.SUFFICIENT
						&& firstRequiredError == null)
					{
						// clear state
						clearState();
						return;
					}
					success = true;
				}
			} catch (NoSuchMethodException nsme)
			{
				throw new LoginException("Unable to instantiate the LoginModule, because "
						+ "it does not provide a no-argument constructor");
			} catch (InstantiationException ie)
			{
				throw new LoginException("Unable to instantiate the LoginModule:" + ie.getMessage());
			} catch (IllegalAccessException iae)
			{
				throw new LoginException("Unable to instantiate the LoginModule, because "
					+ "security restrictions");
			} catch (InvocationTargetException ite)
			{

				// failure cases

				LoginException le;

				if (ite.getCause() instanceof PendingException && methodName.equals(LOGIN_METHOD))
				{

					// XXX
					//
					// if a module's LOGIN_METHOD threw a PendingException
					// then immediately throw it.
					//
					// when LoginContext is called again,
					// the module that threw the exception is invoked first
					// (the module list is not invoked from the start).
					// previously thrown exception state is still present.
					//
					// it is assumed that the module which threw
					// the exception can have its
					// LOGIN_METHOD invoked twice in a row
					// without any commit/abort in between.
					//
					// in all cases when LoginContext returns
					// (either via natural return or by throwing an exception)
					// we need to call clearState before returning.
					// the only time that is not true is in this case -
					// do not call throwException here.

					throw (PendingException) ite.getCause();

				}
				else
					if (ite.getCause() instanceof LoginException)
					{

						le = (LoginException) ite.getCause();

					}
					else
						if (ite.getCause() instanceof SecurityException)
						{

							// do not want privacy leak
							// (e.g., sensitive file path in exception msg)

							le = new LoginException("Security Exception");
							le.initCause(new SecurityException());
							/*if (debug != null)
							{
								debug.println("original security exception with detail msg "
									+ "replaced by new exception with empty detail msg");
								debug.println("original security exception: "
									+ ite.getCause().toString());
							}*/
						}
						else
						{

							// capture an unexpected LoginModule exception
							java.io.StringWriter sw = new java.io.StringWriter();
							ite.getCause().printStackTrace(new java.io.PrintWriter(sw));
							sw.flush();
							le = new LoginException(sw.toString());
						}

				if (moduleStack[i].getFlags() == ProfileEntryFlags.REQUISITE)
				{
					// if REQUISITE, then immediately throw an exception
					if (methodName.equals(ABORT_METHOD) || methodName.equals(LOGOUT_METHOD))
					{
						if (firstRequiredError == null) firstRequiredError = le;
					}
					else
					{
						throwException(firstRequiredError, le);
					}

				}
				else
					if (moduleStack[i].getFlags() == ProfileEntryFlags.REQUIRED)
					{
						// mark down that a REQUIRED module failed
						if (firstRequiredError == null) firstRequiredError = le;
					}
					else
					{
						// mark down that an OPTIONAL module failed
						if (firstError == null) firstError = le;
					}
			}
		}

		// we went thru all the LoginModules.
		if (firstRequiredError != null)
		{
			// a REQUIRED module failed -- return the error
			throwException(firstRequiredError, null);
		}
		else
			if (success == false && firstError != null)
			{
				// no module succeeded -- return the first error
				throwException(firstError, null);
			}
			else
				if (success == false)
				{
					// no module succeeded -- all modules were IGNORED
					throwException(
						new LoginException(
							ResourcesMgr.getString("Login Failure: all modules ignored")), null);
				}
				else
				{
					// success

					clearState();
					return;
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
