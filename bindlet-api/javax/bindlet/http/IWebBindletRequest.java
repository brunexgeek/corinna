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


import javax.bindlet.IBindletRequest;


public interface IWebBindletRequest extends IBindletRequest
{

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
	
}
