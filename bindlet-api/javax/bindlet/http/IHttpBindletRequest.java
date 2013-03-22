/*
 * Copyright 2011-2012 Bruno Ribeiro
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

import java.net.InetSocketAddress;

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
public interface IHttpBindletRequest extends IBindletRequest
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


	/**
	 * Returns the value of the specified request header as a <code>long</code> value that
	 * represents a <code>Date</code> object. Use this method with headers that contain dates, such
	 * as <code>If-Modified-Since</code>.
	 * 
	 * <p>
	 * The date is returned as the number of milliseconds since January 1, 1970 GMT. The header name
	 * is case insensitive.
	 * </p>
	 * 
	 * <p>
	 * If the request did not have a header of the specified name, this method returns -1. If the
	 * header can't be converted to a date, the method throws an
	 * <code>IllegalArgumentException</code>.
	 * </p>
	 * 
	 * @param name
	 *            a <code>String</code> specifying the name of the header
	 * 
	 * @return a <code>long</code> value representing the date specified in the header expressed as
	 *         the number of milliseconds since January 1, 1970 GMT, or -1 if the named header was
	 *         not included with the request
	 * 
	 * @exception IllegalArgumentException
	 *                if the header value can't be converted to a date
	 */
	public long getDateHeader( String name );

	/**
	 * Returns the value of the specified request header as a <code>String</code>. If the request
	 * did not include a header of the specified name, this method returns <code>null</code>. If
	 * there are multiple headers with the same name, this method returns the first head in the
	 * request. The header name is case insensitive. You can use this method with any request
	 * header.
	 * 
	 * @param name
	 *            a <code>String</code> specifying the header name
	 * 
	 * @return a <code>String</code> containing the value of the requested header, or
	 *         <code>null</code> if the request does not have a header of that name
	 */
	public String getHeader( String name );

	/**
	 * Returns all the values of the specified request header as an array of <code>String</code>
	 * objects.
	 * 
	 * <p>
	 * Some headers, such as <code>Accept-Language</code> can be sent by clients as several headers
	 * each with a different value rather than sending the header as a comma separated list.
	 * </p>
	 * 
	 * <p>
	 * If the request did not include any headers of the specified name, this method returns an
	 * empty array. The header name is case insensitive. You can use this method with any request
	 * header.
	 * </p>
	 * 
	 * @param name
	 *            a <code>String</code> specifying the header name
	 * 
	 * @return an array of <code>String</code> objects containing the values of the requested
	 *         header. If the request does not have any headers of that name return an empty
	 *         enumeration. If the container does not allow access to header information, return
	 *         null
	 */
	public String[] getHeaders( String name );

	/**
	 * Returns an array of <code>String</code> object containing all the header names this request
	 * contains. If the request has no headers, this method returns an empty array.
	 * 
	 * <p>
	 * Some servlet containers do not allow servlets to access headers using this method, in which
	 * case this method returns <code>null</code>
	 * 
	 * @return an enumeration of all the header names sent with this request; if the request has no
	 *         headers, an empty enumeration; if the servlet container does not allow servlets to
	 *         use this method, <code>null</code>
	 */
	public String[] getHeaderNames();

	/**
	 * 
	 * Returns the value of the specified request header as an <code>int</code>. If the request does
	 * not have a header of the specified name, this method returns -1. If the header cannot be
	 * converted to an integer, this method throws a <code>NumberFormatException</code>.
	 * 
	 * <p>
	 * The header name is case insensitive.
	 * 
	 * @param name
	 *            a <code>String</code> specifying the name of a request header
	 * 
	 * @return an integer expressing the value of the request header or -1 if the request doesn't
	 *         have a header of this name
	 * 
	 * @exception NumberFormatException
	 *                If the header value can't be converted to an <code>int</code>
	 */
	// public int getIntHeader( String name );

	/**
	 * 
	 * Returns the name of the HTTP method with which this request was made, for example, GET, POST,
	 * or PUT. Same as the value of the CGI variable REQUEST_METHOD.
	 * 
	 * @return a <code>String</code> specifying the name of the method with which this request was
	 *         made
	 * 
	 */
	public HttpMethod getHttpMethod();

	/**
	 * Returns the portion of the request URI that indicates the context of the request. The context
	 * path always comes first in a request URI. The path starts with a "/" character but does not
	 * end with a "/" character. For bindlets in the root context, this method returns "/".
	 * 
	 * @return a <code>String</code> specifying the portion of the request URI that indicates the
	 *         context of the request
	 */
	public String getContextPath();

	/**
	 * Returns the query string that is contained in the request URI. If the URI does not have a
	 * query string, this method returns <code>null</code>.
	 * 
	 * @return a <code>String</code> containing the query string or <code>null</code> if the URI
	 *         contains no query string.
	 */
	public String getQueryString();
		
	/**
	 * Reconstructs the URL the client used to make the request. The returned URL contains a
	 * protocol, server name, port number, context path, bindlet path and resource path, but it does
	 * not include the query string.
	 * 
	 * @return a <code>Stringr</code> containing the URL
	 */
	public String getRequestURL();

	/**
	 * Reconstructs the URI the client used to make the request. The returned URI contains a
	 * protocol, server name, port number, context path, bindlet path, resource path and query
	 * string (if any).
	 * 
	 * @return a <code>String</code> containing the URI
	 */
	public String getRequestURI();

	/**
	 * Reconstructs the URN the client used to make the request. The returned URN contains the
	 * context path, bindlet path and resource path.
	 * 
	 * @return a <code>String</code> object containing the URN
	 */
	public String getRequestURN();

	/**
	 * Returns the part of this request's URI that identify the bindlet. This path starts with a "/"
	 * character and includes the bindlet pattern match (can be a bindlet name or another
	 * <code>String</code>). For example, if the bindlet pattern was "/foo/*" the bindlet path will
	 * be "/foo".
	 * 
	 * <p>
	 * This method will return an empty string if the bindlet used to process this request was
	 * matched using the "/*" pattern.
	 * </p>
	 * 
	 * @return a <code>String</code> containing the name or path of the servlet being called, as
	 *         specified in the request URL, decoded, or an empty string if the servlet used to
	 *         process the request is matched using the "/*" pattern.
	 * 
	 */
	public String getBindletPath();

	/**
	 * Returns the resource path of this request. The resource path follows the bindlet path and
	 * precedes the query string. This path will start with a "/" character and can be any number of
	 * the slash separated values.
	 * 
	 * <p>
	 * This method returns an empty <code>String</code> if there was no resource path.
	 * </p>
	 * 
	 * @return a <code>String</code> specifying the resource extra path or an empty
	 *         <code>String</code> if the URI does not have any resource path
	 * 
	 */
	public String getResourcePath();

	/**
	 * Returns the name of the character encoding (MIME charset) used for the body sent in this
	 * response. The character encoding may have been specified explicitly using the
	 * {@link #setCharacterEncoding} or {@link #setContentType} methods, or implicitly using the
	 * {@link #setLocale} method. Explicit specifications take precedence over implicit
	 * specifications. Calls made to these methods after <code>getOutputStream</code> has been
	 * called or after the response has been committed have no effect on the character encoding. If
	 * no character encoding has been specified, <code>ISO-8859-1</code> is returned.
	 * <p>
	 * See RFC 2047 (http://www.ietf.org/rfc/rfc2047.txt) for more information about character
	 * encoding and MIME.
	 * 
	 * @return a <code>String</code> specifying the name of the character encoding, for example,
	 *         <code>UTF-8</code>
	 */
	public String getCharacterEncoding();

	/**
	 * Returns the content type used for the MIME body sent in this response. The content type
	 * proper must have been specified using {@link #setContentType} before the response is
	 * committed. If no content type has been specified, this method returns null. If a content type
	 * has been specified and a character encoding has been explicitly or implicitly specified as
	 * described in {@link #getCharacterEncoding}, the charset parameter is included in the string
	 * returned. If no character encoding has been specified, the charset parameter is omitted.
	 * 
	 * @return a <code>String</code> specifying the content type, for example,
	 *         <code>text/html; charset=UTF-8</code>, or null
	 * 
	 * @since 2.4
	 */
	public String getContentType();

	public long getContentLength();

	/**
	 * Returns a boolean indicating whether the named response header has already been set.
	 * 
	 * @param name
	 *            the header name
	 * @return <code>true</code> if the named response header has already been set;
	 *         <code>false</code> otherwise
	 */
	public boolean containsHeader( String name );


	/**
	 * Returns the value of a request parameter as a <code>String</code>, or <code>null</code> if
	 * the parameter does not exist. Request parameters are extra information sent with the request.
	 * For HTTP servlets, parameters are contained in the query string or posted form data.
	 * 
	 * <p>
	 * You should only use this method when you are sure the parameter has only one value. If the
	 * parameter might have more than one value, use {@link #getParameterValues}.
	 * 
	 * <p>
	 * If you use this method with a multivalued parameter, the value returned is equal to the first
	 * value in the array returned by <code>getParameterValues</code>.
	 * 
	 * <p>
	 * If the parameter data was sent in the request body, such as occurs with an HTTP POST request,
	 * then reading the body directly via {@link #getInputStream} or {@link #getReader} can
	 * interfere with the execution of this method.
	 * 
	 * @param name
	 *            a <code>String</code> specifying the name of the parameter
	 * 
	 * @return a <code>String</code> representing the single value of the parameter
	 * 
	 * @see #getParameterValues
	 * 
	 */
	public String getParameter( String name );

	/**
	 * 
	 * Returns an <code>Enumeration</code> of <code>String</code> objects containing the names of
	 * the parameters contained in this request. If the request has no parameters, the method
	 * returns an empty <code>Enumeration</code>.
	 * 
	 * @return an <code>Enumeration</code> of <code>String</code> objects, each <code>String</code>
	 *         containing the name of a request parameter; or an empty <code>Enumeration</code> if
	 *         the request has no parameters
	 * 
	 */
	public String[] getParameterNames();

	/**
	 * Returns an array of <code>String</code> objects containing all of the values the given
	 * request parameter has, or <code>null</code> if the parameter does not exist.
	 * 
	 * <p>
	 * If the parameter has a single value, the array has a length of 1.
	 * 
	 * @param name
	 *            a <code>String</code> containing the name of the parameter whose value is
	 *            requested
	 * 
	 * @return an array of <code>String</code> objects containing the parameter's values
	 * 
	 * @see #getParameter
	 * 
	 */
	public String[] getParameterValues( String name );

	public boolean isKeepAlive();
	
	public String getUserName();
	
	public void setUserName( String userName );
	
	/**
	 * Returns the IP address of the remote host. The remote host of a request is the
	 * client that send the request for the connector.
	 * 
	 * @return
	 */
	public InetSocketAddress getRemoteAddress();
	
}
