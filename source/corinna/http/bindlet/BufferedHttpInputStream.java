package corinna.http.bindlet;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.bindlet.http.HttpBindletInputStream;
import javax.bindlet.http.IWebBindletRequest;

import org.jboss.netty.buffer.ChannelBuffer;


public class BufferedHttpInputStream extends HttpBindletInputStream
{

	private Boolean isClosed = false;
	
	private ChannelBuffer content;
	
	public BufferedHttpInputStream( WebBindletRequest request )
	{
		this.content = request.getContent();
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
		return content.readByte();
	}

	@Override
	public int readBytes( byte[] buffer ) throws IOException
	{
		int length = buffer.length;
		if (length > content.readableBytes()) length = content.readableBytes();
		
		content.readBytes(buffer,0, length);
		return length;
	}

	@Override
	public int readBytes( byte[] buffer, int offset, int length ) throws IOException
	{
		if (length > content.readableBytes()) length = content.readableBytes();
		
		content.readBytes(buffer,0, length);
		return length;
	}

	@Override
	public String readString() throws IOException
	{
		return readString( Charset.defaultCharset() );
	}

	@Override
	public String readString( Charset charset ) throws IOException
	{
		int start = content.readerIndex();
		while ( content.readableBytes() > 0 && content.readByte() != '\n');
		
		int length = content.readerIndex() - start;
		content.setIndex(start, content.writerIndex());
		
		byte[] data = new byte[length];
		if (length > 0)
		{
			content.readBytes(data, 0, length);
			if (data[length-1] == '\r') data[length-1] = 0;
			return new String(data, charset);
		}
		else
			return null;
	}

	@Override
	public char readChar() throws IOException
	{
		return content.readChar();
	}

	@Override
	public int readInt() throws IOException
	{
		return content.readInt();
	}

	@Override
	public long readLong() throws IOException
	{
		return content.readLong();
	}

	@Override
	public float readFloat() throws IOException
	{
		return content.readFloat();
	}

	@Override
	public double readDouble() throws IOException
	{
		return content.readDouble();
	}

}
