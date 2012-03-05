package corinna.auth;

import javax.security.auth.login.LoginException;


public interface ILoginModule
{

	/**
     * Method to authenticate a <code>Subject</code> (phase 1).
     *
     * <p> The implementation of this method authenticates
     * a <code>Subject</code>.  For example, it may prompt for
     * <code>Subject</code> information such
     * as a username and password and then attempt to verify the password.
     * This method saves the result of the authentication attempt
     * as private state within the LoginModule.
     *
     * <p>
     *
     * @exception LoginException if the authentication fails
     *
     * @return true if the authentication succeeded, or false if this
     *			<code>LoginModule</code> should be ignored.
     */
    boolean login( IModuleContext context ) throws LoginException;

    /**
     * Method to commit the authentication process (phase 2).
     *
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> method), then this method associates relevant
     * Principals and Credentials with the <code>Subject</code> located in the
     * <code>LoginModule</code>.  If this LoginModule's own
     * authentication attempted failed, then this method removes/destroys
     * any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the commit fails
     *
     * @return true if this method succeeded, or false if this
     *			<code>LoginModule</code> should be ignored.
     */
    boolean commit( IModuleContext context ) throws LoginException;

    /**
     * Method to abort the authentication process (phase 2).
     *
     * <p> This method is called if the LoginContext's
     * overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * did not succeed).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> method), then this method cleans up any state
     * that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the abort fails
     *
     * @return true if this method succeeded, or false if this
     *			<code>LoginModule</code> should be ignored.
     */
    boolean abort( IModuleContext context ) throws LoginException;

    /**
     * Method which logs out a <code>Subject</code>.  
     *
     * <p>An implementation of this method might remove/destroy a Subject's
     * Principals and Credentials.
     *
     * <p>
     *
     * @exception LoginException if the logout fails
     *
     * @return true if this method succeeded, or false if this
     *			<code>LoginModule</code> should be ignored.
     */
    boolean logout( IModuleContext context ) throws LoginException;
	
}
