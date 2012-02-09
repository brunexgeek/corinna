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

import javax.bindlet.IBindletResponse;
import javax.bindlet.http.HttpBindletOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunk;


/**
 * Provides an output stream for sending binary data to the client through HTTP chunks.
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 * 
 * @see IBindletResponse
 */
public class ChunkedHttpOutputStream extends HttpBindletOutputStream
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

	private HttpChunk chunk = null;
	
	private ChannelBuffer buffer = null;

	private WebBindletResponse response;

	private Channel channel = null;

	private String charset = null;

	private Boolean isClosed = false;

	private Boolean isCommited = false;

	private long writtenBytes = 0;

	public ChunkedHttpOutputStream( WebBindletResponse resp, int bufferSize )
	{
		if (resp == null)
			throw new NullPointerException("The HTTP response object can not be null");
		
		// limits the buffer size in a valid range
		if (bufferSize < MIN_BUFFER_SIZE)
			bufferSize = MIN_BUFFER_SIZE;
		else
		if (bufferSize > MAX_BUFFER_SIZE)
			bufferSize = MAX_BUFFER_SIZE;

		response = resp;
		channel = response.getChannel();
		response.getResponse().setContent(null);
		charset = response.getCharacterEncoding();
		// TODO: criar um pool de "ChannelBuffer"
		buffer = ChannelBuffers.buffer(bufferSize);
		chunk = new DefaultHttpChunk(buffer);
	}

	public ChunkedHttpOutputStream( WebBindletResponse resp )
	{
		this(resp, DEFAULT_BUFFER_SIZE);
	}

	@Override
	public void writeString( String value ) throws IOException
	{
		checkClosed();
		if (value == null) value = "null";

		byte[] data = value.getBytes(charset);
		writeBytes(data);
	}

	@Override
	public void writeChar( char value ) throws IOException
	{
		checkClosed();
		flushIfNeeded(2);
		buffer.writeChar(value);
	}

	@Override
	public void writeInt( int value ) throws IOException
	{
		checkClosed();
		flushIfNeeded(4);
		buffer.writeByte(value);
	}

	@Override
	public void writeLong( long value ) throws IOException
	{
		checkClosed();
		flushIfNeeded(8);
		buffer.writeLong(value);
	}

	@Override
	public void writeFloat( float value ) throws IOException
	{
		checkClosed();
		flushIfNeeded(4);
		buffer.writeFloat(value);
	}

	@Override
	public void writeDouble( double value ) throws IOException
	{
		checkClosed();
		flushIfNeeded(8);
		buffer.writeDouble(value);
	}

	@Override
	public void write( Object object ) throws IOException
	{
		if (object == null)
			writeString(null);
		else
			writeString(object.toString());
	}

	public void flushIfNeeded( int bytes ) throws IOException
	{
		if (bytes > buffer.writableBytes()) flush();
	}

	@Override
	public void flush() throws IOException
	{
		checkClosed();

		if (!isCommited())
		{
			response.update();
			channel.write(response.getResponse());
			setCommited(true);
		}

		// ignore if the buffer has no data to flush (the last chunk will be sent by 'close' method)
		if (!buffer.readable()) return;
		
		/*
		 *  NOTE: the 'lastChunk' method of class 'HttpChunk' do not dynamically detects if the
		 *  buffer has data. So we need to assign the buffer whenever the chunk is written. 
		 */
		chunk.setContent(buffer);
		channel.write(chunk);
		writtenBytes  = buffer.writerIndex();
		buffer.clear();
	}

	@Override
	public void close() throws IOException
	{
		checkClosed();

		flush();
		channel.write(HttpChunk.LAST_CHUNK);
		setClosed(true);
	}

	@Override
	public boolean isClosed()
	{
		synchronized (isClosed)
		{
			return isClosed;
		}
	}

	private void setClosed( boolean state )
	{
		synchronized (isClosed)
		{
			isClosed = state;
		}
	}

	@Override
	public void writeByte( byte value ) throws IOException
	{
		checkClosed();
		flushIfNeeded(1);
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

		int remaining = len;
		int cursor = off;

		while (remaining > 0)
		{
			int length = remaining;

			if (length > buffer.writableBytes()) length = buffer.writableBytes();
			buffer.writeBytes(value, cursor, length);
			// check if need flush
			if (buffer.writableBytes() == 0) flush();

			cursor += length;
			remaining -= length;
		}
	}

	protected void setCommited( Boolean isCommited )
	{
		synchronized (isCommited)
		{
			this.isCommited = isCommited;
		}
	}

	protected Boolean isCommited()
	{
		synchronized (isCommited)
		{
			return isCommited;
		}
	}

	private void checkClosed()
	{
		if (isClosed()) throw new IllegalStateException("The output stream has been closed");
	}


	@Override
	public long writtenBytes()
	{
		return writtenBytes;
	}
	
}
