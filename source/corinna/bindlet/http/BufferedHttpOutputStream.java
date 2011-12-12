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

package corinna.bindlet.http;


import java.io.IOException;

import javax.bindlet.http.HttpBindletOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;


public class BufferedHttpOutputStream extends HttpBindletOutputStream
{

	/**
	 * Default buffer size, used when no value is specified in constructor (2 KiB)
	 */
	public static final int DEFAULT_BUFFER_SIZE = 2048;
	
	/**
	 * Minimum bufer size (1 KiB)
	 */
	public static final int MIN_BUFFER_SIZE = 1024;
	
	/**
	 * Maximum buffer size (20 MiB).
	 */
	public static final int MAX_BUFFER_SIZE = 1024 * 1024 * 20;

	private HttpResponse response = null;

	private WebBindletResponse bindlet = null;

	private ChannelBuffer buffer = null;

	private Channel channel = null;

	private Boolean isClosed = false;

	private String charset = null;

	public BufferedHttpOutputStream( WebBindletResponse resp )
	{
		this(resp, DEFAULT_BUFFER_SIZE);
	}

	public BufferedHttpOutputStream( WebBindletResponse resp, int estimatedLength )
	{
		if (resp == null)
			throw new NullPointerException("The bindlet response object can not be null");

		bindlet = resp;
		response = resp.getResponse();
		channel = resp.getChannel();
		buffer = response.getContent();
		charset = resp.getCharacterEncoding();

		// limits the buffer size in a valid range
		if (estimatedLength < MIN_BUFFER_SIZE)
			estimatedLength = MIN_BUFFER_SIZE;
		else
		if (estimatedLength > MAX_BUFFER_SIZE)
			estimatedLength = MAX_BUFFER_SIZE;

		if (buffer == null || buffer == ChannelBuffers.EMPTY_BUFFER
			|| !(buffer instanceof DynamicChannelBuffer))
		{
			// TODO: criar um pool de "ChannelBuffer"
			buffer = ChannelBuffers.dynamicBuffer(estimatedLength);
			response.setContent(buffer);
		}
	}

	@Override
	public void writeString( String value ) throws IOException
	{
		checkClosed();
		if (value == null) value = "null";
		byte[] bytes = value.getBytes(charset);
		writeBytes(bytes);
	}

	@Override
	public void writeChar( char value ) throws IOException
	{
		checkClosed();
		buffer.writeChar(value);
	}

	@Override
	public void writeInt( int value ) throws IOException
	{
		checkClosed();
		buffer.writeByte(value);
	}

	@Override
	public void writeLong( long value ) throws IOException
	{
		checkClosed();
		buffer.writeLong(value);
	}

	@Override
	public void writeFloat( float value ) throws IOException
	{
		checkClosed();
		buffer.writeFloat(value);
	}

	@Override
	public void writeDouble( double value ) throws IOException
	{
		checkClosed();
		buffer.writeDouble(value);
	}

	@Override
	public void flush() throws IOException
	{
	}

	@Override
	public void close() throws IOException
	{
		checkClosed();
		setClosed(true);

		// update the content length
		//bindlet.setContentLength(buffer.writerIndex());
		bindlet.update();
		response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, buffer.writerIndex());
		// write the bindlet content to the output channel
		channel.write(response);
	}

	private void setClosed( boolean value )
	{
		synchronized (isClosed)
		{
			isClosed = value;
		}
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
	public void writeByte( byte value ) throws IOException
	{
		checkClosed();
		buffer.writeByte(value);
	}

	@Override
	public void writeBytes( byte[] value, int off, int len ) throws IOException
	{
		checkClosed();

		if (value == null) throw new NullPointerException("The byte array can not be null");
		if (off < 0)
			throw new IndexOutOfBoundsException("The start index must be greater or equal zero");
		if (off > value.length)
			throw new IndexOutOfBoundsException("The start index must be less than array length");
		if (len < 0 || (off + len) > value.length)
			throw new IndexOutOfBoundsException("Invalid length");
		if (len == 0) return;

		buffer.writeBytes(value, off, len);
	}

	@Override
	public void write( Object object ) throws IOException
	{
		if (object == null)
			writeString(null);
		else
			writeString(object.toString());
	}

	private void checkClosed()
	{
		if (isClosed())
			throw new IllegalStateException("The output stream has been closed");
	}
	
}
