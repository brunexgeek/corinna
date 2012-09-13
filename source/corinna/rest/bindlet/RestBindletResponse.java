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

package corinna.rest.bindlet;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.bindlet.BindletOutputStream;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import corinna.http.bindlet.WebBindletResponse;
import corinna.rpc.ParameterList;

public class RestBindletResponse extends WebBindletResponse implements IRestBindletResponse
{

	private RestStatus status = RestStatus.OK;
	
	private Exception exception = null;
	
	private Object returnValue = null;
		
	public RestBindletResponse( Channel channel, HttpResponse response )
	{
		super(channel, response);
		setCharacterEncoding(Charset.defaultCharset());
		setContentType("text/plain");
	}

	public RestBindletResponse( Channel channel, HttpVersion version )
	{
		super(channel, new DefaultHttpResponse(version, HttpResponseStatus.OK));
	}
	
	@Override
	public Object getReturnValue()
	{
		return returnValue;
	}

	public void setReturnValue( Object value )
	{
		this.returnValue = value;
	}
	
	@Override
	public RestStatus getStatus()
	{
		return status;
	}
	
	@Override
	public Exception getException()
	{
		return exception;
	}

	public void setException( Exception exception )
	{
		this.exception = exception;
		if (exception == null)
			status = RestStatus.OK;
		else
			status = RestStatus.ERROR;
	}

	@Override
	public void close() throws IOException
	{
		if (isClosed()) return;

		BindletOutputStream out = getOutputStream();
		try
		{
			if (!out.isClosed() && out.writtenBytes() == 0)
			{
				// TODO: use one of client acceptable charsets 
				Charset charset = Charset.defaultCharset();
				ParameterList buffer = new ParameterList(charset);
				
				buffer.setValue("result", status.name());
				
				if (exception != null)
					buffer.setValue("message", exception.getMessage());
				else
				if (returnValue != null)
					buffer.setValue("return", returnValue);

				byte[] output = buffer.toString().getBytes(charset);
				out.write(output);
			}
		} catch (Exception e)
		{
			// suprime os erros
		}
		if (out != null && !out.isClosed()) out.close();
	}
	
	@Override
	public void setStatus( RestStatus status )
	{
		this.status = status;
	}
	
}
