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
import java.lang.reflect.Method;

import javax.bindlet.Bindlet;
import javax.bindlet.IBindlet;
import javax.bindlet.IBindletAuthenticator;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.io.HttpBindletOutputStream;


/**
 * <p>
 * Provides an abstract class to create an HTTP bindlet. A subclass of <code>HttpBindlet</code> must
 * override at least one of these methods:
 * </p>
 * 
 * <ul>
 * <li> <code>doGet</code>, for HTTP GET requests
 * <li> <code>doPost</code>, for HTTP POST requests
 * <li> <code>doPut</code>, for HTTP PUT requests
 * <li> <code>doDelete</code>, for HTTP DELETE requests
 * </ul>
 * 
 * <p>
 * Additionally, all bindlets must implements the <code>init</code> and <code>destroy</code>
 * methods, to manage resources that are held for the life of the bindet, and
 * <code>getServletInfo</code>, which the bindlet uses to provide information about itself
 * </p>
 * 
 * <p>
 * HTTP bindlets have the <code>service</code> method that handle requests for all HTTP verbs by
 * dispatching them to their respective handler methods (the <code>do</code><i>XXX</i> methods
 * listed above). Likewise, there's almost no reason to override the <code>process</code>,
 * <code>doOptions</code> and <code>doTrace</code> methods.
 * 
 * <p>
 * Bindlets typically run on multithreaded servers, so be aware that a bindlet must handle
 * concurrent requests and be careful to synchronize access to shared resources. Shared resources
 * include in-memory data such as instance or class variables and external objects such as files,
 * database connections, and network connections.
 * </p>
 * 
 * <p>
 * Bindlets can be also designed to a specific model. The bindlet model is defined through the
 * {@link BindletModel} annotation (ie. a bindlet can be designed for <code>stateless</code> model
 * for the container use only one instance of it, like a singleton).
 * </p>
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 * @see {@link Bindlet}
 */
