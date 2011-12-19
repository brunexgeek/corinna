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

package corinna.network.http;

import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import corinna.bindlet.http.HttpBindletRequest;
import corinna.bindlet.http.HttpBindletResponse;
import corinna.network.RequestEvent;
import corinna.network.StreamHandler;
import corinna.util.StateModel;
import corinna.util.StateModel.Model;


@StateModel(Model.STATELESS)
public class HttpStreamHandler extends StreamHandler
{

	private HttpNetworkConnector connector;
	
	public HttpStreamHandler( HttpNetworkConnector connector )
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
		
		HttpBindletRequest req = new HttpBindletRequest(request);
		HttpBindletResponse res = new HttpBindletResponse(event.getChannel(), request.getProtocolVersion() );

		// dispatch the request event to network connector
		RequestEvent<IHttpBindletRequest, IHttpBindletResponse> e = new HttpRequestEvent(req, res);
		try
		{
			connector.handlerRequestReceived(this, e);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		// check if no bindlets handle this request
		if (!e.isHandled())
			// send 'HTTP 404' to client
			res.sendError(HttpStatus.NOT_FOUND);
		else
			// flush the HTTP response content
			res.close();
		// close the connection, if necessary
		if (!HttpHeaders.isKeepAlive(request))
			event.getChannel().close();
	}
	
}
