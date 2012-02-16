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

import corinna.bindlet.rest.IRestBindletRequest;
import corinna.bindlet.rest.IRestBindletResponse;
import corinna.core.INetworkConnectorConfig;
import corinna.exception.LifecycleException;
import corinna.network.IProtocol;
import corinna.network.NetworkConfig;
import corinna.network.NetworkConnector;
import corinna.network.http.HttpProtocol;
import corinna.network.http.HttpStreamHandler;
import corinna.util.StateModel;
import corinna.util.Stateless;
import corinna.util.StateModel.Model;


public class RestNetworkConnector extends NetworkConnector
{

	/*rivate HttpRequestDecoder decoder;

	private HttpChunkAggregator aggregator;

	private HttpResponseEncoder encoder;

	private ChunkedWriteHandler chunkedWriter;*/

	private RestStreamHandler channelHandler;
	
	public RestNetworkConnector( INetworkConnectorConfig config )
	{
		super(config);
		
		// TODO: create stateless (or pooled) decoders and encoders 
		/*this.decoder = new HttpRequestDecoder(1024, 4096, 8192);
		this.encoder = new HttpResponseEncoder();
		this.aggregator = new HttpChunkAggregator(1024 * 1024);
		this.chunkedWriter = new ChunkedWriteHandler();*/
		
		StateModel state = HttpStreamHandler.class.getAnnotation(StateModel.class);
		if (state != null && state.value() == Model.STATELESS)
			this.channelHandler = new RestStreamHandler(this);
		else
			this.channelHandler = null;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		// create the default stateless pipeline for all channels
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", new HttpRequestDecoder(1024, 4096, 8192));
		pipeline.addLast("aggregator", new HttpChunkAggregator(1024 * 1024));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

		RestStreamHandler handler = channelHandler;
		if (handler == null) handler = new RestStreamHandler(this);
		pipeline.addLast("handler", handler);

		return pipeline;
	}

	@Override
	public IProtocol<IRestBindletRequest, IRestBindletResponse> getProtocol()
	{
		return RestProtocol.getInstance();
	}
}
