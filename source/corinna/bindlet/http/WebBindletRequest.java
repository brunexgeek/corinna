package corinna.bindlet.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.bindlet.BindletInputStream;
import javax.bindlet.http.IWebBindletRequest;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import corinna.core.http.HttpUtils;


public class WebBindletRequest implements IWebBindletRequest
{

	private HttpRequest request;
	
	private Map<String,String> params;
	
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
	
	public WebBindletRequest( HttpRequest request )
	{
		if (request == null)
			throw new NullPointerException("The internal request can not be null");
		
		this.request = request;
		this.params = new HashMap<String,String>();
		
		parseHost();
		parseUri();
		parseContentType( );
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
		return request.getHeaderNames().toArray( new String[0] );
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
	public String getHttpMethod()
	{
		return request.getMethod().getName();
	}

	@Override
	public BindletInputStream getInputStream() throws IOException
	{
		if (inputStream  != null)
		{
			if (inputStream.isClosed())
				throw new IllegalStateException("The bindlet input stream has been closed");
			return inputStream;
		}
		
		inputStream = new BufferedHttpInputStream(this);

		return inputStream;
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

	void parseContentType( )
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
		if (pos >= 0)
			characterEncoding = value.substring(pos + 7);
	}

	protected void parseForm( )
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
			String port = host.substring(pos+1, host.length());
			serverPort = Integer.parseInt(port);
		}
	}

	/**
	 * Separete the query string from the URL.
	 * 
	 * @param uri
	 */
	protected void parseUri( )
	{
		String uri = request.getUri();
		// extract the query string
		int pos = uri.indexOf("?");
		if (pos >= 0 && pos < (uri.length()-1))
		{
			setQueryString( uri.substring(pos+1) );
			setResourcePath( uri.substring(0, pos) );
		}
		else
			setResourcePath(uri);
		// extract the protocol scheme
		pos = uri.indexOf("://");
		if (pos >= 0)
			uriScheme = uri.substring(0, pos);
	}

	public void setBindletPath( String path )
	{
		if (path == null)
			throw new NullPointerException("The bindlet path can not be null");
		if (resourcePath == null)
			throw new NullPointerException("The bindlet context path must be setted");
		
		bindletPath = path;
		if (!resourcePath.startsWith(path))
			throw new IllegalArgumentException("The bindlet context path must be the prefix of request resource path");
		if (path.length() < resourcePath.length())
			resourcePath = resourcePath.substring( path.length() );
		else
			resourcePath = "";
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
		
		String temp = getResourcePath();
		if (!temp.startsWith(path))
			throw new IllegalArgumentException("The bindlet context path must be the prefix of request URI");
		
		contextPath = path;
		bindletPath = "";
		
		// extract the bindlet path
		if (path.length() < resourcePath.length())
			resourcePath = "/" + resourcePath.substring( path.length() );
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
	
}
