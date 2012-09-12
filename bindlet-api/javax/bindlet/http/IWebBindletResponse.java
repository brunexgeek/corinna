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


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;

import javax.bindlet.IBindletResponse;
import javax.bindlet.io.ICloseable;


public interface IWebBindletResponse extends ICloseable, IBindletResponse
{

	public boolean isChunked();

	public void setChunked( boolean value );

	/**
	 * Returns the name of the character encoding (MIME charset) used for the body sent in this
	 * response. The character encoding may have been specified explicitly using the
	 * {@link #setCharacterEncoding} or {@link #setContentType} methods, or implicitly using the
	 * {@link #setLocale} method. Explicit specifications take precedence over implicit
	 * specifications. Calls made to these methods after <code>getOutputStream</code> has been called or
	 * after the response has been committed have no effect on the character encoding. If no
	 * character encoding has been specified, <code>ISO-8859-1</code> is returned.
	 * <p>
	 * See RFC 2047 (http://www.ietf.org/rfc/rfc2047.txt) for more information about character
	 * encoding and MIME.
	 * 
	 * @return a <code>String</code> specifying the name of the character encoding, for example,
	 *         <code>UTF-8</code>
	 * 
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

	/**
	 * Sets the character encoding (MIME charset) of the response being sent to the client, for
	 * example, to UTF-8. If the character encoding has already been set by {@link #setContentType}
	 * or {@link #setLocale}, this method overrides it. Calling {@link #setContentType} with the
	 * <code>String</code> of <code>text/html</code> and calling this method with the
	 * <code>String</code> of <code>UTF-8</code> is equivalent with calling
	 * <code>setContentType</code> with the <code>String</code> of
	 * <code>text/html; charset=UTF-8</code>.
	 * <p>
	 * This method can be called repeatedly to change the character encoding. This method has no
	 * effect if it is called after <code>getWriter</code> has been called or after the response has
	 * been committed.
	 * <p>
	 * Containers must communicate the character encoding used for the servlet response's writer to
	 * the client if the protocol provides a way for doing so. In the case of HTTP, the character
	 * encoding is communicated as part of the <code>Content-Type</code> header for text media
	 * types. Note that the character encoding cannot be communicated via HTTP headers if the
	 * servlet does not specify a content type; however, it is still used to encode text written via
	 * the servlet response's writer.
	 * 
	 * @param charset
	 *            a String specifying only the character set defined by IANA Character Sets
	 *            (http://www.iana.org/assignments/character-sets)
	 * 
	 * @see #setContentType #setLocale
	 * 
	 * @since 2.4
	 * 
	 */
	public void setCharacterEncoding( String charset );

	/**
	 * Sets the length of the content body in the response In HTTP servlets, this method sets the
	 * HTTP Content-Length header.
	 * 
	 * 
	 * @param len
	 *            an long specifying the length of the content being returned to the client; sets
	 *            the Content-Length header
	 * 
	 */
	public void setContentLength( long length );

	public long getContentLength();

	/**
	 * Sets the content type of the response being sent to the client, if the response has not been
	 * committed yet. The given content type may include a character encoding specification, for
	 * example, <code>text/html;charset=UTF-8</code>. The response's character encoding is only set
	 * from the given content type if this method is called before <code>getWriter</code> is called.
	 * <p>
	 * This method may be called repeatedly to change content type and character encoding. This
	 * method has no effect if called after the response has been committed. It does not set the
	 * response's character encoding if it is called after <code>getWriter</code> has been called or
	 * after the response has been committed.
	 * <p>
	 * Containers must communicate the content type and the character encoding used for the servlet
	 * response's writer to the client if the protocol provides a way for doing so. In the case of
	 * HTTP, the <code>Content-Type</code> header is used.
	 * 
	 * @param type
	 *            a <code>String</code> specifying the MIME type of the content
	 * 
	 * @see #setLocale
	 * @see #setCharacterEncoding
	 * @see #getOutputStream
	 * @see #getWriter
	 * 
	 */
	public void setContentType( String type );

	public void setContentType( String contentType, String charset );

	/**
	 * Sets the locale of the response, if the response has not been committed yet. It also sets the
	 * response's character encoding appropriately for the locale, if the character encoding has not
	 * been explicitly set using {@link #setContentType} or {@link #setCharacterEncoding},
	 * <code>getWriter</code> hasn't been called yet, and the response hasn't been committed yet. If
	 * the deployment descriptor contains a <code>locale-encoding-mapping-list</code> element, and
	 * that element provides a mapping for the given locale, that mapping is used. Otherwise, the
	 * mapping from locale to character encoding is container dependent.
	 * <p>
	 * This method may be called repeatedly to change locale and character encoding. The method has
	 * no effect if called after the response has been committed. It does not set the response's
	 * character encoding if it is called after {@link #setContentType} has been called with a
	 * charset specification, after {@link #setCharacterEncoding} has been called, after
	 * <code>getWriter</code> has been called, or after the response has been committed.
	 * <p>
	 * Containers must communicate the locale and the character encoding used for the servlet
	 * response's writer to the client if the protocol provides a way for doing so. In the case of
	 * HTTP, the locale is communicated via the <code>Content-Language</code> header, the character
	 * encoding as part of the <code>Content-Type</code> header for text media types. Note that the
	 * character encoding cannot be communicated via HTTP headers if the servlet does not specify a
	 * content type; however, it is still used to encode text written via the servlet response's
	 * writer.
	 * 
	 * @param loc
	 *            the locale of the response
	 * 
	 * @see #getLocale
	 * @see #setContentType
	 * @see #setCharacterEncoding
	 * 
	 */
	public void setLocale( Locale locale );

