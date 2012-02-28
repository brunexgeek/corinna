/*
 * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.bindlet.http;


import javax.bindlet.IBindletRequest;


/**
 * 
 * Extends the {@link corinna.service.bindlet.IBindletRequest} interface to provide request
 * information for HTTP servlets.
 * 
 * <p>
 * The servlet container creates an <code>HttpServletRequest</code> object and passes it as an
 * argument to the servlet's service methods (<code>doGet</code>, <code>doPost</code>, etc).
 * 
 * 
 * @author Various
 * @version $Version$
 * 
 * 
 */
//TODO: mover para 'javax.bindlet.http'
public interface IHttpBindletRequest extends IWebBindletRequest
{

	/**
	 * String identifier for Basic authentication. Value "BASIC"
	 */
	public static final String BASIC_AUTH = "BASIC";

	/**
	 * String identifier for Form authentication. Value "FORM"
	 */
	public static final String FORM_AUTH = "FORM";

	/**
	 * String identifier for Client Certificate authentication. Value "CLIENT_CERT"
	 */
	public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";

	/**
	 * String identifier for Digest authentication. Value "DIGEST"
	 */
	public static final String DIGEST_AUTH = "DIGEST";

	/**
	 * Returns the name of the authentication scheme used to protect the servlet. All servlet
	 * containers support basic, form and client certificate authentication, and may additionally
	 * support digest authentication. If the servlet is not authenticated <code>null</code> is
	 * returned.
	 * 
	 * <p>
	 * Same as the value of the CGI variable AUTH_TYPE.
	 * 
	 * 
	 * @return one of the static members BASIC_AUTH, FORM_AUTH, CLIENT_CERT_AUTH, DIGEST_AUTH
	 *         (suitable for == comparison) or the container-specific string indicating the
	 *         authentication scheme, or <code>null</code> if the request was not authenticated.
	 * 
	 */

	public String getAuthType();

	/**
	 * 
	 * Returns an array containing all of the <code>Cookie</code> objects the client sent with this
	 * request. This method returns <code>null</code> if no cookies were sent.
	 * 
	 * @return an array of all the <code>Cookies</code> included with this request, or
	 *         <code>null</code> if the request has no cookies
	 * 
	 * 
	 */

	public Cookie[] getCookies();

	/**
	 * 
	 * Returns the login of the user making this request, if the user has been authenticated, or
	 * <code>null</code> if the user has not been authenticated. Whether the user name is sent with
	 * each subsequent request depends on the browser and type of authentication. Same as the value
	 * of the CGI variable REMOTE_USER.
	 * 
	 * @return a <code>String</code> specifying the login of the user making this request, or
	 *         <code>null</code> if the user login is not known
	 * 
	 */

	public String getRemoteUser();

	/**
	 * 
	 * Returns a boolean indicating whether the authenticated user is included in the specified
	 * logical "role". Roles and role membership can be defined using deployment descriptors. If the
	 * user has not been authenticated, the method returns <code>false</code>.
	 * 
	 * @param role
	 *            a <code>String</code> specifying the name of the role
	 * 
	 * @return a <code>boolean</code> indicating whether the user making this request belongs to a
	 *         given role; <code>false</code> if the user has not been authenticated
	 * 
	 */

	public boolean isUserInRole( String role );

	/**
	 * 
	 * Returns a <code>java.security.Principal</code> object containing the name of the current
	 * authenticated user. If the user has not been authenticated, the method returns
	 * <code>null</code>.
	 * 
	 * @return a <code>java.security.Principal</code> containing the name of the user making this
	 *         request; <code>null</code> if the user has not been authenticated
	 * 
	 */

	public java.security.Principal getUserPrincipal();

	/**
	 * 
	 * Returns the session ID specified by the client. This may not be the same as the ID of the
	 * current valid session for this request. If the client did not specify a session ID, this
	 * method returns <code>null</code>.
	 * 
	 * 
	 * @return a <code>String</code> specifying the session ID, or <code>null</code> if the request
	 *         did not specify a session ID
	 * 
	 * @see #isRequestedSessionIdValid
	 * 
	 */

	public String getRequestedSessionId();

	/**
	 * 
	 * Returns the part of this request's URL from the protocol name up to the query string in the
	 * first line of the HTTP request. The web container does not decode this String. For example:
	 * 
	 * 
	 * 
	 * <table summary="Examples of Returned Values">
	 * <tr align=left>
	 * <th>First line of HTTP request</th>
	 * <th>Returned Value</th>
	 * <tr>
	 * <td>POST /some/path.html HTTP/1.1
	 * <td>
	 * <td>/some/path.html
	 * <tr>
	 * <td>GET http://foo.bar/a.html HTTP/1.0
	 * <td>
	 * <td>/a.html
	 * <tr>
	 * <td>HEAD /xyz?a=b HTTP/1.1
	 * <td>
	 * <td>/xyz
	 * </table>
	 * 
	 * <p>
	 * To reconstruct an URL with a scheme and host, use {@link HttpUtils#getRequestURL}.
	 * 
	 * @return a <code>String</code> containing the part of the URL from the protocol name up to the
	 *         query string
	 * 
	 * @see HttpUtils#getRequestURL
	 * 
	 */

	//public String getRequestURI();

	/**
	 * 
	 * Returns the current <code>HttpSession</code> associated with this request or, if there is no
	 * current session and <code>create</code> is true, returns a new session.
	 * 
	 * <p>
	 * If <code>create</code> is <code>false</code> and the request has no valid
	 * <code>HttpSession</code>, this method returns <code>null</code>.
	 * 
	 * <p>
	 * To make sure the session is properly maintained, you must call this method before the
	 * response is committed. If the container is using cookies to maintain session integrity and is
	 * asked to create a new session when the response is committed, an IllegalStateException is
	 * thrown.
	 * 
	 * 
	 * 
	 * 
	 * @param create
	 *            <code>true</code> to create a new session for this request if necessary;
	 *            <code>false</code> to return <code>null</code> if there's no current session
	 * 
	 * 
	 * @return the <code>HttpSession</code> associated with this request or <code>null</code> if
	 *         <code>create</code> is <code>false</code> and the request has no valid session
	 * 
	 * @see #getSession()
	 * 
	 * 
	 */

	public ISession getSession( boolean create );

	/**
	 * 
	 * Returns the current session associated with this request, or if the request does not have a
	 * session, creates one.
	 * 
	 * @return the <code>HttpSession</code> associated with this request
	 * 
	 * @see #getSession(boolean)
	 * 
	 */

	public ISession getSession();

	/**
	 * 
	 * Checks whether the requested session ID is still valid.
	 * 
	 * @return <code>true</code> if this request has an id for a valid session in the current
	 *         session context; <code>false</code> otherwise
	 * 
	 * @see #getRequestedSessionId
	 * @see #getSession
	 * @see HttpSessionContext
	 * 
	 */

	public boolean isRequestedSessionIdValid();

	/**
	 * 
	 * Checks whether the requested session ID came in as a cookie.
	 * 
	 * @return <code>true</code> if the session ID came in as a cookie; otherwise,
	 *         <code>false</code>
	 * 
	 * 
	 * @see #getSession
	 * 
	 */

	public boolean isRequestedSessionIdFromCookie();

	/**
	 * 
	 * Checks whether the requested session ID came in as part of the request URL.
	 * 
	 * @return <code>true</code> if the session ID came in as part of a URL; otherwise,
	 *         <code>false</code>
	 * 
	 * 
	 * @see #getSession
	 * 
	 */

	public boolean isRequestedSessionIdFromURL();

	
	public boolean isKeepAlive();
	
}
