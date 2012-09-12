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


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.bindlet.io.ICloseable;



public abstract class BindletInputStream extends InputStream implements ICloseable
{

	public abstract byte readByte( ) throws IOException;

	public abstract int readBytes( byte[] buffer ) throws IOException;
	
	public abstract int readBytes( byte[] buffer, int offset, int length ) throws IOException;

	/**
	 * Read UTF-8 characters from the client until found a line end.
	 * 
	 * @param value the <code>string</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less available space than the UTF-8 string size.
	 */
	public abstract String readString( ) throws IOException;

	/**
	 * Read characters from the client until found a line end.
	 * 
	 * @param value the <code>string</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less available space than the UTF-8 string size.
	 */
	public abstract String readString( Charset charset ) throws IOException;
	
	/**
	 * Read a character from client and increases
	 * the cursor position by 2.
	 * 
	 * @param value the <code>char</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 2 bytes available.
	 */
	public abstract char readChar( ) throws IOException;

	/**
	 * Read a 32-bit integer number from the client and increases
	 * the cursor position by 4.
	 * 
	 * @param value the <code>int</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 4 bytes available.
	 */
	public abstract int readInt( ) throws IOException;

	/**
	 * Read a 64-bit integer number from the client and increases
	 * the cursor position by 8.
	 * 
	 * @param value the <code>long</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 8 bytes available.
	 */
	public abstract long readLong( ) throws IOException;

	/**
	 * Read a 32-bit floating point number from the client and increases
	 * the cursor position by 4.
	 * 
	 * @param value the <code>float</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 4 bytes available.
	 */
	public abstract float readFloat( ) throws IOException;

	/**
	 * Read a 64-bit floating point number from the client and increases
	 * the cursor position by 8.
	 * 
	 * @param value the <code>double</code> value to send to the client
	 * @throws IOException
	 *             if the buffer have less than 8 bytes available.
	 */
	public abstract double readDouble( ) throws IOException;

	@Override
	public int read( ) throws IOException
	{
		return readByte();
	}

	@Override
	public int read(byte buffer[], int off, int len) throws IOException
	{
		return readBytes(buffer, off, len);
	}
	
	@Override
	public int read( byte value[] ) throws IOException
	{
		return readBytes(value);
	}
	
}
