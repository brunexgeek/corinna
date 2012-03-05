package corinna.auth;




@SuppressWarnings("serial")
public abstract class Subject implements ISubject
{
	
	
	
//
//	/**
//	 * A <code>Set</code> that provides a view of all of this Subject's Principals
//	 * 
//	 * <p>
//	 * 
//	 * @serial Each element in this set is a <code>java.security.Principal</code>. The set is a
//	 *         <code>Subject.SecureSet</code>.
//	 */
//	Set<Principal> principals;
//
//	/**
//	 * Sets that provide a view of all of this Subject's Credentials
//	 */
//	transient Set<?> pubCredentials;
//
//	transient Set<?> privCredentials;
//
//	/**
//	 * Whether this Subject is read-only
//	 * 
//	 * @serial
//	 */
//	private volatile boolean readOnly = false;
//
//	private static final int PRINCIPAL_SET = 1;
//
//	private static final int PUB_CREDENTIAL_SET = 2;
//
//	private static final int PRIV_CREDENTIAL_SET = 3;
//
//	/**
//	 * Create an instance of a <code>Subject</code> with an empty <code>Set</code> of Principals and
//	 * empty Sets of public and private credentials.
//	 * 
//	 * <p>
//	 * The newly constructed Sets check whether this <code>Subject</code> has been set read-only
//	 * before permitting subsequent modifications. The newly created Sets also prevent illegal
//	 * modifications by ensuring that callers have sufficient permissions.
//	 * 
//	 * <p>
//	 * To modify the Principals Set, the caller must have
//	 * <code>AuthPermission("modifyPrincipals")</code>. To modify the public credential Set, the
//	 * caller must have <code>AuthPermission("modifyPublicCredentials")</code>. To modify the
//	 * private credential Set, the caller must have
//	 * <code>AuthPermission("modifyPrivateCredentials")</code>.
//	 */
//	public Subject()
//	{
//
//		this.principals = Collections.synchronizedSet(new Set(this, PRINCIPAL_SET));
//		this.pubCredentials = Collections.synchronizedSet(new Set(this, PUB_CREDENTIAL_SET));
//		this.privCredentials = Collections.synchronizedSet(new Set(this, PRIV_CREDENTIAL_SET));
//	}
//
//	/**
//	 * Create an instance of a <code>Subject</code> with Principals and credentials.
//	 * 
//	 * <p>
//	 * The Principals and credentials from the specified Sets are copied into newly constructed
//	 * Sets. These newly created Sets check whether this <code>Subject</code> has been set read-only
//	 * before permitting subsequent modifications. The newly created Sets also prevent illegal
//	 * modifications by ensuring that callers have sufficient permissions.
//	 * 
//	 * <p>
//	 * To modify the Principals Set, the caller must have
//	 * <code>AuthPermission("modifyPrincipals")</code>. To modify the public credential Set, the
//	 * caller must have <code>AuthPermission("modifyPublicCredentials")</code>. To modify the
//	 * private credential Set, the caller must have
//	 * <code>AuthPermission("modifyPrivateCredentials")</code>.
//	 * <p>
//	 * 
//	 * @param readOnly
//	 *            true if the <code>Subject</code> is to be read-only, and false otherwise.
//	 *            <p>
//	 * 
//	 * @param principals
//	 *            the <code>Set</code> of Principals to be associated with this <code>Subject</code>
//	 *            .
//	 *            <p>
//	 * 
//	 * @param pubCredentials
//	 *            the <code>Set</code> of public credentials to be associated with this
//	 *            <code>Subject</code>.
//	 *            <p>
//	 * 
//	 * @param privCredentials
//	 *            the <code>Set</code> of private credentials to be associated with this
//	 *            <code>Subject</code>.
//	 * 
//	 * @exception NullPointerException
//	 *                if the specified <code>principals</code>, <code>pubCredentials</code>, or
//	 *                <code>privCredentials</code> are <code>null</code>.
//	 */
//	public Subject( boolean readOnly, Set<? extends Principal> principals, Set<?> pubCredentials,
//		Set<?> privCredentials )
//	{
//
//		if (principals == null || pubCredentials == null || privCredentials == null)
//			throw new NullPointerException(ResourcesMgr.getString("invalid null input(s)"));
//
//		this.principals = Collections
//			.synchronizedSet(new SecureSet(this, PRINCIPAL_SET, principals));
//		this.pubCredentials = Collections.synchronizedSet(new SecureSet(this, PUB_CREDENTIAL_SET,
//			pubCredentials));
//		this.privCredentials = Collections.synchronizedSet(new SecureSet(this, PRIV_CREDENTIAL_SET,
//			privCredentials));
//		this.readOnly = readOnly;
//	}
//
//	/**
//	 * Set this <code>Subject</code> to be read-only.
//	 * 
//	 * <p>
//	 * Modifications (additions and removals) to this Subject's <code>Principal</code>
//	 * <code>Set</code> and credential Sets will be disallowed. The <code>destroy</code> operation
//	 * on this Subject's credentials will still be permitted.
//	 * 
//	 * <p>
//	 * Subsequent attempts to modify the Subject's <code>Principal</code> and credential Sets will
//	 * result in an <code>IllegalStateException</code> being thrown. Also, once a
//	 * <code>Subject</code> is read-only, it can not be reset to being writable again.
//	 * 
//	 * <p>
//	 * 
//	 * @exception SecurityException
//	 *                if the caller does not have permission to set this <code>Subject</code> to be
//	 *                read-only.
//	 */
//	public void setReadOnly()
//	{
//		java.lang.SecurityManager sm = System.getSecurityManager();
//		if (sm != null)
//		{
//			sm.checkPermission(new AuthPermission("setReadOnly"));
//		}
//
//		this.readOnly = true;
//	}
//
//	/**
//	 * Query whether this <code>Subject</code> is read-only.
//	 * 
//	 * <p>
//	 * 
//	 * @return true if this <code>Subject</code> is read-only, false otherwise.
//	 */
//	public boolean isReadOnly()
//	{
//		return this.readOnly;
//	}
//
//	/**
//	 * Get the <code>Subject</code> associated with the provided <code>AccessControlContext</code>.
//	 * 
//	 * <p>
//	 * The <code>AccessControlContext</code> may contain many Subjects (from nested
//	 * <code>doAs</code> calls). In this situation, the most recent <code>Subject</code> associated
//	 * with the <code>AccessControlContext</code> is returned.
//	 * 
//	 * <p>
//	 * 
//	 * @param acc
//	 *            the <code>AccessControlContext</code> from which to retrieve the
//	 *            <code>Subject</code>.
//	 * 
//	 * @return the <code>Subject</code> associated with the provided
//	 *         <code>AccessControlContext</code>, or <code>null</code> if no <code>Subject</code> is
//	 *         associated with the provided <code>AccessControlContext</code>.
//	 * 
//	 * @exception SecurityException
//	 *                if the caller does not have permission to get the <code>Subject</code>.
//	 *                <p>
//	 * 
//	 * @exception NullPointerException
//	 *                if the provided <code>AccessControlContext</code> is <code>null</code>.
//	 */
//	public static Subject getSubject( final AccessControlContext acc )
//	{
//
//		java.lang.SecurityManager sm = System.getSecurityManager();
//		if (sm != null)
//		{
//			sm.checkPermission(new AuthPermission("getSubject"));
//		}
//
//		if (acc == null)
//		{
//			throw new NullPointerException(
//				ResourcesMgr.getString("invalid null AccessControlContext provided"));
//		}
//
//		// return the Subject from the DomainCombiner of the provided context
//		return (Subject) AccessController.doPrivileged(new java.security.PrivilegedAction()
//		{
//
//			public Object run()
//			{
//				DomainCombiner dc = acc.getDomainCombiner();
//				if (!(dc instanceof SubjectDomainCombiner)) return null;
//				SubjectDomainCombiner sdc = (SubjectDomainCombiner) dc;
//				return sdc.getSubject();
//			}
//		});
//	}
//
//	/**
//	 * Perform work as a particular <code>Subject</code>.
//	 * 
//	 * <p>
//	 * This method first retrieves the current Thread's <code>AccessControlContext</code> via
//	 * <code>AccessController.getContext</code>, and then instantiates a new
//	 * <code>AccessControlContext</code> using the retrieved context along with a new
//	 * <code>SubjectDomainCombiner</code> (constructed using the provided <code>Subject</code>).
//	 * Finally, this method invokes <code>AccessController.doPrivileged</code>, passing it the
//	 * provided <code>PrivilegedAction</code>, as well as the newly constructed
//	 * <code>AccessControlContext</code>.
//	 * 
//	 * <p>
//	 * 
//	 * @param subject
//	 *            the <code>Subject</code> that the specified <code>action</code> will run as. This
//	 *            parameter may be <code>null</code>.
//	 *            <p>
//	 * 
//	 * @param action
//	 *            the code to be run as the specified <code>Subject</code>.
//	 *            <p>
//	 * 
//	 * @return the value returned by the PrivilegedAction's <code>run</code> method.
//	 * 
//	 * @exception NullPointerException
//	 *                if the <code>PrivilegedAction</code> is <code>null</code>.
//	 *                <p>
//	 * 
//	 * @exception SecurityException
//	 *                if the caller does not have permission to invoke this method.
//	 */
//	public static <T> T doAs( final Subject subject, final java.security.PrivilegedAction<T> action )
//	{
//
//		java.lang.SecurityManager sm = System.getSecurityManager();
//		if (sm != null)
//		{
//			sm.checkPermission(SecurityConstants.DO_AS_PERMISSION);
//		}
//		if (action == null)
//			throw new NullPointerException(ResourcesMgr.getString("invalid null action provided"));
//
//		// set up the new Subject-based AccessControlContext
//		// for doPrivileged
//		final AccessControlContext currentAcc = AccessController.getContext();
//
//		// call doPrivileged and push this new context on the stack
//		return java.security.AccessController.doPrivileged(action,
//			createContext(subject, currentAcc));
//	}
//
//	/**
//	 * Perform work as a particular <code>Subject</code>.
//	 * 
//	 * <p>
//	 * This method first retrieves the current Thread's <code>AccessControlContext</code> via
//	 * <code>AccessController.getContext</code>, and then instantiates a new
//	 * <code>AccessControlContext</code> using the retrieved context along with a new
//	 * <code>SubjectDomainCombiner</code> (constructed using the provided <code>Subject</code>).
//	 * Finally, this method invokes <code>AccessController.doPrivileged</code>, passing it the
//	 * provided <code>PrivilegedExceptionAction</code>, as well as the newly constructed
//	 * <code>AccessControlContext</code>.
//	 * 
//	 * <p>
//	 * 
//	 * @param subject
//	 *            the <code>Subject</code> that the specified <code>action</code> will run as. This
//	 *            parameter may be <code>null</code>.
//	 *            <p>
//	 * 
//	 * @param action
//	 *            the code to be run as the specified <code>Subject</code>.
//	 *            <p>
//	 * 
//	 * @return the value returned by the PrivilegedExceptionAction's <code>run</code> method.
//	 * 
//	 * @exception PrivilegedActionException
//	 *                if the <code>PrivilegedExceptionAction.run</code> method throws a checked
//	 *                exception.
//	 *                <p>
//	 * 
//	 * @exception NullPointerException
//	 *                if the specified <code>PrivilegedExceptionAction</code> is <code>null</code>.
//	 *                <p>
//	 * 
//	 * @exception SecurityException
//	 *                if the caller does not have permission to invoke this method.
//	 */
//	public static <T> T doAs( final Subject subject,
//		final java.security.PrivilegedExceptionAction<T> action )
//		throws java.security.PrivilegedActionException
//	{
//
//		java.lang.SecurityManager sm = System.getSecurityManager();
//		if (sm != null)
//		{
//			sm.checkPermission(SecurityConstants.DO_AS_PERMISSION);
//		}
//
//		if (action == null)
//			throw new NullPointerException(ResourcesMgr.getString("invalid null action provided"));
//
//		// set up the new Subject-based AccessControlContext for doPrivileged
//		final AccessControlContext currentAcc = AccessController.getContext();
//
//		// call doPrivileged and push this new context on the stack
//		return java.security.AccessController.doPrivileged(action,
//			createContext(subject, currentAcc));
//	}
//
//	/**
//	 * Perform privileged work as a particular <code>Subject</code>.
//	 * 
//	 * <p>
//	 * This method behaves exactly as <code>Subject.doAs</code>, except that instead of retrieving
//	 * the current Thread's <code>AccessControlContext</code>, it uses the provided
//	 * <code>AccessControlContext</code>. If the provided <code>AccessControlContext</code> is
//	 * <code>null</code>, this method instantiates a new <code>AccessControlContext</code> with an
//	 * empty collection of ProtectionDomains.
//	 * 
//	 * <p>
//	 * 
//	 * @param subject
//	 *            the <code>Subject</code> that the specified <code>action</code> will run as. This
//	 *            parameter may be <code>null</code>.
//	 *            <p>
//	 * 
//	 * @param action
//	 *            the code to be run as the specified <code>Subject</code>.
//	 *            <p>
//	 * 
//	 * @param acc
//	 *            the <code>AccessControlContext</code> to be tied to the specified <i>subject</i>
//	 *            and <i>action</i>.
//	 *            <p>
//	 * 
//	 * @return the value returned by the PrivilegedAction's <code>run</code> method.
//	 * 
//	 * @exception NullPointerException
//	 *                if the <code>PrivilegedAction</code> is <code>null</code>.
//	 *                <p>
//	 * 
//	 * @exception SecurityException
//	 *                if the caller does not have permission to invoke this method.
//	 */
//	public static <T> T doAsPrivileged( final Subject subject,
//		final java.security.PrivilegedAction<T> action, final java.security.AccessControlContext acc )
//	{
//
//		java.lang.SecurityManager sm = System.getSecurityManager();
//		if (sm != null)
//		{
//			sm.checkPermission(SecurityConstants.DO_AS_PRIVILEGED_PERMISSION);
//		}
//
//		if (action == null)
//			throw new NullPointerException(ResourcesMgr.getString("invalid null action provided"));
//
//		// set up the new Subject-based AccessControlContext
//		// for doPrivileged
//		final AccessControlContext callerAcc = (acc == null ? new AccessControlContext(
//			new ProtectionDomain[0]) : acc);
//
//		// call doPrivileged and push this new context on the stack
//		return java.security.AccessController.doPrivileged(action,
//			createContext(subject, callerAcc));
//	}
//
//	/**
//	 * Perform privileged work as a particular <code>Subject</code>.
//	 * 
//	 * <p>
//	 * This method behaves exactly as <code>Subject.doAs</code>, except that instead of retrieving
//	 * the current Thread's <code>AccessControlContext</code>, it uses the provided
//	 * <code>AccessControlContext</code>. If the provided <code>AccessControlContext</code> is
//	 * <code>null</code>, this method instantiates a new <code>AccessControlContext</code> with an
//	 * empty collection of ProtectionDomains.
//	 * 
//	 * <p>
//	 * 
//	 * @param subject
//	 *            the <code>Subject</code> that the specified <code>action</code> will run as. This
//	 *            parameter may be <code>null</code>.
//	 *            <p>
//	 * 
//	 * @param action
//	 *            the code to be run as the specified <code>Subject</code>.
//	 *            <p>
//	 * 
//	 * @param acc
//	 *            the <code>AccessControlContext</code> to be tied to the specified <i>subject</i>
//	 *            and <i>action</i>.
//	 *            <p>
//	 * 
//	 * @return the value returned by the PrivilegedExceptionAction's <code>run</code> method.
//	 * 
//	 * @exception PrivilegedActionException
//	 *                if the <code>PrivilegedExceptionAction.run</code> method throws a checked
//	 *                exception.
//	 *                <p>
//	 * 
//	 * @exception NullPointerException
//	 *                if the specified <code>PrivilegedExceptionAction</code> is <code>null</code>.
//	 *                <p>
//	 * 
//	 * @exception SecurityException
//	 *                if the caller does not have permission to invoke this method.
//	 */
//	public static <T> T doAsPrivileged( final Subject subject,
//		final java.security.PrivilegedExceptionAction<T> action,
//		final java.security.AccessControlContext acc )
//		throws java.security.PrivilegedActionException
//	{
//
//		java.lang.SecurityManager sm = System.getSecurityManager();
//		if (sm != null)
//		{
//			sm.checkPermission(SecurityConstants.DO_AS_PRIVILEGED_PERMISSION);
//		}
//
//		if (action == null)
//			throw new NullPointerException(ResourcesMgr.getString("invalid null action provided"));
//
//		// set up the new Subject-based AccessControlContext for doPrivileged
//		final AccessControlContext callerAcc = (acc == null ? new AccessControlContext(
//			new ProtectionDomain[0]) : acc);
//
//		// call doPrivileged and push this new context on the stack
//		return java.security.AccessController.doPrivileged(action,
//			createContext(subject, callerAcc));
//	}
//
//	private static AccessControlContext createContext( final Subject subject,
//		final AccessControlContext acc )
//	{
//
//		return (AccessControlContext) java.security.AccessController
//			.doPrivileged(new java.security.PrivilegedAction()
//			{
//
//				public Object run()
//				{
//					if (subject == null)
//						return new AccessControlContext(acc, null);
//					else
//						return new AccessControlContext(acc, new SubjectDomainCombiner(subject));
//				}
//			});
//	}
//
//	/**
//	 * Return the <code>Set</code> of Principals associated with this <code>Subject</code>. Each
//	 * <code>Principal</code> represents an identity for this <code>Subject</code>.
//	 * 
//	 * <p>
//	 * The returned <code>Set</code> is backed by this Subject's internal <code>Principal</code>
//	 * <code>Set</code>. Any modification to the returned <code>Set</code> affects the internal
//	 * <code>Principal</code> <code>Set</code> as well.
//	 * 
//	 * <p>
//	 * 
//	 * @return The <code>Set</code> of Principals associated with this <code>Subject</code>.
//	 */
//	public Set<Principal> getPrincipals()
//	{
//
//		// always return an empty Set instead of null
//		// so LoginModules can add to the Set if necessary
//		return principals;
//	}
//
//	/**
//	 * Return a <code>Set</code> of Principals associated with this <code>Subject</code> that are
//	 * instances or subclasses of the specified <code>Class</code>.
//	 * 
//	 * <p>
//	 * The returned <code>Set</code> is not backed by this Subject's internal <code>Principal</code>
//	 * <code>Set</code>. A new <code>Set</code> is created and returned for each method invocation.
//	 * Modifications to the returned <code>Set</code> will not affect the internal
//	 * <code>Principal</code> <code>Set</code>.
//	 * 
//	 * <p>
//	 * 
//	 * @param c
//	 *            the returned <code>Set</code> of Principals will all be instances of this class.
//	 * 
//	 * @return a <code>Set</code> of Principals that are instances of the specified
//	 *         <code>Class</code>.
//	 * 
//	 * @exception NullPointerException
//	 *                if the specified <code>Class</code> is <code>null</code>.
//	 */
//	public <T extends Principal> Set<T> getPrincipals( Class<T> c )
//	{
//
//		if (c == null)
//			throw new NullPointerException(ResourcesMgr.getString("invalid null Class provided"));
//
//		// always return an empty Set instead of null
//		// so LoginModules can add to the Set if necessary
//		return new ClassSet(PRINCIPAL_SET, c);
//	}
//
//	/**
//	 * Return the <code>Set</code> of public credentials held by this <code>Subject</code>.
//	 * 
//	 * <p>
//	 * The returned <code>Set</code> is backed by this Subject's internal public Credential
//	 * <code>Set</code>. Any modification to the returned <code>Set</code> affects the internal
//	 * public Credential <code>Set</code> as well.
//	 * 
//	 * <p>
//	 * 
//	 * @return A <code>Set</code> of public credentials held by this <code>Subject</code>.
//	 */
//	public Set<Object> getPublicCredentials()
//	{
//
//		// always return an empty Set instead of null
//		// so LoginModules can add to the Set if necessary
//		return pubCredentials;
//	}
//
//	/**
//	 * Return the <code>Set</code> of private credentials held by this <code>Subject</code>.
//	 * 
//	 * <p>
//	 * The returned <code>Set</code> is backed by this Subject's internal private Credential
//	 * <code>Set</code>. Any modification to the returned <code>Set</code> affects the internal
//	 * private Credential <code>Set</code> as well.
//	 * 
//	 * <p>
//	 * A caller requires permissions to access the Credentials in the returned <code>Set</code>, or
//	 * to modify the <code>Set</code> itself. A <code>SecurityException</code> is thrown if the
//	 * caller does not have the proper permissions.
//	 * 
//	 * <p>
//	 * While iterating through the <code>Set</code>, a <code>SecurityException</code> is thrown if
//	 * the caller does not have permission to access a particular Credential. The
//	 * <code>Iterator</code> is nevertheless advanced to next element in the <code>Set</code>.
//	 * 
//	 * <p>
//	 * 
//	 * @return A <code>Set</code> of private credentials held by this <code>Subject</code>.
//	 */
//	public Set<Object> getPrivateCredentials()
//	{
//
//		// XXX
//		// we do not need a security check for
//		// AuthPermission(getPrivateCredentials)
//		// because we already restrict access to private credentials
//		// via the PrivateCredentialPermission. all the extra AuthPermission
//		// would do is protect the set operations themselves
//		// (like size()), which don't seem security-sensitive.
//
//		// always return an empty Set instead of null
//		// so LoginModules can add to the Set if necessary
//		return privCredentials;
//	}
//
//	/**
//	 * Return a <code>Set</code> of public credentials associated with this <code>Subject</code>
//	 * that are instances or subclasses of the specified <code>Class</code>.
//	 * 
//	 * <p>
//	 * The returned <code>Set</code> is not backed by this Subject's internal public Credential
//	 * <code>Set</code>. A new <code>Set</code> is created and returned for each method invocation.
//	 * Modifications to the returned <code>Set</code> will not affect the internal public Credential
//	 * <code>Set</code>.
//	 * 
//	 * <p>
//	 * 
//	 * @param c
//	 *            the returned <code>Set</code> of public credentials will all be instances of this
//	 *            class.
//	 * 
//	 * @return a <code>Set</code> of public credentials that are instances of the specified
//	 *         <code>Class</code>.
//	 * 
//	 * @exception NullPointerException
//	 *                if the specified <code>Class</code> is <code>null</code>.
//	 */
//	public <T> Set<T> getPublicCredentials( Class<T> c )
//	{
//
//		if (c == null)
//			throw new NullPointerException(ResourcesMgr.getString("invalid null Class provided"));
//
//		// always return an empty Set instead of null
//		// so LoginModules can add to the Set if necessary
//		return new ClassSet<T>(PUB_CREDENTIAL_SET, c);
//	}
//
//	/**
//	 * Return a <code>Set</code> of private credentials associated with this <code>Subject</code>
//	 * that are instances or subclasses of the specified <code>Class</code>.
//	 * 
//	 * <p>
//	 * The caller must have permission to access all of the requested Credentials, or a
//	 * <code>SecurityException</code> will be thrown.
//	 * 
//	 * <p>
//	 * The returned <code>Set</code> is not backed by this Subject's internal private Credential
//	 * <code>Set</code>. A new <code>Set</code> is created and returned for each method invocation.
//	 * Modifications to the returned <code>Set</code> will not affect the internal private
//	 * Credential <code>Set</code>.
//	 * 
//	 * <p>
//	 * 
//	 * @param c
//	 *            the returned <code>Set</code> of private credentials will all be instances of this
//	 *            class.
//	 * 
//	 * @return a <code>Set</code> of private credentials that are instances of the specified
//	 *         <code>Class</code>.
//	 * 
//	 * @exception NullPointerException
//	 *                if the specified <code>Class</code> is <code>null</code>.
//	 */
//	public <T> Set<T> getPrivateCredentials( Class<T> c )
//	{
//
//		// XXX
//		// we do not need a security check for
//		// AuthPermission(getPrivateCredentials)
//		// because we already restrict access to private credentials
//		// via the PrivateCredentialPermission. all the extra AuthPermission
//		// would do is protect the set operations themselves
//		// (like size()), which don't seem security-sensitive.
//
//		if (c == null)
//			throw new NullPointerException(ResourcesMgr.getString("invalid null Class provided"));
//
//		// always return an empty Set instead of null
//		// so LoginModules can add to the Set if necessary
//		return new ClassSet<T>(PRIV_CREDENTIAL_SET, c);
//	}
//
//	/**
//	 * Compares the specified Object with this <code>Subject</code> for equality. Returns true if
//	 * the given object is also a Subject and the two <code>Subject</code> instances are equivalent.
//	 * More formally, two <code>Subject</code> instances are equal if their <code>Principal</code>
//	 * and <code>Credential</code> Sets are equal.
//	 * 
//	 * <p>
//	 * 
//	 * @param o
//	 *            Object to be compared for equality with this <code>Subject</code>.
//	 * 
//	 * @return true if the specified Object is equal to this <code>Subject</code>.
//	 * 
//	 * @exception SecurityException
//	 *                if the caller does not have permission to access the private credentials for
//	 *                this <code>Subject</code>, or if the caller does not have permission to access
//	 *                the private credentials for the provided <code>Subject</code>.
//	 */
//	public boolean equals( Object o )
//	{
//
//		if (o == null) return false;
//
//		if (this == o) return true;
//
//		if (o instanceof Subject)
//		{
//
//			final Subject that = (Subject) o;
//
//			// check the principal and credential sets
//			Set thatPrincipals;
//			synchronized (that.principals)
//			{
//				// avoid deadlock from dual locks
//				thatPrincipals = new HashSet(that.principals);
//			}
//			if (!principals.equals(thatPrincipals))
//			{
//				return false;
//			}
//
//			Set thatPubCredentials;
//			synchronized (that.pubCredentials)
//			{
//				// avoid deadlock from dual locks
//				thatPubCredentials = new HashSet(that.pubCredentials);
//			}
//			if (!pubCredentials.equals(thatPubCredentials))
//			{
//				return false;
//			}
//
//			Set thatPrivCredentials;
//			synchronized (that.privCredentials)
//			{
//				// avoid deadlock from dual locks
//				thatPrivCredentials = new HashSet(that.privCredentials);
//			}
//			if (!privCredentials.equals(thatPrivCredentials))
//			{
//				return false;
//			}
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * Return the String representation of this <code>Subject</code>.
//	 * 
//	 * <p>
//	 * 
//	 * @return the String representation of this <code>Subject</code>.
//	 */
//	public String toString()
//	{
//		return toString(true);
//	}
//
//	/**
//	 * package private convenience method to print out the Subject without firing off a security
//	 * check when trying to access the Private Credentials
//	 */
//	String toString( boolean includePrivateCredentials )
//	{
//
//		String s = ResourcesMgr.getString("Subject:\n");
//		String suffix = "";
//
//		synchronized (principals)
//		{
//			Iterator pI = principals.iterator();
//			while (pI.hasNext())
//			{
//				Principal p = (Principal) pI.next();
//				suffix = suffix + ResourcesMgr.getString("\tPrincipal: ") + p.toString()
//					+ ResourcesMgr.getString("\n");
//			}
//		}
//
//		synchronized (pubCredentials)
//		{
//			Iterator pI = pubCredentials.iterator();
//			while (pI.hasNext())
//			{
//				Object o = pI.next();
//				suffix = suffix + ResourcesMgr.getString("\tPublic Credential: ") + o.toString()
//					+ ResourcesMgr.getString("\n");
//			}
//		}
//
//		if (includePrivateCredentials)
//		{
//			synchronized (privCredentials)
//			{
//				Iterator pI = privCredentials.iterator();
//				while (pI.hasNext())
//				{
//					try
//					{
//						Object o = pI.next();
//						suffix += ResourcesMgr.getString("\tPrivate Credential: ") + o.toString()
//							+ ResourcesMgr.getString("\n");
//					} catch (SecurityException se)
//					{
//						suffix += ResourcesMgr.getString("\tPrivate Credential inaccessible\n");
//						break;
//					}
//				}
//			}
//		}
//		return s + suffix;
//	}
//
//	/**
//	 * Returns a hashcode for this <code>Subject</code>.
//	 * 
//	 * <p>
//	 * 
//	 * @return a hashcode for this <code>Subject</code>.
//	 * 
//	 * @exception SecurityException
//	 *                if the caller does not have permission to access this Subject's private
//	 *                credentials.
//	 */
//	public int hashCode()
//	{
//
//		/**
//		 * The hashcode is derived exclusive or-ing the hashcodes of this Subject's Principals and
//		 * credentials.
//		 * 
//		 * If a particular credential was destroyed (<code>credential.hashCode()</code> throws an
//		 * <code>IllegalStateException</code>), the hashcode for that credential is derived via:
//		 * <code>credential.getClass().toString().hashCode()</code>.
//		 */
//
//		int hashCode = 0;
//
//		synchronized (principals)
//		{
//			Iterator pIterator = principals.iterator();
//			while (pIterator.hasNext())
//			{
//				Principal p = (Principal) pIterator.next();
//				hashCode ^= p.hashCode();
//			}
//		}
//
//		synchronized (pubCredentials)
//		{
//			Iterator pubCIterator = pubCredentials.iterator();
//			while (pubCIterator.hasNext())
//			{
//				hashCode ^= getCredHashCode(pubCIterator.next());
//			}
//		}
//		return hashCode;
//	}
//
//	/**
//	 * get a credential's hashcode
//	 */
//	private int getCredHashCode( Object o )
//	{
//		try
//		{
//			return o.hashCode();
//		} catch (IllegalStateException ise)
//		{
//			return o.getClass().toString().hashCode();
//		}
//	}
//
//	/**
//	 * Writes this object out to a stream (i.e., serializes it).
//	 */
//	private void writeObject( java.io.ObjectOutputStream oos ) throws java.io.IOException
//	{
//		synchronized (principals)
//		{
//			oos.defaultWriteObject();
//		}
//	}
//
//	/**
//	 * Reads this object from a stream (i.e., deserializes it)
//	 */
//	private void readObject( java.io.ObjectInputStream s ) throws java.io.IOException,
//		ClassNotFoundException
//	{
//
//		s.defaultReadObject();
//
//		// The Credential <code>Set</code> is not serialized, but we do not
//		// want the default deserialization routine to set it to null.
//		this.pubCredentials = Collections.synchronizedSet(new HashSet(this, PUB_CREDENTIAL_SET));
//		this.privCredentials = Collections.synchronizedSet(new HashSet(this, PRIV_CREDENTIAL_SET));
//	}

}
