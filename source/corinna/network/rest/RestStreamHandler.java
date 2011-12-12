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

package corinna.network.rest;


import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;

import java.nio.charset.Charset;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import corinna.bindlet.rest.IRestBindletRequest;
import corinna.bindlet.rest.IRestBindletResponse;
import corinna.bindlet.rest.RestBindletRequest;
import corinna.bindlet.rest.RestBindletResponse;
import corinna.network.RequestEvent;
import corinna.network.StreamHandler;
import corinna.util.Stateless;


@Stateless
public class RestStreamHandler extends StreamHandler
{

	private RestNetworkConnector connector;

	public RestStreamHandler( RestNetworkConnector connector )
	{
		if (connector == null)
			throw new NullPointerException("The network connector can not be null");
		this.connector = connector;
	}

	@Override
	public void incomingMessage( ChannelHandlerContext context, MessageEvent event )
		throws Exception
	{
		HttpRequest request = (HttpRequest) event.getMessage();
		HttpResponse response = null;
		Charset charset = getCharset(request);

		IRestBindletRequest req = new RestBindletRequest(request);
		IRestBindletResponse res = new RestBindletResponse(event.getChannel(), request.getProtocolVersion() );

		RequestEvent<IRestBindletRequest, IRestBindletResponse> e = new RestRequestEvent(req, res);
		try
		{
			connector.handlerRequestReceived(this, e);
		} catch (Exception ex)
		{
			res.setException(ex);
			ex.printStackTrace();
		}
		
		response = res.getResponse();
		ChannelFuture writeFuture = event.getChannel().write(response);
		// decide whether to close the connection or not.
		if (!isKeepAlive(request))
			// close the connection when the whole content is written out.
			writeFuture.addListener(ChannelFutureListener.CLOSE);
	}

	protected Charset getCharset( HttpRequest request )
	{
		String value = request.getHeader( HttpHeaders.Names.CONTENT_ENCODING);
		if (value != null)
		{
			try
			{
				return Charset.forName(value);
			} catch (Exception e)
			{
				// supress any error
			}
		}
		return Charset.defaultCharset();
	}
	
}
