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

package corinna.bindlet.rest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;

import javax.bindlet.BindletOutputStream;
import javax.bindlet.http.HttpStatus;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import corinna.bindlet.http.WebBindletResponse;
import corinna.service.rpc.ParameterList;

public class RestBindletResponse extends WebBindletResponse implements IRestBindletResponse
{

	private RestStatus status = RestStatus.OK;
	
	private Exception exception = null;
	
	private Object returnValue = null;

	private HttpResponse response = null;
	
	private String encoding = Charset.defaultCharset().displayName();
	
	public RestBindletResponse( Channel channel, HttpResponse response )
	{
		super(channel, response);
		this.response = response;
		String value = this.response.getHeader( HttpHeaders.Names.CONTENT_ENCODING);
		if (value != null) encoding = value;			
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
	
	public HttpResponse getResponse()
	{
		Charset charset = Charset.forName(encoding);
		ParameterList buffer = new ParameterList(charset);
		
		buffer.setValue("result", status.name());
		
		if (exception != null)
			buffer.setValue("message", exception.getMessage());
		else
		if (returnValue != null)
			buffer.setValue("return", returnValue);

		byte[] output = buffer.toString().getBytes(Charset.forName(encoding));
		ChannelBuffer content = ChannelBuffers.copiedBuffer(output);
		
		response.setContent(content);
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=" + encoding);
		HttpHeaders.setContentLength( response, content.capacity() );
		
		return response;
	}
	
}
