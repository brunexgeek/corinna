package corinna.network.web;

import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import corinna.network.IProtocol;
import corinna.network.NetworkConfig;
import corinna.network.NetworkConnector;
import corinna.network.http.HttpStreamHandler;
import corinna.util.StateModel;
import corinna.util.Stateless;
import corinna.util.StateModel.Model;


public class WebNetworkConnector extends NetworkConnector
{

	private HttpRequestDecoder decoder;

	private HttpChunkAggregator aggregator;

	private HttpResponseEncoder encoder;

	private ChunkedWriteHandler chunkedWriter;

	private WebStreamHandler channelHandler;
	
	public WebNetworkConnector( NetworkConfig config )
	{
		super(config);
		
		this.decoder = new HttpRequestDecoder(1024, 4096, 8192);
		this.encoder = new HttpResponseEncoder();
		this.aggregator = new HttpChunkAggregator(1024 * 1024);
		this.chunkedWriter = new ChunkedWriteHandler();
		if (HttpStreamHandler.class.isAnnotationPresent(Stateless.class))
			this.channelHandler = new WebStreamHandler(this);
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

		WebStreamHandler handler = channelHandler;
		if (handler == null) handler = new WebStreamHandler(this);
		pipeline.addLast("handler", handler);

		return pipeline;
	}
	
	@Override
	public IProtocol<?, ?> getProtocol()
	{
		return WebProtocol.getInstance();
	}
	
}
