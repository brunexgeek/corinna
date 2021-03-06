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

import javax.bindlet.http.io.HttpBindletOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunk;


public class DefaultHttpOutputStream extends HttpBindletOutputStream implements ChannelFutureListener
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
	 * Maximum buffer size (10 MiB).
	 */
	public static final int MAX_BUFFER_SIZE = 1024 * 1024 * 10;

	protected ChannelBuffer buffer = null;

	protected HttpBindletResponse response;

	protected Channel channel = null;

	protected String charset = null;

	protected Boolean isClosed = false;

	protected Boolean isCommited = false;

	private Long writtenBytes = 0L;

	public DefaultHttpOutputStream( HttpBindletResponse resp, long bufferSize )
	{
		if (resp == null)
			throw new NullPointerException("The HTTP response object can not be null");

		// try to use the size specified in the response content length
		if (!resp.isChunked() && resp.getContentLength() > 0)
			bufferSize = resp.getContentLength();

		// limits the buffer size in a valid range
		if (bufferSize < MIN_BUFFER_SIZE)
			bufferSize = MIN_BUFFER_SIZE;
		else
			if (bufferSize > MAX_BUFFER_SIZE) bufferSize = MAX_BUFFER_SIZE;

		response = resp;
		channel = response.getChannel();
		response.getResponse().setContent(null);
		charset = response.getCharacterEncoding();
		if (charset == null) charset = "UTF-8";
		// TODO: criar um pool de "ChannelBuffer"
		buffer = ChannelBuffers.buffer( (int)bufferSize + 1 );
	}

	public DefaultHttpOutputStream( HttpBindletResponse resp )
	{
		this(resp, DEFAULT_BUFFER_SIZE);
	}

	@Override
	public void writeString( String value ) throws IOException
	{
		throwIfClosed();
		if (value == null || value.isEmpty()) return;

		byte[] data = value.getBytes(charset);
		writeBytes(data);
	}

	@Override
	public void writeChar( char value ) throws IOException
	{
		throwIfClosed();
		flushIfNeeded(2);
		buffer.writeChar(value);
	}

	@Override
	public void writeInt( int value ) throws IOException
	{
		throwIfClosed();
		flushIfNeeded(4);
		buffer.writeByte(value);
	}

	@Override
	public void writeLong( long value ) throws IOException
	{
		throwIfClosed();
		flushIfNeeded(8);
		buffer.writeLong(value);
	}

	@Override
	public void writeFloat( float value ) throws IOException
	{
		throwIfClosed();
		flushIfNeeded(4);
		buffer.writeFloat(value);
	}

	@Override
	public void writeDouble( double value ) throws IOException
	{
		throwIfClosed();
		flushIfNeeded(8);
		buffer.writeDouble(value);
	}

	@Override
	public void write( Object object ) throws IOException
	{
		if (object == null) return;
		writeString(object.toString());
	}

	public void flushIfNeeded( int bytes ) throws IOException
	{
		if (bytes > buffer.writableBytes()) flush(false);
	}

	/**
	 * 
	 * @param isFinished
	 * @throws IOException
	 */
	protected void flush( boolean isFinished ) throws IOException
	{
		throwIfClosed();

		// check whether the HTTP headers are already sent
		commit(isFinished);

		// ignore empty buffers
		if (buffer.readable())
		{
			// Note: we need to create a copy of the main buffer because the 'Channel.write' method is
			// asynchronous. It's necessary to discover a new method that not require create
			// multiple intermediary buffers when writting to output channel.
			// TODO: create a pool of ChannelBuffer's that return the objects when ChannelFuture is
			// called
			ChannelBuffer temp = ChannelBuffers.copiedBuffer(buffer);
			// TODO: we really need to create a new chunk every time?
			if (!response.isChunked())
				channel.write(temp);
			else
				channel.write( new DefaultHttpChunk(temp));
			incWrittenBytes(buffer.writerIndex());
			buffer.clear();
		}
		// whether we are using HTTP chunks and this is the last flush, write the last chunk
		if (isFinished && response.isChunked()) 
			channel.write(HttpChunk.LAST_CHUNK);
	}

	@Override
	public void flush() throws IOException
	{
		flush(false);
	}

	@Override
	public void close() throws IOException
	{
		flush(true);

		synchronized (isClosed)
		{
			isClosed = true;
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
		throwIfClosed();
		flushIfNeeded(1);
		buffer.writeByte(value);
	}

	@Override
	public void writeBytes( byte[] value, int off, int len ) throws IOException
	{
		throwIfClosed();

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
			if (buffer.writableBytes() == 0 && remaining > 0) flush();

			cursor += length;
			remaining -= length;
		}
	}

	protected void throwIfClosed()
	{
		if (isClosed()) throw new IllegalStateException("Output stream has been closed");
	}

	@Override
	public long writtenBytes()
	{
		synchronized (writtenBytes)
		{
			return writtenBytes;
		}
	}

	protected void incWrittenBytes( long amount )
	{
		synchronized (writtenBytes)
		{
			writtenBytes += amount;
		}
	}

	@Override
	public void operationComplete( ChannelFuture arg0 ) throws Exception
	{
		/*
		 * incWrittenBytes(buffer.writerIndex()); buffer.clear(); isFlushing.setFlag(false);
		 * System.out.println("Flushed ended! Current total is " + writtenBytes());
		 */
	}

	protected void commit( boolean finished )
	{
		synchronized (isCommited)
		{
			if (isCommited) return;
			// update the content length of the response
			if (finished) 
				response.setContentLength(buffer.writerIndex());
			else
				response.setChunked(true);
			// send the HTTP headers
			response.commit();
			isCommited = true;
		}
	}

	protected Boolean isCommited()
	{
		synchronized (isCommited)
		{
			return isCommited;
		}
	}

}
