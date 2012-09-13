//package corinna.rest.network;
//
//import org.jboss.netty.handler.codec.http.HttpHeaders;
//import org.jboss.netty.handler.codec.http.HttpRequest;
//import org.jboss.netty.handler.codec.http.HttpResponse;
//
//import corinna.network.IAdapterFilter;
//
//
//public class RestAdapterFilter implements IAdapterFilter
//{
//
//	private static final String MIME_TYPE = "application/x-www-form-urlencoded";
//	//private static final String MIME_TYPE = "text/html";
//
//	@Override
//	public boolean evaluate( Object request, Object response )
//	{
//		boolean valid;
//
//		valid  = HttpRequest.class.isAssignableFrom(request.getClass());
//		valid &= HttpResponse.class.isAssignableFrom(response.getClass());
//		if (!valid) return false;
//
//		String value = ((HttpRequest)request).getHeader(HttpHeaders.Names.CONTENT_TYPE);
//
//		return (value != null && value.contains(MIME_TYPE));
//	}
//
//}
