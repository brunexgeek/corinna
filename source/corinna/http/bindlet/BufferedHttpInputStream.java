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

package corinna.http.bindlet;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.bindlet.http.HttpBindletInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

//TODO: use this class as a generic buffered input stream
public class BufferedHttpInputStream extends HttpBindletInputStream
{

	private Boolean isClosed = false;
	
	protected ChannelBuffer content = null;

	protected WebBindletRequest request;

	protected Channel channel = null;
	
	public BufferedHttpInputStream( WebBindletRequest request )
	{
		this.content = request.getContent();
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

	@Override
	public String readText( Charset charset )
	{
		if (charset == null) charset = Charset.defaultCharset();
		return content.toString(charset);
	}
	
	@Override
	public String readText( )
	{
		return content.toString( Charset.defaultCharset() );
	}
	
}
