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


import javax.bindlet.IBindletResponse;


/**
 * 
 * Extends the {@link IBindletResponse} interface to provide HTTP-specific functionality in sending
 * a response. For example, it has methods to access HTTP headers and cookies.
 * 
 * <p>
 * The servlet container creates an <code>HttpServletResponse</code> object and passes it as an
 * argument to the servlet's service methods (<code>doGet</code>, <code>doPost</code>, etc).
 * 
 * 
 * @author Various
 * @version $Version$
 * 
 * @see corinna.service.bindlet.IBindletResponse
 * 
 */
// TODO: mover para 'javax.bindlet.http'
public interface IHttpBindletResponse extends IWebBindletResponse
{

	/**
	 * Adds the specified cookie to the response. This method can be called multiple times to set
	 * more than one cookie.
	 * 
	 * @param cookie
	 *            the Cookie to return to the client
	 * 
	 */
	public void addCookie( Cookie cookie );

	/**
	 * Encodes the specified URL by including the session ID in it, or, if encoding is not needed,
	 * returns the URL unchanged. The implementation of this method includes the logic to determine
	 * whether the session ID needs to be encoded in the URL. For example, if the browser supports
	 * cookies, or session tracking is turned off, URL encoding is unnecessary.
	 * 
	 * <p>
	 * For robust session tracking, all URLs emitted by a servlet should be run through this method.
	 * Otherwise, URL rewriting cannot be used with browsers which do not support cookies.
	 * 
	 * @param url
	 *            the url to be encoded.
	 * @return the encoded URL if encoding is needed; the unchanged URL otherwise.
	 */
	public String encodeURL( String url );

	/**
	 * Encodes the specified URL for use in the <code>sendRedirect</code> method or, if encoding is
	 * not needed, returns the URL unchanged. The implementation of this method includes the logic
	 * to determine whether the session ID needs to be encoded in the URL. Because the rules for
	 * making this determination can differ from those used to decide whether to encode a normal
	 * link, this method is separated from the <code>encodeURL</code> method.
	 * 
	 * <p>
	 * All URLs sent to the <code>HttpServletResponse.sendRedirect</code> method should be run
	 * through this method. Otherwise, URL rewriting cannot be used with browsers which do not
	 * support cookies.
	 * 
	 * @param url
	 *            the url to be encoded.
	 * @return the encoded URL if encoding is needed; the unchanged URL otherwise.
	 * 
	 * @see #sendRedirect
	 * @see #encodeUrl
	 */
	public String encodeRedirectURL( String url );

}