@SuppressWarnings("serial")
public abstract class HttpBindlet extends Bindlet<IHttpBindletRequest, IHttpBindletResponse>
	implements java.io.Serializable
{

	private static final String HEADER_IFMODSINCE = "If-Modified-Since";

	private static final String HEADER_LASTMOD = "Last-Modified";

	private static final String INIT_PARAM_RESTRICTED = "isRestricted";

	private IBindletAuthenticator authenticator = null;

	/**
	 * Does nothing, because this is an abstract class.
	 * 
	 * @throws BindletException
	 * 
	 */
	public HttpBindlet() throws BindletException
	{
		super();
	}

	/**
	 * 
	 * Called by the server (via the <code>service</code> method) to allow a servlet to handle a GET
	 * request.
	 * 
	 * <p>
	 * Overriding this method to support a GET request also automatically supports an HTTP HEAD
	 * request. A HEAD request is a GET request that returns no body in the response, only the
	 * request header fields.
	 * 
	 * <p>
	 * When overriding this method, read the request data, write the response headers, get the
	 * response's writer or output stream object, and finally, write the response data. It's best to
	 * include content type and encoding. When using a <code>PrintWriter</code> object to return the
	 * response, set the content type before accessing the <code>PrintWriter</code> object.
	 * 
	 * <p>
	 * The servlet container must write the headers before committing the response, because in HTTP
	 * the headers must be sent before the response body.
	 * 
	 * <p>
	 * Where possible, set the Content-Length header (with the
	 * {@link corinna.service.bindlet.IBindletResponse#setContentLength} method), to allow the
	 * servlet container to use a persistent connection to return its response to the client,
	 * improving performance. The content length is automatically set if the entire response fits
	 * inside the response buffer.
	 * 
	 * <p>
	 * When using HTTP 1.1 chunked encoding (which means that the response has a Transfer-Encoding
	 * header), do not set the Content-Length header.
	 * 
	 * <p>
	 * The GET method should be safe, that is, without any side effects for which users are held
	 * responsible. For example, most form queries have no side effects. If a client request is
	 * intended to change stored data, the request should use some other HTTP method.
	 * 
	 * <p>
	 * The GET method should also be idempotent, meaning that it can be safely repeated. Sometimes
	 * making a method safe also makes it idempotent. For example, repeating queries is both safe
	 * and idempotent, but buying a product online or modifying data is neither safe nor idempotent.
	 * 
	 * <p>
	 * If the request is incorrectly formatted, <code>doGet</code> returns an HTTP "Bad Request"
	 * message.
	 * 
	 * 
	 * @param request
	 *            an {@link IHttpBindletRequest} object that contains the request the client has
	 *            made of the servlet
	 * 
	 * @param response
	 *            an {@link IHttpBindletResponse} object that contains the response the servlet
	 *            sends to the client
	 * 
	 * @exception IOException
	 *                if an input or output error is detected when the servlet handles the GET
	 *                request
	 * 
	 * @exception BindletException
	 *                if the request for the GET could not be handled
	 * 
	 * 
	 * @see corinna.service.bindlet.IBindletResponse#setContentType
	 * 
	 */
	protected void doGet( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		String protocol = request.getProtocol();
		if (protocol.endsWith("1.1"))
			response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
		else
			response.sendError(HttpStatus.BAD_REQUEST);
	}

	/**
	 * 
	 * Returns the time the <code>HttpServletRequest</code> object was last modified, in
	 * milliseconds since midnight January 1, 1970 GMT. If the time is unknown, this method returns
	 * a negative number (the default).
	 * 
	 * <p>
	 * Servlets that support HTTP GET requests and can quickly determine their last modification
	 * time should override this method. This makes browser and proxy caches work more effectively,
	 * reducing the load on server and network resources.
	 * 
	 * 
	 * @param request
	 *            the <code>HttpServletRequest</code> object that is sent to the servlet
	 * 
	 * @return a <code>long</code> integer specifying the time the <code>HttpServletRequest</code>
	 *         object was last modified, in milliseconds since midnight, January 1, 1970 GMT, or -1
	 *         if the time is not known
	 * 
	 */
	protected long getLastModified( IHttpBindletRequest request )
	{
		return -1;
	}

	/**
	 * 
	 * 
	 * <p>
	 * Receives an HTTP HEAD request from the protected <code>service</code> method and handles the
	 * request. The client sends a HEAD request when it wants to see only the headers of a response,
	 * such as Content-Type or Content-Length. The HTTP HEAD method counts the output bytes in the
	 * response to set the Content-Length header accurately.
	 * 
	 * <p>
	 * If you override this method, you can avoid computing the response body and just set the
	 * response headers directly to improve performance. Make sure that the <code>doHead</code>
	 * method you write is both safe and idempotent (that is, protects itself from being called
	 * multiple times for one HTTP HEAD request).
	 * 
	 * <p>
	 * If the HTTP HEAD request is incorrectly formatted, <code>doHead</code> returns an HTTP
	 * "Bad Request" message.
	 * 
	 * 
	 * @param request
	 *            the request object that is passed to the servlet
	 * 
	 * @param response
	 *            the response object that the servlet uses to return the headers to the clien
	 * 
	 * @exception IOException
	 *                if an input or output error occurs
	 * 
	 * @exception BindletException
	 *                if the request for the HEAD could not be handled
	 */
	protected void doHead( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		/*
		 * NoBodyResponse response = new NoBodyResponse(response);
		 * 
		 * doGet(request, response); response.setContentLength();
		 */
		// TODO: create a new class similar the "NoBodyResponse"
	}

	/**
	 * 
	 * Called by the server (via the <code>service</code> method) to allow a servlet to handle a
	 * POST request.
	 * 
	 * The HTTP POST method allows the client to send data of unlimited length to the Web server a
	 * single time and is useful when posting information such as credit card numbers.
	 * 
	 * <p>
	 * When overriding this method, read the request data, write the response headers, get the
	 * response's writer or output stream object, and finally, write the response data. It's best to
	 * include content type and encoding. When using a <code>PrintWriter</code> object to return the
	 * response, set the content type before accessing the <code>PrintWriter</code> object.
	 * 
	 * <p>
	 * The servlet container must write the headers before committing the response, because in HTTP
	 * the headers must be sent before the response body.
	 * 
	 * <p>
	 * Where possible, set the Content-Length header (with the
	 * {@link corinna.service.bindlet.IBindletResponse#setContentLength} method), to allow the
	 * servlet container to use a persistent connection to return its response to the client,
	 * improving performance. The content length is automatically set if the entire response fits
	 * inside the response buffer.
	 * 
	 * <p>
	 * When using HTTP 1.1 chunked encoding (which means that the response has a Transfer-Encoding
	 * header), do not set the Content-Length header.
	 * 
	 * <p>
	 * This method does not need to be either safe or idempotent. Operations requested through POST
	 * can have side effects for which the user can be held accountable, for example, updating
	 * stored data or buying items online.
	 * 
	 * <p>
	 * If the HTTP POST request is incorrectly formatted, <code>doPost</code> returns an HTTP
	 * "Bad Request" message.
	 * 
	 * 
	 * @param request
	 *            an {@link IHttpBindletRequest} object that contains the request the client has
	 *            made of the servlet
	 * 
	 * @param response
	 *            an {@link IHttpBindletResponse} object that contains the response the servlet
	 *            sends to the client
	 * 
	 * @exception IOException
	 *                if an input or output error is detected when the servlet handles the request
	 * 
	 * @exception BindletException
	 *                if the request for the POST could not be handled
	 * 
	 * 
	 * @see corinna.service.bindlet.http.ChunkedHttpOutputStream
	 * @see corinna.service.bindlet.IBindletResponse#setContentType
	 * 
	 * 
	 */

	protected void doPost( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		String protocol = request.getProtocol();
		if (protocol.endsWith("1.1"))
			response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
		else
			response.sendError(HttpStatus.BAD_REQUEST);
	}

	/**
	 * Called by the server (via the <code>service</code> method) to allow a servlet to handle a PUT
	 * request.
	 * 
	 * The PUT operation allows a client to place a file on the server and is similar to sending a
	 * file by FTP.
	 * 
	 * <p>
	 * When overriding this method, leave intact any content headers sent with the request
	 * (including Content-Length, Content-Type, Content-Transfer-Encoding, Content-Encoding,
	 * Content-Base, Content-Language, Content-Location, Content-MD5, and Content-Range). If your
	 * method cannot handle a content header, it must issue an error message (HTTP 501 - Not
	 * Implemented) and discard the request. For more information on HTTP 1.1, see RFC 2616 <a
	 * href="http://www.ietf.org/rfc/rfc2616.txt"></a>.
	 * 
	 * <p>
	 * This method does not need to be either safe or idempotent. Operations that <code>doPut</code>
	 * performs can have side effects for which the user can be held accountable. When using this
	 * method, it may be useful to save a copy of the affected URL in temporary storage.
	 * 
	 * <p>
	 * If the HTTP PUT request is incorrectly formatted, <code>doPut</code> returns an HTTP
	 * "Bad Request" message.
	 * 
	 * 
	 * @param request
	 *            the {@link IHttpBindletRequest} object that contains the request the client made
	 *            of the servlet
	 * 
	 * @param response
	 *            the {@link IHttpBindletResponse} object that contains the response the servlet
	 *            returns to the client
	 * 
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is handling the PUT
	 *                request
	 * 
	 * @exception BindletException
	 *                if the request for the PUT cannot be handled
	 * 
	 */

	protected void doPut( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		String protocol = request.getProtocol();
		if (protocol.endsWith("1.1"))
			response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
		else
			response.sendError(HttpStatus.BAD_REQUEST);
	}

	/**
	 * 
	 * Called by the server (via the <code>service</code> method) to allow a servlet to handle a
	 * DELETE request.
	 * 
	 * The DELETE operation allows a client to remove a document or Web page from the server.
	 * 
	 * <p>
	 * This method does not need to be either safe or idempotent. Operations requested through
	 * DELETE can have side effects for which users can be held accountable. When using this method,
	 * it may be useful to save a copy of the affected URL in temporary storage.
	 * 
	 * <p>
	 * If the HTTP DELETE request is incorrectly formatted, <code>doDelete</code> returns an HTTP
	 * "Bad Request" message.
	 * 
	 * 
	 * @param request
	 *            the {@link IHttpBindletRequest} object that contains the request the client made
	 *            of the servlet
	 * 
	 * 
	 * @param response
	 *            the {@link IHttpBindletResponse} object that contains the response the servlet
	 *            returns to the client
	 * 
	 * 
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is handling the DELETE
	 *                request
	 * 
	 * @exception BindletException
	 *                if the request for the DELETE cannot be handled
	 * 
	 */

	protected void doDelete( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		String protocol = request.getProtocol();
		if (protocol.endsWith("1.1"))
			response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
		else
			response.sendError(HttpStatus.BAD_REQUEST);
	}

	private static Method[] getAllDeclaredMethods( Class<?> clazz )
	{

		if (clazz.equals(HttpBindlet.class))
		{
			return null;
		}

		Method[] parentMethods = getAllDeclaredMethods(clazz.getSuperclass());
		Method[] thisMethods = clazz.getDeclaredMethods();

		if ((parentMethods != null) && (parentMethods.length > 0))
		{
			Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
			System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
			System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);

			thisMethods = allMethods;
		}

		return thisMethods;
	}

	/**
	 * Called by the server (via the <code>service</code> method) to allow a servlet to handle a
	 * OPTIONS request.
	 * 
	 * The OPTIONS request determines which HTTP methods the server supports and returns an
	 * appropriate header. For example, if a servlet overrides <code>doGet</code>, this method
	 * returns the following header:
	 * 
	 * <p>
	 * <code>Allow: GET, HEAD, TRACE, OPTIONS</code>
	 * 
	 * <p>
	 * There's no need to override this method unless the servlet implements new HTTP methods,
	 * beyond those implemented by HTTP 1.1.
	 * 
	 * @param request
	 *            the {@link IHttpBindletRequest} object that contains the request the client made
	 *            of the servlet
	 * 
	 * 
	 * @param response
	 *            the {@link IHttpBindletResponse} object that contains the response the servlet
	 *            returns to the client
	 * 
	 * 
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is handling the OPTIONS
	 *                request
	 * 
	 * @exception BindletException
	 *                if the request for the OPTIONS cannot be handled
	 * 
	 */

	protected void doOptions( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		Method[] methods = getAllDeclaredMethods(this.getClass());

		boolean ALLOW_GET = false;
		boolean ALLOW_HEAD = false;
		boolean ALLOW_POST = false;
		boolean ALLOW_PUT = false;
		boolean ALLOW_DELETE = false;
		boolean ALLOW_TRACE = true;
		boolean ALLOW_OPTIONS = true;

		for (int i = 0; i < methods.length; i++)
		{
			Method m = methods[i];

			if (m.getName().equals("doGet"))
			{
				ALLOW_GET = true;
				ALLOW_HEAD = true;
			}
			if (m.getName().equals("doPost")) ALLOW_POST = true;
			if (m.getName().equals("doPut")) ALLOW_PUT = true;
			if (m.getName().equals("doDelete")) ALLOW_DELETE = true;

		}

		String allow = null;
		if (ALLOW_GET) if (allow == null) allow = HttpMethod.GET.toString();
		if (ALLOW_HEAD) if (allow == null)
			allow = HttpMethod.HEAD.toString();
		else
			allow += ", " + HttpMethod.HEAD.toString();
		if (ALLOW_POST) if (allow == null)
			allow = HttpMethod.POST.toString();
		else
			allow += ", " + HttpMethod.POST.toString();
		if (ALLOW_PUT) if (allow == null)
			allow = HttpMethod.PUT.toString();
		else
			allow += ", " + HttpMethod.PUT.toString();
		if (ALLOW_DELETE) if (allow == null)
			allow = HttpMethod.DELETE.toString();
		else
			allow += ", " + HttpMethod.DELETE.toString();
		if (ALLOW_TRACE) if (allow == null)
			allow = HttpMethod.TRACE.toString();
		else
			allow += ", " + HttpMethod.TRACE.toString();
		if (ALLOW_OPTIONS) if (allow == null)
			allow = HttpMethod.OPTIONS.toString();
		else
			allow += ", " + HttpMethod.OPTIONS.toString();

		response.setHeader("Allow", allow);
	}

	/**
	 * Called by the server (via the <code>service</code> method) to allow a servlet to handle a
	 * TRACE request.
	 * 
	 * A TRACE returns the headers sent with the TRACE request to the client, so that they can be
	 * used in debugging. There's no need to override this method.
	 * 
	 * 
	 * 
	 * @param request
	 *            the {@link IHttpBindletRequest} object that contains the request the client made
	 *            of the servlet
	 * 
	 * 
	 * @param response
	 *            the {@link IHttpBindletResponse} object that contains the response the servlet
	 *            returns to the client
	 * 
	 * 
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is handling the TRACE
	 *                request
	 * 
	 * @exception BindletException
	 *                if the request for the TRACE cannot be handled
	 * 
	 */

	protected void doTrace( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{

		int responseLength;

		String CRLF = "\r\n";
		String responseString = "TRACE " + request.getRequestURI() + " " + request.getProtocol();

		String[] reqHeaderEnum = request.getHeaderNames();

		for (String headerName : reqHeaderEnum)
			responseString += CRLF + headerName + ": " + request.getHeader(headerName);
		responseString += CRLF;
		responseLength = responseString.length();

		response.setContentType("message/http");
		response.setContentLength(responseLength);
		HttpBindletOutputStream out = (HttpBindletOutputStream) response.getOutputStream();
		out.write(responseString);
		out.close();
		return;
	}

	/**
	 * <p>
	 * Receives HTTP requests from the HTTP context and dispatches them to the <code>do</code>
	 * <i>XXX</i> methods defined in this class. This method is an HTTP-specific version of the
	 * {@link javax.bindlet.IBindlet#process} method. There's no need to override this method.
	 * </p>
	 * 
	 * @param request
	 *            the {@link IHttpBindletRequest} object that contains the request the client made
	 *            of the servlet
	 * 
	 * @param response
	 *            the {@link IHttpBindletResponse} object that contains the response the servlet
	 *            returns to the client
	 * 
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is handling the HTTP
	 *                request
	 * 
	 * @exception BindletException
	 *                if the HTTP request cannot be handled
	 * 
	 * @see IBindlet#process
	 * @see IHttpBindletRequest
	 * @see IHttpBindletResponse
	 */
	@Override
	public void process( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		if (request == null)
		{
			response.sendError(HttpStatus.BAD_REQUEST);
			return;
		}

		HttpMethod method = request.getHttpMethod();

		if (method == null)
		{
			response.sendError(HttpStatus.NOT_IMPLEMENTED);
			return;
		}

		if (isRestricted() && !doAuthentication(request, response)) return;

		switch (method)
		{
			case GET:
				long lastModified = getLastModified(request);
				if (lastModified == -1)
				{
					// bindlet doesn't support if-modified-since, no reason
					// to go through further expensive logic
					doGet(request, response);
				}
				else
				{
					long ifModifiedSince = request.getDateHeader(HEADER_IFMODSINCE);
					if (ifModifiedSince < (lastModified / 1000 * 1000))
					{
						// If the bindlet mod time is later, call doGet()
						// Round down to the nearest second for a proper compare
						// A ifModifiedSince of -1 will always be less
						maybeSetLastModified(response, lastModified);
						doGet(request, response);
					}
					else
					{
						response.setStatus(HttpStatus.NOT_MODIFIED);
					}
				}
				break;
			case POST:
				doPost(request, response);
				break;
			case PUT:
				doPut(request, response);
				break;
			case OPTIONS:
				doOptions(request, response);
				break;
			case TRACE:
				doTrace(request, response);
				break;
			case DELETE:
				doDelete(request, response);
				break;
			case HEAD:
				maybeSetLastModified(response, getLastModified(request));
				doHead(request, response);
				break;
		}
	}

	public boolean isRestricted()
	{
		String value = getInitParameter(INIT_PARAM_RESTRICTED);
		return (authenticator != null && (value != null && value.equalsIgnoreCase("true")));
	}

	protected void setAuthenticator( IBindletAuthenticator authenticator )
	{
		this.authenticator = authenticator;
	}

	protected IBindletAuthenticator getAuthenticator()
	{
		return authenticator;
	}

	/**
	 * Sets the Last-Modified entity header field, if it has not already been set and if the value
	 * is meaningful. Called before doGet, to ensure that headers are set before response data is
	 * written. A subclass might have set this header already, so we check.
	 */
	private void maybeSetLastModified( IHttpBindletResponse response, long lastModified )
	{
		if (response.containsHeader(HEADER_LASTMOD)) return;
		if (lastModified >= 0) response.setDateHeader(HEADER_LASTMOD, lastModified);
	}

	protected boolean doAuthentication( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		if (authenticator != null)
			return authenticator.authenticate(request, response);
		else
			throw new BindletException("No authenticator configured");
	}

}
