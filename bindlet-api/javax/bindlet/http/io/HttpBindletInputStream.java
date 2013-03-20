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

package javax.bindlet.http.io;


import java.io.IOException;
import java.nio.charset.Charset;

import javax.bindlet.IBindletRequest;
import javax.bindlet.io.BindletInputStream;


/**
 * <p>
 * Provides an abstract input stream for reading binary and text data from a client request.
 * </p>
 * 
 * <p>
 * A <code>HttpBindletInputStream</code> object is normally retrieved via the
 * <code>getInputStream</code> method from <code>IHttpBindletRequest</code> interface.
 * </p>
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 * 
 * @see IBindletRequest
 * @see IHttpBindletRequest
 */

public abstract class HttpBindletInputStream extends BindletInputStream
{

	/**
	 * Does nothing, because this is an abstract class.
	 * 
	 */
	public HttpBindletInputStream()
	{
	}

	/**
	 * <p>
	 * Reads the input stream, one line at a time. Starting at an offset, reads bytes into an array,
	 * until it reads a certain number of bytes or reaches a newline character, which it reads into
	 * the array as well.
	 * </p>
	 * 
	 * <p>
	 * This method returns -1 no byte is available because the end of the stream has been reached.
	 * </p>
	 * 
	 * @param b
	 *            an array of bytes into which data is read
	 * 
	 * @param off
	 *            an integer specifying the character at which this method begins reading
	 * 
	 * @param len
	 *            an integer specifying the maximum number of bytes to read
	 * 
	 * @return an integer specifying the actual number of bytes read, or -1 if the end of the stream
	 *         is reached
	 * 
	 * @exception IOException
	 *                if an input or output exception has occurred
	 * 
	 */
	public int readLine( byte[] b, int off, int len ) throws IOException
	{

		if (len <= 0)
		{
			return 0;
		}
		int count = 0, c;

		while ((c = read()) != -1)
		{
			b[off++] = (byte) c;
			count++;
			if (c == '\n' || count == len) break;
		}
		return count > 0 ? count : -1;
	}

	/**
	 * Read the entire data of the input stream as a string. It's necessary to specify the charset
	 * to be used to interpret the content.
	 * 
	 * @param charset
	 * @return
	 */
	public abstract String readText( Charset charset );

	/**
	 * Read the entire data of the input stream as a string.
	 * 
	 * @return
	 */
	public abstract String readText();

}