	/**
	 * Returns the locale specified for this response using the {@link #setLocale} method. Calls
	 * made to <code>setLocale</code> after the response is committed have no effect. If no locale
	 * has been specified, the container's default locale is returned.
	 * 
	 * @see #setLocale
	 * 
	 */
	public Locale getLocale();

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
	 * Sends an error response to the client using the specified status. The server defaults to
	 * creating the response to look like an HTML-formatted server error page containing the
	 * specified message, setting the content type to "text/html", leaving cookies and other headers
	 * unmodified.
	 * 
	 * If an error-page declaration has been made for the web application corresponding to the
	 * status code passed in, it will be served back in preference to the suggested msg parameter.
	 * 
	 * <p>
	 * If the response has already been committed, this method throws an IllegalStateException.
	 * After using this method, the response should be considered to be committed and should not be
	 * written to.
	 * 
	 * @param sc
	 *            the error status code
	 * @param msg
	 *            the descriptive message
	 * @exception IOException
	 *                If an input or output exception occurs
	 * @exception IllegalStateException
	 *                If the response was committed
	 */
	public void sendError( HttpStatus status, String message ) throws IOException;

	/**
	 * Sends an error response to the client using the specified status code and clearing the
	 * buffer.
	 * <p>
	 * If the response has already been committed, this method throws an IllegalStateException.
	 * After using this method, the response should be considered to be committed and should not be
	 * written to.
	 * 
	 * @param sc
	 *            the error status code
	 * @exception IOException
	 *                If an input or output exception occurs
	 * @exception IllegalStateException
	 *                If the response was committed before this method call
	 */
	public void sendError( HttpStatus status ) throws IOException;

	/**
	 * Sends a temporary redirect response to the client using the specified redirect location URL.
	 * This method can accept relative URLs; the servlet container must convert the relative URL to
	 * an absolute URL before sending the response to the client. If the location is relative
	 * without a leading '/' the container interprets it as relative to the current request URI. If
	 * the location is relative with a leading '/' the container interprets it as relative to the
	 * servlet container root.
	 * 
	 * <p>
	 * If the response has already been committed, this method throws an IllegalStateException.
	 * After using this method, the response should be considered to be committed and should not be
	 * written to.
	 * 
	 * @param location
	 *            the redirect location URL
	 * @exception IOException
	 *                If an input or output exception occurs
	 * @exception IllegalStateException
	 *                If the response was committed or if a partial URL is given and cannot be
	 *                converted into a valid URL
	 */
	public void sendRedirect( String location ) throws IOException;

	/**
	 * 
	 * Sets a response header with the given name and date-value. The date is specified in terms of
	 * milliseconds since the epoch. If the header had already been set, the new value overwrites
	 * the previous one. The <code>containsHeader</code> method can be used to test for the presence
	 * of a header before setting its value.
	 * 
	 * @param name
	 *            the name of the header to set
	 * @param date
	 *            the assigned date value
	 * 
	 * @see #containsHeader
	 * @see #addDateHeader
	 */
	public void setDateHeader( String name, Date date );

	public void setDateHeader( String name, long date );
	
	/**
	 * 
	 * Adds a response header with the given name and date-value. The date is specified in terms of
	 * milliseconds since the epoch. This method allows response headers to have multiple values.
	 * 
	 * @param name
	 *            the name of the header to set
	 * @param date
	 *            the additional date value
	 * 
	 * @see #setDateHeader
	 */
	public void addDateHeader( String name, Date date );

	public void addDateHeader( String name, long date );
	
	/**
	 * 
	 * Sets a response header with the given name and value. If the header had already been set, the
	 * new value overwrites the previous one. The <code>containsHeader</code> method can be used to
	 * test for the presence of a header before setting its value.
	 * 
	 * @param name
	 *            the name of the header
	 * @param value
	 *            the header value If it contains octet string, it should be encoded according to
	 *            RFC 2047 (http://www.ietf.org/rfc/rfc2047.txt)
	 * 
	 * @see #containsHeader
	 * @see #addHeader
	 */
	public void setHeader( String name, Object value );

	/**
	 * Adds a response header with the given name and value. This method allows response headers to
	 * have multiple values.
	 * 
	 * @param name
	 *            the name of the header
	 * @param value
	 *            the additional header value If it contains octet string, it should be encoded
	 *            according to RFC 2047 (http://www.ietf.org/rfc/rfc2047.txt)
	 * 
	 * @see #setHeader
	 */
	public void addHeader( String name, Object value );

	/**
	 * Sets the status code for this response. This method is used to set the return status code
	 * when there is no error (for example, for the status codes SC_OK or SC_MOVED_TEMPORARILY). If
	 * there is an error, and the caller wishes to invoke an error-page defined in the web
	 * application, the <code>sendError</code> method should be used instead.
	 * <p>
	 * The container clears the buffer and sets the Location header, preserving cookies and other
	 * headers.
	 * 
	 * @param sc
	 *            the status code
	 * 
	 * @see #sendError
	 */
	public void setStatus( HttpStatus sc );

	public void setCharacterEncoding( Charset charset );

	public void setContentType( String contentType, Charset charset );
	
}
