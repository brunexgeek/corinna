//package corinna.http.network;
//
//import javax.bindlet.http.HttpStatus;
//import javax.bindlet.http.IHttpBindletRequest;
//import javax.bindlet.http.IHttpBindletResponse;
//
//import org.jboss.netty.channel.Channel;
//import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
//import org.jboss.netty.handler.codec.http.HttpRequest;
//import org.jboss.netty.handler.codec.http.HttpResponse;
//import org.jboss.netty.handler.codec.http.HttpResponseStatus;
//
//import corinna.exception.AdapterException;
//import corinna.http.bindlet.HttpBindletRequest;
//import corinna.http.bindlet.HttpBindletResponse;
//import corinna.network.Adapter;
//import corinna.network.IAdapterConfig;
//import corinna.network.RequestEvent;
//import corinna.util.StateModel;
//import corinna.util.StateModel.Model;
//
//
//@StateModel(Model.STATELESS)
//public class HttpAdapter extends Adapter
//{
//
//	public HttpAdapter( IAdapterConfig config )
//	{
//		super(config);
//	}
//
//	@Override
//	public Class<?> getOutputResponseType()
//	{
//		return IHttpBindletRequest.class;
//	}
//
//	@Override
//	public Class<?> getOutputRequestType()
//	{
//		return IHttpBindletResponse.class;
//	}
//
//	@Override
//	public void onError( RequestEvent<?,?> event, Channel channel, Throwable exception )
//	{
//		try
//		{
//			IHttpBindletResponse response = (IHttpBindletResponse) event.getResponse();
//			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
//		} catch (Exception e)
//		{
//			// supress any error
//		}
//	}
//
//	@Override
//	public void onSuccess( RequestEvent<?,?> event, Channel channel )
//	{
//		try
//		{
//			IHttpBindletResponse response = (IHttpBindletResponse) event.getResponse();
//			IHttpBindletRequest request = (IHttpBindletRequest) event.getRequest();
//			
//			if (!event.isHandled())
//				response.sendError(HttpStatus.NOT_FOUND);
//			else
//				response.close();
//			// close the connection, if necessary
//			if (!request.isKeepAlive()) channel.close();
//		} catch (Exception e)
//		{
//			// supress any error
//		}
//	}
//
//	@Override
//	public RequestEvent<?, ?> translate( Object request, Object response, Channel channel )
//		throws AdapterException
//	{
//		if (request == null)
//			throw new NullPointerException("The request object can not be null");
//		
//		HttpRequest req = (HttpRequest)request;
//		HttpResponse res = null;
//		
//		if (response == null)
//			res = new DefaultHttpResponse( req.getProtocolVersion(), HttpResponseStatus.OK);
//		else
//			res = (HttpResponse)response;
//
//		IHttpBindletRequest r = new HttpBindletRequest(req);
//		IHttpBindletResponse p = new HttpBindletResponse(channel, res);
//		HttpRequestEvent aaa = new HttpRequestEvent(r, p);
//		return aaa;
//	}
//
//	@Override
//	public Class<?> getInputRequestType()
//	{
//		return HttpRequest.class;
//	}
//
//	@Override
//	public Class<?> getInputResponseType()
//	{
//		return HttpResponse.class;
//	}
//
//}
