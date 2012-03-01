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

package corinna.http.network;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import corinna.network.StreamHandler;
import corinna.util.StateModel;
import corinna.util.StateModel.Model;


@StateModel(Model.STATELESS)
public class HttpStreamHandler extends StreamHandler
{

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
		HttpRequest request = (HttpRequest) event.getMessage();
		HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);

		try
		{
			connector.handlerRequestReceived(this, request, response, context.getChannel());
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}
