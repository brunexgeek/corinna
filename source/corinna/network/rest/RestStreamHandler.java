///*
// * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package corinna.network.rest;
//
//
//import javax.bindlet.http.HttpStatus;
//
//import org.jboss.netty.channel.ChannelHandlerContext;
//import org.jboss.netty.channel.MessageEvent;
//import org.jboss.netty.handler.codec.http.HttpHeaders;
//import org.jboss.netty.handler.codec.http.HttpRequest;
//
//import corinna.bindlet.rest.RestBindletRequest;
//import corinna.bindlet.rest.RestBindletResponse;
//import corinna.network.StreamHandler;
//import corinna.util.StateModel;
//import corinna.util.StateModel.Model;
//
//
//@StateModel(Model.STATELESS)
//public class RestStreamHandler extends StreamHandler
//{
//
//	private RestNetworkConnector connector;
//	
//	public RestStreamHandler( RestNetworkConnector connector )
//	{
//		if (connector == null)
//			throw new NullPointerException("The network connector can not be null");
//		this.connector = connector;
//	}
//
//	@Override
//	public void incomingMessage( ChannelHandlerContext context, MessageEvent event )
//		throws Exception
//	{
//		HttpRequest request = (HttpRequest) event.getMessage();
//		
//		RestBindletRequest req = new RestBindletRequest(request);
//		RestBindletResponse res = new RestBindletResponse(event.getChannel(), request.getProtocolVersion() );
//
//		// dispatch the request event to network connector
//		RestRequestEvent e = new RestRequestEvent(req, res);
//		try
//		{
//			connector.handlerRequestReceived(this, e);
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//		}
//		
//		// check if no bindlets handle this request
//		if (!e.isHandled())
//			// send 'HTTP 404' to client
//			res.sendError(HttpStatus.NOT_FOUND);
//		else
//			// flush the HTTP response content
//			res.close();
//		// close the connection, if necessary
//		if (!HttpHeaders.isKeepAlive(request))
//			event.getChannel().close();
//	}
//	
//	/*private RestNetworkConnector connector;
//
//	public RestStreamHandler( RestNetworkConnector connector )
//	{
//		if (connector == null)
//			throw new NullPointerException("The network connector can not be null");
//		this.connector = connector;
//	}
//
//	@Override
//	public void incomingMessage( ChannelHandlerContext context, MessageEvent event )
//		throws Exception
//	{
//		HttpRequest request = (HttpRequest) event.getMessage();
//		HttpResponse response = null;
//		Charset charset = getCharset(request);
//
//		IRestBindletRequest req = new RestBindletRequest(request);
//		IRestBindletResponse res = new RestBindletResponse(event.getChannel(), request.getProtocolVersion() );
//
//		RequestEvent<IRestBindletRequest, IRestBindletResponse> e = new RestRequestEvent(req, res);
//		try
//		{
//			connector.handlerRequestReceived(this, e);
//		} catch (Exception ex)
//		{
//			res.setException(ex);
//			ex.printStackTrace();
//		}
//		
//		response = res.getResponse();
//		ChannelFuture writeFuture = event.getChannel().write(response);
//		// decide whether to close the connection or not.
//		if (!isKeepAlive(request))
//			// close the connection when the whole content is written out.
//			writeFuture.addListener(ChannelFutureListener.CLOSE);
//	}
//
//	protected Charset getCharset( HttpRequest request )
//	{
//		String value = request.getHeader( HttpHeaders.Names.CONTENT_ENCODING);
//		if (value != null)
//		{
//			try
//			{
//				return Charset.forName(value);
//			} catch (Exception e)
//			{
//				// supress any error
//			}
//		}
//		return Charset.defaultCharset();
//	}*/
//	
//}
