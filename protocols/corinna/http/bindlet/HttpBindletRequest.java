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

package corinna.http.bindlet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.bindlet.http.Cookie;
import javax.bindlet.http.HttpMethod;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.ISession;
import javax.bindlet.io.BindletInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;

import corinna.http.core.HttpUtils;

//TODO: promote this class to a concrete request (not a request wrapper) PS: need to create new pipeline handlers
public class HttpBindletRequest implements IHttpBindletRequest
{


	private HttpRequest request;

	private Map<String, String> params;

	private String serverName = null;

	private int serverPort = 0;

	private String contextPath = "";

	private String bindletPath = "";

	private String queryString = "";

	private String resourcePath = "";

	private String uriScheme = "http";

	private BindletInputStream inputStream;

	private String contentType;

	private String characterEncoding;

	private String userName = null;

	private Channel channel;

	public HttpBindletRequest( Channel channel, HttpRequest request )
	{
		if (request == null)
			throw new NullPointerException("The internal request can not be null");

		this.request = request;
		this.params = new HashMap<String, String>();
		this.channel = channel;

		parseHost();
		parseUri();
		parseContentType();
	}

	@Override
	public boolean containsHeader( String name )
	{
		return request.containsHeader(name);
	}

	@Override
	public String getBindletPath()
	{
		return bindletPath;
	}

	@Override
	public String getCharacterEncoding()
	{
		return characterEncoding;
	}

	@Override
	public long getContentLength()
	{
		return HttpHeaders.getContentLength(request);
	}

	@Override
	public String getContentType()
	{
		return contentType;
	}

	@Override
	public String getContextPath()
	{
		return contextPath;
	}

	@Override
	public long getDateHeader( String name )
	{
		String value = request.getHeader(name);
		if (value == null) return -1;
		return HttpUtils.parseDateToLong(value);
	}

	@Override
	public String getHeader( String name )
	{
		return request.getHeader(name);
	}

	@Override
	public String[] getHeaderNames()
	{
		return request.getHeaderNames().toArray(new String[0]);
	}

	@Override
	public String[] getHeaders( String name )
	{
		String value = getHeader(name);
		if (value != null)
			return value.split(";");
		else
			return null;
	}

	@Override
	public HttpMethod getHttpMethod()
	{
		return HttpMethod.valueOf(request.getMethod().getName());
	}

	@Override
	public BindletInputStream getInputStream() throws IOException
	{
		if (inputStream != null)
		{
			if (inputStream.isClosed())
				throw new IllegalStateException("The bindlet input stream has been closed");
			return inputStream;
		}

		inputStream = new BufferedHttpInputStream(this);

		return inputStream;
	}

	public ChannelBuffer getContent()
	{
		return request.getContent();
	}

	@Override
	public String getParameter( String name )
	{
		return params.get(name);
	}

	@Override
	public String[] getParameterNames()
	{
		// TODO: store the parameters array in a private field (do not generate everytime)
		return params.keySet().toArray(new String[0]);
	}

	@Override
	public String[] getParameterValues( String name )
	{
		// TODO: implements this!
		return null;
	}

	@Override
	public String getProtocol()
	{
		return "HTTP/1.1";
	}

	@Override
	public String getQueryString()
	{
		return queryString;
	}

	protected HttpRequest getRequest()
	{
		return request;
	}

	@Override
	public String getRequestURI()
	{
		return HttpUtils.getRequestURI(this).toString();
	}

	@Override
	public String getRequestURL()
	{
		return HttpUtils.getRequestURL(this).toString();
	}

	@Override
	public String getRequestURN()
	{
		return HttpUtils.getRequestURN(this).toString();
	}

	@Override
	public String getResourcePath()
	{
		return resourcePath;
	}

	@Override
	public String getScheme()
	{
		return uriScheme;
	}

	@Override
	public String getServerName()
	{
		return serverName;
	}

	@Override
	public int getServerPort()
	{
		return serverPort;
	}

	@Override
	public boolean isSecure()
	{
		return false;
	}

	void parseContentType()
	{
		String value = request.getHeader(HttpHeaders.Names.CONTENT_TYPE);
		if (value == null || value.isEmpty())
		{
			contentType = "";
			characterEncoding = "";
			return;
		}

		int pos = value.indexOf(";");
		if (pos < 0)
		{
			contentType = value.trim();
			if (contentType.equalsIgnoreCase("application/x-www-form-urlencoded"))
			{
				characterEncoding = "ANSI";
				parseForm();
			}
			return;
		}

		contentType = value.substring(0, pos).trim();
		pos = value.indexOf("charset=");
		if (pos >= 0) characterEncoding = value.substring(pos + 8);
	}

