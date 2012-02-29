package corinna.bindlet.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.bindlet.BindletOutputStream;
import javax.bindlet.http.HttpBindletOutputStream;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IWebBindletResponse;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import corinna.core.http.HttpUtils;
import corinna.thread.ObjectLocker;


public abstract class WebBindletResponse implements IWebBindletResponse
{

	private static final String HEADER_SERVER = "Corinna";

	protected HttpResponse response = null;

	private String charset = null;
	
	private Calendar calendar = Calendar.getInstance();
	
	private Channel channel = null;

	private String contentType = null;

	private Boolean isCommited = false;

	private long contentLength = -1;

	private Locale locale = null;

	private boolean isChunked = true;

	private BindletOutputStream outputStream = null;
	
	private ObjectLocker outputLocker = null;
	
	//private Boolean isClosed = false;
	
	public WebBindletResponse( Channel channel, HttpResponse response )
	{
		if (response == null)
			throw new IllegalAccessError("The response object can not be null");
		if (channel == null)
			throw new IllegalArgumentException("The channel can not be null");
		
		this.response = response;
		this.channel = channel;
		this.outputLocker = new ObjectLocker();
		init();
	}
	
	@Override
	public boolean isClosed()
	{
		outputLocker.readLock();
		boolean result = (outputStream != null && outputStream.isClosed());
		outputLocker.readUnlock();
		
		return result;
	}
	
	protected void init()
	{
		contentType = "text/html";
		charset = Charset.defaultCharset().displayName();
		contentLength = -1;
		locale = Locale.ENGLISH;
		response.setStatus(HttpResponseStatus.OK);
		response.setContent(null);
		
		update();
	}
	
	protected void update()
	{
		String now = HttpUtils.formatDate( calendar.getTime() );
		
		// update the response date and last modification date
		response.setHeader(HttpHeaders.Names.DATE, now );
		if (!containsHeader(HttpHeaders.Names.LAST_MODIFIED))
			response.setHeader(HttpHeaders.Names.LAST_MODIFIED, now );
		
		// update the response content informations
		if (charset == null)
			response.setHeader( HttpHeaders.Names.CONTENT_TYPE, contentType );
		else
			response.setHeader( HttpHeaders.Names.CONTENT_TYPE, contentType + "; charset=" + charset );
		response.setHeader(HttpHeaders.Names.CONTENT_LANGUAGE, Locale.ENGLISH.getLanguage());
		
		// update the response extra fields
		response.setHeader(HttpHeaders.Names.SERVER, HEADER_SERVER);
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
	
	protected boolean isCommited()
	{
		synchronized (isCommited)
		{
			return isCommited || isClosed();
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
		
		update();
		
		outputLocker.writeLock();
		try
		{
			if (outputStream  == null)
			{
				if (!isChunked() || getContentLength() >= 0)
					outputStream = new BufferedHttpOutputStream(this);
				else
					outputStream = new ChunkedHttpOutputStream(this);
		
				setCommited(true);
			}
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
		if (isCommited()) return;

		response.clearHeaders();
		init();
	}

	@Override
	public String getContentType()
	{
		return contentType;
	}

	@Override
	public void setCharacterEncoding( String charset )
	{
		if (isCommited()) return;
		this.charset = charset;
	}

	@Override
	public void setCharacterEncoding( Charset charset )
	{
		if (isCommited()) return;
		if (charset == null) charset = Charset.defaultCharset();
		this.charset = charset.displayName();
	}

	@Override
	public void setContentType( String contentType )
	{
		if (isCommited()) return;
		this.contentType = contentType;
	}

	@Override
	public void setContentType( String contentType, String charset )
	{
		if (isCommited()) return;
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
		if (isCommited()) return;
		contentLength = length;
	}

	@Override
	public long getContentLength()
	{
		return contentLength;
	}

	@Override
	public void setLocale( Locale locale )
	{
		if (isCommited()) return;
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
		return response.containsHeader(name);
	}

	@Override
	public void sendError( HttpStatus code, String message ) throws IOException
	{
		if (isCommited()) return;
		
		HttpResponseStatus status = HttpResponseStatus.valueOf(code.getCode());
		response.setStatus(status);
		
		HttpBindletOutputStream output = (HttpBindletOutputStream) getOutputStream();
		output.writeString(message);
		output.close();
	}

	@Override
	public void sendError( HttpStatus code ) throws IOException
	{
		sendError(code, code.getDescription());
	}

	@Override
	public void sendRedirect( String location ) throws IOException
	{
		if (isCommited()) return;
		
		HttpStatus status = HttpStatus.valueOf( HttpResponseStatus.TEMPORARY_REDIRECT.getCode() );
		sendError(status);
		response.setHeader(HttpHeaders.Names.LOCATION , location);
		setCommited(true);
	}

	protected void setCommited( boolean value )
	{
		synchronized (isCommited)
		{
			isCommited = value;
		}
	}

	@Override
	public void setDateHeader( String name, Date date )
	{
		if (isCommited()) return;
		
		response.setHeader(name, HttpUtils.formatDate(date));
	}

	@Override
	public void setDateHeader( String name, long date )
	{
		if (isCommited()) return;
		
		Date temp = new Date(date);
		response.setHeader(name, HttpUtils.formatDate(temp));
	}
	
	@Override
	public void addDateHeader( String name, Date date )
	{
		if (isCommited()) return;
		
		response.addHeader(name, HttpUtils.formatDate(date));
	}

	@Override
	public void addDateHeader( String name, long date )
	{
		if (isCommited()) return;
		
		Date temp = new Date(date);
		response.addHeader(name, HttpUtils.formatDate(temp));
	}
	
	@Override
	public void setHeader( String name, Object value )
	{
		if (isCommited()) return;
		
		if (value instanceof Date)
			setDateHeader(name, (Date)value);
		else
			response.setHeader(name, value);
	}

	@Override
	public void addHeader( String name, Object value )
	{
		if (isCommited()) return;
		
		if (value instanceof Date)
			addDateHeader(name, (Date)value);
		else
			response.addHeader(name, value);
	}

	@Override
	public void setStatus( HttpStatus status )
	{
		if (isCommited()) return;
		
		response.setStatus( HttpResponseStatus.valueOf(status.getCode()) );
	}

	protected Channel getChannel()
	{
		return channel;
	}

	@Override
	public void setContentType( String contentType, Charset charset )
	{
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
		isChunked = value;
	}
	
	@Override
	public void close() throws IOException
	{
		if (isClosed()) return;

		BindletOutputStream out = getOutputStream();
		if (out != null && !out.isClosed()) out.close();
	}

	protected void sendHeaders()
	{
		update();
		channel.write(getResponse());
	}
	
	
	
}
