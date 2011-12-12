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

package javax.bindlet;


import java.io.BufferedReader;
import java.io.IOException;


/**
 * Defines an object to provide client request information to a servlet. The servlet container
 * creates a <code>ServletRequest</code> object and passes it as an argument to the servlet's
 * <code>service</code> method.
 * 
 * <p>
 * A <code>ServletRequest</code> object provides data including parameter name and values,
 * attributes, and an input stream. Interfaces that extend <code>ServletRequest</code> can provide
 * additional protocol-specific data (for example, HTTP data is provided by
 * {@link corinna.service.bindlet.http.IHttpBindletRequest}.
 * 
 * @author Various
 * @version $Version$
 * 
 * @see corinna.service.bindlet.http.IHttpBindletRequest
 * 
 */
public interface IBindletRequest
{

	/**
	 * Retrieves the body of the request as binary data using a {@link HttpBindletInputStream}.
	 * Either this method or {@link #getReader} may be called to read the body, not both.
	 * 
	 * @return a {@link HttpBindletInputStream} object containing the body of the request
	 * 
	 * @exception IllegalStateException
	 *                if the {@link #getReader} method has already been called for this request
	 * 
	 * @exception IOException
	 *                if an input or output exception occurred
	 * 
	 */

	public BindletInputStream getInputStream() throws IOException;

	/**
	 * Returns the name and version of the protocol the request uses in the form
	 * <i>protocol/majorVersion.minorVersion</i>, for example, HTTP/1.1. For HTTP servlets, the
	 * value returned is the same as the value of the CGI variable <code>SERVER_PROTOCOL</code>.
	 * 
	 * @return a <code>String</code> containing the protocol name and version number
	 * 
	 */
	public String getProtocol();

	/**
	 * Returns the name of the scheme used to make this request, for example, <code>http</code>,
	 * <code>https</code>, or <code>ftp</code>. Different schemes have different rules for
	 * constructing URLs, as noted in RFC 1738.
	 * 
	 * @return a <code>String</code> containing the name of the scheme used to make this request
	 * 
	 */
	public String getScheme();

	/**
	 * Returns the host name of the server to which the request was sent. It is the value of the
	 * part before ":" in the <code>Host</code> header value, if any, or the resolved server name,
	 * or the server IP address.
	 * 
	 * @return a <code>String</code> containing the name of the server
	 */

	public String getServerName();

	/**
	 * Returns the port number to which the request was sent. It is the value of the part after ":"
	 * in the <code>Host</code> header value, if any, or the server port where the client connection
	 * was accepted on.
	 * 
	 * @return an integer specifying the port number
	 * 
	 */

	public int getServerPort();

	/**
	 * 
	 * Returns a boolean indicating whether this request was made using a secure channel, such as
	 * HTTPS.
	 * 
	 * 
	 * @return a boolean indicating if the request was made using a secure channel
	 * 
	 */

	public boolean isSecure();

}
