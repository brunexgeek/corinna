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

package javax.bindlet;


import java.io.IOException;

import javax.bindlet.io.BindletOutputStream;
import javax.bindlet.io.ICloseable;


/**
 * Defines an object to assist a servlet in sending a response to the client. The servlet container
 * creates a <code>ServletResponse</code> object and passes it as an argument to the servlet's
 * <code>service</code> method.
 * 
 * <p>
 * To send binary data in a MIME body response, use the {@link ChunkedHttpOutputStream} returned by
 * {@link #getOutputStream}. To send character data, use the <code>PrintWriter</code> object
 * returned by {@link #getWriter}. To mix binary and text data, for example, to create a multipart
 * response, use a <code>ServletOutputStream</code> and manage the character sections manually.
 * 
 * <p>
 * The charset for the MIME body response can be specified explicitly using the
 * {@link #setCharacterEncoding} and {@link #setContentType} methods, or implicitly using the
 * {@link #setLocale} method. Explicit specifications take precedence over implicit specifications.
 * If no charset is specified, ISO-8859-1 will be used. The <code>setCharacterEncoding</code>,
 * <code>setContentType</code>, or <code>setLocale</code> method must be called before
 * <code>getWriter</code> and before committing the response for the character encoding to be used.
 * 
 * <p>
 * See the Internet RFCs such as <a href="http://www.ietf.org/rfc/rfc2045.txt"> RFC 2045</a> for
 * more information on MIME. Protocols such as SMTP and HTTP define profiles of MIME, and those
 * standards are still evolving.
 * 
 * @author Various
 * @version $Version$
 * 
 * @see ChunkedHttpOutputStream
 * 
 */
public interface IBindletResponse  extends ICloseable
{

	/**
	 * Returns a {@link ChunkedHttpOutputStream} suitable for writing binary data in the response.
	 * The servlet container does not encode the binary data.
	 * 
	 * <p>
	 * Calling flush() on the ServletOutputStream commits the response.
	 * 
	 * Either this method or {@link #getWriter} may be called to write the body, not both.
	 * 
	 * @return a {@link ChunkedHttpOutputStream} for writing binary data
	 * 
	 * @exception IllegalStateException
	 *                if the <code>getWriter</code> method has been called on this response
	 * 
	 * @exception IOException
	 *                if an input or output exception occurred
	 * 
	 * @see #getWriter
	 * 
	 */
	public BindletOutputStream getOutputStream() throws IOException;

	/**
	 * Clears any data that exists in the buffer as well as the status code and headers. If the
	 * response has been committed, this method throws an <code>IllegalStateException</code>.
	 * 
	 * @exception IllegalStateException
	 *                if the response has already been committed
	 * 
	 * @see #setBufferSize
	 * @see #getBufferSize
	 * @see #flushBuffer
	 * @see #isCommitted
	 * 
	 */

	public void reset();
	
}
