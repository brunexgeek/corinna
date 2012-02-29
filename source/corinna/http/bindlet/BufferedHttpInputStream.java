package corinna.http.bindlet;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.bindlet.http.HttpBindletInputStream;
import javax.bindlet.http.IWebBindletRequest;


public class BufferedHttpInputStream extends HttpBindletInputStream
{

	private Boolean isClosed = false;
	
	private IWebBindletRequest request;
	
	public BufferedHttpInputStream( IWebBindletRequest request )
	{
		this.request = request;
	}
	
	@Override
	public boolean isClosed()
	{
		synchronized (isClosed)
		{
			return isClosed;
		}
	}

	@Override
	public byte readByte() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readBytes( byte[] buffer ) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readBytes( byte[] buffer, int offset, int length ) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void flush() throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String readString() throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readString( Charset charset ) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char readChar() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readInt() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long readLong() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float readFloat() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double readDouble() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
