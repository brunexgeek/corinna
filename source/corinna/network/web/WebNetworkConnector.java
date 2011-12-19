package corinna.network.web;

import java.net.MalformedURLException;
import java.net.URL;

import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import corinna.network.IProtocol;
import corinna.network.NetworkConnector;
import corinna.network.http.HttpStreamHandler;
import corinna.util.Stateless;


public class WebNetworkConnector extends NetworkConnector<IWebBindletRequest, IWebBindletResponse>
{

	private HttpRequestDecoder decoder;

	private HttpChunkAggregator aggregator;

	private HttpResponseEncoder encoder;

	private ChunkedWriteHandler chunkedWriter;

	private WebStreamHandler channelHandler;
	
	public WebNetworkConnector( String name, String url, int workers ) throws MalformedURLException
	{
		this( name, new URL(url), workers );
	}
	
	public WebNetworkConnector( String name, URL address, int workers )
	{
		super(name, address, workers);
		
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
	public IProtocol<IWebBindletRequest, IWebBindletResponse> getProtocol()
	{
		return WebProtocol.getInstance();
	}
	
}
