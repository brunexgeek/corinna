package corinna.soap.network;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import corinna.network.IAdapterFilter;


public class SoapAdapterFilter implements IAdapterFilter
{

	@Override
	public boolean evaluate( Object request, Object response )
	{
		HttpRequest req = (HttpRequest) request;
		String value = req.getHeader(HttpHeaders.Names.CONTENT_TYPE);

		return (value != null && value.contains("text/xml")) || 
			req.getUri().endsWith("?wsdl");
	}

}
