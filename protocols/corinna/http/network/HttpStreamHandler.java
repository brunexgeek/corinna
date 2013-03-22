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

package corinna.http.network;

import javax.bindlet.BindletModel.Model;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.http.bindlet.HttpBindletRequest;
import corinna.http.bindlet.HttpBindletResponse;
import corinna.network.RequestEvent;
import corinna.network.StreamHandler;
import corinna.util.StateModel;


@StateModel(Model.STATELESS)
public class HttpStreamHandler extends StreamHandler
{

	Logger log = LoggerFactory.getLogger(HttpStreamHandler.class);
	
	private HttpConnector connector;
	
	public HttpStreamHandler( HttpConnector connector )
	{
		if (connector == null)
			throw new NullPointerException("The network connector can not be null");
		this.connector = connector;
	}

	@Override
	public void incomingMessage( ChannelHandlerContext context, MessageEvent event )
		throws Exception
	{
		if (event.getMessage() == null)
			throw new NullPointerException("The request object can not be null");

		Channel channel = context.getChannel();

		HttpRequest request = (HttpRequest) event.getMessage();
		HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
		
		IHttpBindletRequest req = new HttpBindletRequest(channel, request);
		IHttpBindletResponse res = new HttpBindletResponse(channel, response);
		
		HttpRequestEvent ev = new HttpRequestEvent(req,res);
		try
		{
			connector.handlerRequestReceived(this, ev, channel);
		} catch (Exception ex)
		{
			onError(ev, channel, ex);
			return;
		}
		onSuccess(ev, channel);
	}
	
	@Override
	public void onError( RequestEvent<?,?> event, Channel channel, Throwable exception )
	{
		log.error("Error processing HTTP request", exception);

		IHttpBindletResponse response = (IHttpBindletResponse) event.getResponse();
		IHttpBindletRequest request = (IHttpBindletRequest) event.getRequest();
		
		try
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, exception);
		} catch (Exception e)
		{
			log.error("Error sending HTTP error information", e);
		}
		
		// close the connection, if necessary
		if (!request.isKeepAlive()) channel.close();
	}

	@Override
	public void onSuccess( RequestEvent<?,?> event, Channel channel )
	{
		IHttpBindletResponse response = (IHttpBindletResponse) event.getResponse();
		IHttpBindletRequest request = (IHttpBindletRequest) event.getRequest();
		
		try
		{
			if (!event.isHandled())
				response.sendError(HttpStatus.NOT_FOUND);
			else
				response.close();
		} catch (Exception e)
		{
			log.error("Error sending HTTP message", e);
		}

		// close the connection, if necessary
		if (!request.isKeepAlive()) channel.close();
	}
}
