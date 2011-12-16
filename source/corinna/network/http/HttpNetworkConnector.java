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

import java.net.MalformedURLException;
import java.net.URL;

import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import corinna.exception.LifecycleException;
import corinna.network.IProtocol;
import corinna.network.NetworkConnector;
import corinna.util.Stateless;


public class HttpNetworkConnector extends NetworkConnector<IHttpBindletRequest, 
	IHttpBindletResponse>
{

	private HttpRequestDecoder decoder;

	private HttpChunkAggregator aggregator;

	private HttpResponseEncoder encoder;

	private ChunkedWriteHandler chunkedWriter;

	private HttpStreamHandler channelHandler;
	
	public HttpNetworkConnector( String name, String url, int workers ) throws MalformedURLException
	{
		this( name, new URL(url), workers );
	}
	
	public HttpNetworkConnector( String name, URL address, int workers )
	{
		super(name, address, workers);
		
		this.decoder = new HttpRequestDecoder(1024, 4096, 8192);
		this.encoder = new HttpResponseEncoder();
		this.aggregator = new HttpChunkAggregator(1024 * 1024);
		this.chunkedWriter = new ChunkedWriteHandler();
		if (HttpStreamHandler.class.isAnnotationPresent(Stateless.class))
			this.channelHandler = new HttpStreamHandler(this);
		else
			this.channelHandler = null;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		// create the default stateless pipeline for all channels
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", decoder);
		pipeline.addLast("aggregator", aggregator);
		pipeline.addLast("encoder", encoder);
		pipeline.addLast("chunkedWriter", chunkedWriter);

		HttpStreamHandler handler = channelHandler;
		if (handler == null) handler = new HttpStreamHandler(this);
		pipeline.addLast("handler", handler);

		return pipeline;
	}

	@Override
	protected void initInternal() throws LifecycleException
	{
	}

	@Override
	protected void startInternal() throws LifecycleException
	{
	}

	@Override
	protected void stopInternal() throws LifecycleException
	{
	}

	@Override
	protected void destroyInternal() throws LifecycleException
	{
	}

	@Override
	public IProtocol<IHttpBindletRequest, IHttpBindletResponse> getProtocol()
	{
		return HttpProtocol.getInstance();
	}
}
