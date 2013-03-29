/*
 * Copyright 2011-2013 Bruno Ribeiro
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
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.bindlet.http.Cookie;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.http.io.HttpBindletOutputStream;
import javax.bindlet.io.BindletOutputStream;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import corinna.http.core.HttpUtils;
import corinna.thread.ObjectLocker;


public class HttpBindletResponse implements IHttpBindletResponse
{

	//private List<Cookie> cookies = null;

	protected HttpResponse response = null;

	private String charset = null;

	private Channel channel = null;

	private String contentType = null;
	
	private Boolean isCommited = false;

	private long contentLength = -1;

	private Locale locale = Locale.ENGLISH;

	private boolean isChunked = true;

	private BindletOutputStream outputStream = null;

	private ObjectLocker outputLocker = null;

	private HttpStatus status = HttpStatus.OK;

	private Map<String, Object> headers;

	public HttpBindletResponse( Channel channel, HttpResponse response )
	{
		if (response == null) throw new IllegalAccessError("The response object can not be null");
		if (channel == null) throw new IllegalArgumentException("The channel can not be null");

		this.response = response;
		this.channel = channel;
		this.outputLocker = new ObjectLocker();
		this.headers = new HashMap<String, Object>();
		//cookies = new LinkedList<Cookie>();

		reset();
	}

	@Override
	public boolean isClosed()
	{
		outputLocker.readLock();
		boolean result = (outputStream != null && outputStream.isClosed());
		outputLocker.readUnlock();

		return result;
	}

	/**
	 * Update the wrapped <code>HttpResponse</code> object.
	 */
	protected void update()
	{
		// copy all HTTP headers
		response.clearHeaders();
		Set<String> names = headers.keySet();
		for (String current : names)
			response.setHeader(current, getHeader(current));

		// update the response content informations
		if (charset == null)
			response.setHeader(HttpHeaders.Names.CONTENT_TYPE, contentType);
		else
			response.setHeader(HttpHeaders.Names.CONTENT_TYPE, contentType + "; charset=" + charset);
		response.setHeader(HttpHeaders.Names.CONTENT_LANGUAGE, Locale.ENGLISH.getLanguage());

		response.setStatus( HttpResponseStatus.valueOf(status.getCode()));
		
		// update the response extra fields
		// response.setHeader(HttpHeaders.Names.SERVER, HEADER_SERVER);
		if (isChunked() && getContentLength() < 0)
		{
			response.setHeader(HttpHeaders.Names.TRANSFER_ENCODING, HttpHeaders.Values.CHUNKED);
			response.removeHeader(HttpHeaders.Names.CONTENT_LENGTH);
		}
		else
		{
			long value = (contentLength >= 0) ? contentLength : 0L;
			response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, value);
			// TODO: it will cause problems with others transfer encodings (gzip, deflate, etc.).
			response.removeHeader(HttpHeaders.Names.TRANSFER_ENCODING);
		}
	}

	protected HttpResponse getResponse()
	{
		update();
		return response;
	}

	@Override
	public BindletOutputStream getOutputStream() throws IOException
	{
		BindletOutputStream out = null;

		outputLocker.writeLock();
		try
		{
			if (outputStream == null) outputStream = new DefaultHttpOutputStream(this);
			out = outputStream;
		} finally
		{
			outputLocker.writeUnlock();
		}

		return out;
	}

	@Override
	public void reset()
	{
		throwIfCommited();

		clearHeaders();
		// set the default values
		contentType = "text/html";
		charset = Charset.defaultCharset().displayName();
		contentLength = -1;
		locale = Locale.ENGLISH;
		status = HttpStatus.OK;
		// TODO: all responses must be chunked because we always commit before write the content. Fix this!
		isChunked = true;
	}

	@Override
	public void clearHeaders()
	{
		headers.clear();
	}

	@Override
	public Object getHeader( String name )
	{
		return headers.get(name);
	}

	@Override
	public String getContentType()
	{
		return contentType;
	}

	@Override
	public void setCharacterEncoding( String charset )
	{
		throwIfCommited();
		this.charset = charset;
	}

	@Override
	public void setCharacterEncoding( Charset charset )
	{
		throwIfCommited();
		if (charset == null) charset = Charset.defaultCharset();
		this.charset = charset.displayName();
	}

	@Override
	public void setContentType( String contentType )
	{
		throwIfCommited();
		this.contentType = contentType;
	}

	@Override
	public void setContentType( String contentType, String charset )
	{
		throwIfCommited();
		this.contentType = contentType;
		this.charset = charset;
	}

	@Override
	public String getCharacterEncoding()
	{
		return charset;
	}

	@Override
	public void setContentLength( long length )
	{
		throwIfCommited();
		contentLength = length;
		isChunked = (length < 0);
	}

	@Override
	public long getContentLength()
	{
		return contentLength;
	}

	@Override
	public void setLocale( Locale locale )
	{
		throwIfCommited();
		this.locale = locale;
	}

	@Override
	public Locale getLocale()
	{
		return locale;
	}

	@Override
	public boolean containsHeader( String name )
	{
		return headers.containsKey(name);
	}

	protected void throwIfCommited()
	{
		if (isCommited())
			throw new IllegalStateException("HTTP response already commited");
	}

	@Override
	public void sendError( HttpStatus status, Throwable exception ) throws IOException
	{
		throwIfCommited();

		StringBuilder sb = new StringBuilder();
		sb.append("<pre id='info'>");
		
		while (exception != null)
		{
			sb.append("<p id='exception'><strong>");
			sb.append(exception.toString());
			sb.append("</strong></p>");
			for (StackTraceElement element : exception.getStackTrace())
			{
				sb.append("<p class='stackEntry'>   at ");
				sb.append(element.toString());
				sb.append("</p>");
			}
			exception = exception.getCause();
		}
		sb.append("</pre>");
		sendError(status, sb.toString());
	}

	@Override
	public void sendError( HttpStatus status, String message ) throws IOException
	{
		throwIfCommited();

		setStatus(status);
		HttpBindletOutputStream output = (HttpBindletOutputStream) getOutputStream();
		if (message != null) output.writeString(message);
		output.close();
	}

	@Override
	public void sendError( HttpStatus code ) throws IOException
	{
		sendError(code, (String) null);
	}

	@Override
	public void sendRedirect( String location ) throws IOException
	{
		throwIfCommited();

		setHeader(HttpHeaders.Names.LOCATION, location);
		setStatus(HttpStatus.TEMPORARY_REDIRECT);
		BindletOutputStream output = getOutputStream();
		output.close();
	}

	@Override
	public void setDateHeader( String name, Date date )
	{
		throwIfCommited();

		if (date == null) return;
		setHeader(name, HttpUtils.formatDate(date));
	}

	@Override
	public void setDateHeader( String name, long date )
	{
		throwIfCommited();

		if (date < 0) return;
		Date temp = new Date(date);
		setHeader(name, HttpUtils.formatDate(temp));
	}

	@Override
	public void addDateHeader( String name, Date date )
	{
		throwIfCommited();

		if (date == null) return;
		addHeader(name, HttpUtils.formatDate(date));
	}

	@Override
	public void addDateHeader( String name, long date )
	{
		throwIfCommited();

		Date temp = new Date(date);
		addHeader(name, HttpUtils.formatDate(temp));
	}

	@Override
	public void setHeader( String name, Object value )
	{
		throwIfCommited();

		if (value instanceof Date)
			setDateHeader(name, (Date) value);
		else
			headers.put(name, value);
	}

	@Override
	public void addHeader( String name, Object value )
	{
		throwIfCommited();

		if (value instanceof Date)
			addDateHeader(name, (Date) value);
		else
			// TODO: implements this!
			setHeader(name, value);
	}

	@Override
	public void setStatus( HttpStatus status )
	{
		throwIfCommited();

		this.status = status;
	}

	public HttpStatus getStatus()
	{
		return status;
	}

	protected Channel getChannel()
	{
		return channel;
	}

	@Override
	public void setContentType( String contentType, Charset charset )
	{
		throwIfCommited();

		this.contentType = contentType;
		this.charset = charset.displayName();
	}

	@Override
	public boolean isChunked()
	{
		return isChunked;
	}

	@Override
	public void setChunked( boolean value )
	{
		throwIfCommited();
		isChunked = value;
		if (value) contentLength = -1;
	}

	@Override
	public void close() throws IOException
	{
		if (isClosed()) return;

		BindletOutputStream out = getOutputStream();
		if (out != null && !out.isClosed()) out.close();
	}

	protected boolean isCommited()
	{
		synchronized (isCommited)
		{
			return isCommited;
		}
	}
	
	protected void commit()
	{
		synchronized (isCommited)
		{
			channel.write(getResponse());
			isCommited = true;
		}
	}
	
	@Override
	public void addCookie( Cookie cookie )
	{
	}

	@Override
	public String encodeURL( String url )
	{
		return url;
	}

	@Override
	public String encodeRedirectURL( String url )
	{
		return url;
	}
	
}
