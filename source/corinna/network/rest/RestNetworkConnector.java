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
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import org.jboss.netty.channel.ChannelPipeline;
//import org.jboss.netty.channel.Channels;
//import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
//import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
//import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
//import org.jboss.netty.handler.stream.ChunkedWriteHandler;
//
//import corinna.bindlet.rest.IRestBindletRequest;
//import corinna.bindlet.rest.IRestBindletResponse;
//import corinna.exception.LifecycleException;
//import corinna.network.IProtocol;
//import corinna.network.NetworkConnector;
//import corinna.util.Stateless;
//
//
//public class RestNetworkConnector extends NetworkConnector<IRestBindletRequest, IRestBindletResponse>
//{
//
//	private HttpRequestDecoder decoder;
//
//	private HttpChunkAggregator aggregator;
//
//	private HttpResponseEncoder encoder;
//
//	private ChunkedWriteHandler chunkedWriter;
//
//	private RestStreamHandler channelHandler;
//	
//	public RestNetworkConnector( String name, String url ) throws MalformedURLException
//	{
//		this( name, new URL(url) );
//	}
//	
//	public RestNetworkConnector( String name, URL address )
//	{
//		super(name, address);
//		
//		this.decoder = new HttpRequestDecoder(1024, 4096, 8192);
//		this.encoder = new HttpResponseEncoder();
//		this.aggregator = new HttpChunkAggregator(1024 * 1024);
//		this.chunkedWriter = new ChunkedWriteHandler();
//		if (RestStreamHandler.class.isAnnotationPresent(Stateless.class))
//			this.channelHandler = new RestStreamHandler(this);
//		else
//			this.channelHandler = null;
//	}
//	
//	@Override
//	public ChannelPipeline getPipeline() throws Exception
//	{
//		// create the default stateless pipeline for all channels
//		ChannelPipeline pipeline = Channels.pipeline();
//		pipeline.addLast("decoder", decoder);
//		pipeline.addLast("aggregator", aggregator);
//		pipeline.addLast("encoder", encoder);
//		pipeline.addLast("chunkedWriter", chunkedWriter);
//
//		RestStreamHandler handler = channelHandler;
//		if (handler == null) handler = new RestStreamHandler(this);
//		pipeline.addLast("handler", handler);
//
//		return pipeline;
//	}
//
//	@Override
//	protected void initInternal() throws LifecycleException
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	protected void startInternal() throws LifecycleException
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	protected void stopInternal() throws LifecycleException
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	protected void destroyInternal() throws LifecycleException
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public IProtocol<IRestBindletRequest, IRestBindletResponse> getProtocol()
//	{
//		return RestProtocol.getInstance();
//	}
//
//}