	protected void parseForm()
	{
		try
		{
			String line = request.getContent().toString(Charset.forName("8859_1"));
			HttpUtils.parseQueryString(params, line, "&", "=");
		} catch (Exception e)
		{
			// supress any errors
			e.printStackTrace();
		}
	}

	protected void parseHost()
	{
		String host = request.getHeader(HttpHeaders.Names.HOST);
		if (host == null || host.isEmpty()) return;

		serverPort = 80;
		serverName = "";

		int pos = host.indexOf(":");
		if (pos < 0 || pos + 1 == host.length())
			serverName = host;
		else
		{
			serverName = host.substring(0, pos);
			String port = host.substring(pos + 1, host.length());
			serverPort = Integer.parseInt(port);
		}
	}

	/**
	 * Separete the query string from the URL.
	 * 
	 * @param uri
	 */
	protected void parseUri()
	{
		String uri = HttpUtils.clearUri(request.getUri());
		if (uri == null) return;

		// extract the query string
		int pos = uri.indexOf("?");
		if (pos >= 0 && pos < (uri.length() - 1))
		{
			setQueryString(uri.substring(pos + 1));
			setResourcePath(uri.substring(0, pos));
		}
		else
			setResourcePath(uri);
		// extract the protocol scheme
		pos = uri.indexOf("://");
		if (pos >= 0) uriScheme = uri.substring(0, pos);
	}

	public void setBindletPath( String path )
	{
		if (path == null) throw new NullPointerException("The bindlet path can not be null");
		if (resourcePath == null)
			throw new NullPointerException("The bindlet context path must be setted");

		if (!resourcePath.startsWith(path))
			throw new IllegalArgumentException(
				"The bindlet context path must be the prefix of request resource path");

		// add the initial slash if necessary
		if (path.isEmpty() || path.charAt(0) != '/') path = '/' + path;

		bindletPath = path;

		if (path.length() < resourcePath.length())
			resourcePath = resourcePath.substring(path.length());
		else
			resourcePath = "/";
	}

	/**
	 * Defines the context path for this request. This method extract the real context path from
	 * URI, which will returned by {@link #getContextPath()} and the rest of URI will be returned by
	 * {@link #getBindletPath()}.
	 * 
	 * @param path
	 */
	public void setContextPath( String path )
	{
		if (path == null)
			throw new NullPointerException("The bindlet context path can not be null");
		if (!path.isEmpty() && path.charAt(0) != '/')
			throw new NullPointerException("Malformed context path");

		String temp = getResourcePath();
		if (!temp.startsWith(path))
			throw new IllegalArgumentException(
				"The bindlet context path must be the prefix of request URI");

		// add the initial slash if necessary
		if (path.isEmpty())
			contextPath = '/' + path;
		else
			contextPath = path;
		bindletPath = "";

		// extract the bindlet path
		if (path.length() < resourcePath.length())
			resourcePath = resourcePath.substring(path.length());
		else
			resourcePath = "";
	}

	public void setQueryString( String queryString )
	{
		if (queryString == null || queryString.isEmpty())
		{
			this.queryString = "";
			this.params = null;
		}
		else
		{
			this.queryString = queryString;
			HttpUtils.parseQueryString(params, queryString, "&", "=");
		}
	}

	public void setResourcePath( String path )
	{
		this.resourcePath = path;
	}

	@Override
	public boolean isKeepAlive()
	{
		String value = getHeader(Names.CONNECTION);
		if (Values.CLOSE.equalsIgnoreCase(value)) return false;

		if (request.getProtocolVersion().isKeepAliveDefault())
			return true;
		else
			return Values.KEEP_ALIVE.equalsIgnoreCase(value);
	}

	@Override
	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
	}

	@Override
	public InetSocketAddress getRemoteAddress()
	{
		SocketAddress addr = channel.getRemoteAddress();
		if (addr instanceof InetSocketAddress)
			return (InetSocketAddress) addr;
		else
			return null;
	}

	@Override
	public String getAuthType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteUser()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserInRole( String role )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Principal getUserPrincipal()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISession getSession( boolean create )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISession getSession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdValid()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL()
	{
		// TODO Auto-generated method stub
		return false;
	}


}
