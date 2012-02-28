//package corinna.network.http;
//
//
//import javax.bindlet.http.HttpStatus;
//import javax.bindlet.http.IWebBindletRequest;
//import javax.bindlet.http.IWebBindletResponse;
//
//import org.jboss.netty.channel.ChannelHandlerContext;
//import org.jboss.netty.channel.MessageEvent;
//import org.jboss.netty.handler.codec.http.HttpHeaders;
//import org.jboss.netty.handler.codec.http.HttpRequest;
//import org.jboss.netty.handler.codec.http.HttpVersion;
//
//import corinna.bindlet.http.HttpBindletResponse;
//import corinna.network.RequestEvent;
//import corinna.network.StreamHandler;
//
//
//public abstract class BasicStreamHandler extends StreamHandler
//{
//
//	private WebNetworkConnector connector;
//
//	public BasicStreamHandler( WebNetworkConnector connector )
//	{
//		if (connector == null)
//			throw new NullPointerException("The network connector can not be null");
//		this.connector = connector;
//	}
//
//	protected abstract IWebBindletRequest createRequest( MessageEvent event );
//
//	protected abstract IWebBindletResponse createResponse( MessageEvent event );
//
//	@Override
//	public void incomingMessage( ChannelHandlerContext context, MessageEvent event )
//		throws Exception
//	{
//		HttpRequest request = (HttpRequest) event.getMessage();
//
//		IWebBindletRequest req = createRequest(event);
//		IWebBindletResponse res = createResponse(event);
//
//		if (req != null && res != null)
//		{
//			// dispatch the request event to network connector
//			RequestEvent<IWebBindletRequest, IWebBindletResponse> e = new WebRequestEvent(req, res);
//			try
//			{
//				connector.handlerRequestReceived(this, e);
//			} catch (Exception ex)
//			{
//				ex.printStackTrace();
//			}
//	
//			// check if no bindlets handle this request
//			if (!e.isHandled())
//				// send 'HTTP 404' to client
//				res.sendError(HttpStatus.NOT_FOUND);
//			else
//				// flush the HTTP response content
//				res.close();
//		}
//		else
//		{
//			res = new HttpBindletResponse(event.getChannel(), HttpVersion.HTTP_1_1);
//			res.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		// close the connection, if necessary
//		if (!HttpHeaders.isKeepAlive(request)) event.getChannel().close();
//	}
//
//}
