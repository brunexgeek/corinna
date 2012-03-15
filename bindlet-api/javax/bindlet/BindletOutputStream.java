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

package javax.bindlet;


import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;

import javax.bindlet.io.ICloseable;



/**
 * Provides an output stream for sending binary data to the client. A
 * <code>BindletOutputStream</code> object is normally retrieved via the
 * {@link IBindletResponse#getOutputStream} method.
 * 
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 * 
 * @see IBindletResponse
 * 
 */
public abstract class BindletOutputStream extends OutputStream implements ICloseable, Flushable
{

	/**
	 * Writes the specified byte to this output stream. The general contract for <code>write</code>
	 * is that one byte is written to the output stream. The byte to be written is the eight
	 * low-order bits of the argument <code>b</code>. The 24 high-order bits of <code>b</code> are
	 * ignored.
	 * <p>
	 * Subclasses of <code>OutputStream</code> must provide an implementation for this method.
	 * 
	 * @param b
	 *            the <code>byte</code>.
	 * @exception IOException
	 *                if an I/O error occurs. In particular, an <code>IOException</code> may be
	 *                thrown if the output stream has been closed.
	 */
	public abstract void writeByte( byte value ) throws IOException;

	/**
	 * Writes <code>b.length</code> bytes from the specified byte array to this output stream. The
	 * general contract for <code>write(b)</code> is that it should have exactly the same effect as
	 * the call <code>write(b, 0, b.length)</code>.
	 * 
	 * @param b
	 *            the data.
	 * @exception IOException
	 *                if an I/O error occurs.
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void writeBytes( byte value[] ) throws IOException
	{
		writeBytes(value, 0, value.length);
	}

	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at offset
	 * <code>off</code> to this output stream. The general contract for
	 * <code>write(b, off, len)</code> is that some of the bytes in the array <code>b</code> are
	 * written to the output stream in order; element <code>b[off]</code> is the first byte written
	 * and <code>b[off+len-1]</code> is the last byte written by this operation.
	 * <p>
	 * The <code>write</code> method of <code>OutputStream</code> calls the write method of one
	 * argument on each of the bytes to be written out. Subclasses are encouraged to override this
	 * method and provide a more efficient implementation.
	 * <p>
	 * If <code>b</code> is <code>null</code>, a <code>NullPointerException</code> is thrown.
	 * <p>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or <code>off+len</code> is
	 * greater than the length of the array <code>b</code>, then an
	 * <tt>IndexOutOfBoundsException</tt> is thrown.
	 * 
	 * @param value
	 *            the data.
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of bytes to write.
	 * @exception IOException
	 *                if an I/O error occurs. In particular, an <code>IOException</code> is thrown
	 *                if the output stream is closed.
	 */
	public abstract void writeBytes( byte value[], int off, int len ) throws IOException;

	/**
	 * Flushes this output stream and forces any buffered output bytes to be written out. The
	 * general contract of <code>flush</code> is that calling it is an indication that, if any bytes
	 * previously written have been buffered by the implementation of the output stream, such bytes
	 * should immediately be written to their intended destination.
	 * <p>
	 * If the intended destination of this stream is an abstraction provided by the underlying
	 * operating system, for example a file, then flushing the stream guarantees only that bytes
	 * previously written to the stream are passed to the operating system for writing; it does not
	 * guarantee that they are actually written to a physical device such as a disk drive.
	 * <p>
	 * The <code>flush</code> method of <code>OutputStream</code> does nothing.
	 * 
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public abstract void flush() throws IOException;

	/**
	 * Write a string to the client as UTF-8.
	 * 
	 * @param value the <code>string</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less available space than the UTF-8 string size.
	 */
	public abstract void writeString( String value ) throws IOException;

	/**
	 * Write a character to the client and increases
	 * the cursor position by 2.
	 * 
	 * @param value the <code>char</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 2 bytes available.
	 */
	public abstract void writeChar( char value ) throws IOException;

	/**
	 * Write a 32-bit integer number to the client and increases
	 * the cursor position by 4.
	 * 
	 * @param value the <code>int</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 4 bytes available.
	 */
	public abstract void writeInt( int value ) throws IOException;

	/**
	 * Write a 64-bit integer number to the client and increases
	 * the cursor position by 8.
	 * 
	 * @param value the <code>long</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 8 bytes available.
	 */
	public abstract void writeLong( long value ) throws IOException;

	/**
	 * Write a 32-bit floating point number to the client and increases
	 * the cursor position by 4.
	 * 
	 * @param value the <code>float</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 4 bytes available.
	 */
	public abstract void writeFloat( float value ) throws IOException;

	/**
	 * Write a 64-bit floating point number to the client and increases
	 * the cursor position by 8.
	 * 
	 * @param value the <code>double</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 8 bytes available.
	 */
	public abstract void writeDouble( double value ) throws IOException;

	public abstract void write( Object object ) throws IOException;

	@Override
	public void write( int value ) throws IOException
	{
		writeByte((byte)value);
	}

	@Override
	public void write( byte value[], int off, int len ) throws IOException
	{
		writeBytes(value, off, len);
	}
	
	@Override
	public void write( byte value[] ) throws IOException
	{
		writeBytes(value);
	}
	
	public abstract long writtenBytes();
}
