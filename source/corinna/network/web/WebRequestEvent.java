package corinna.network.web;

import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import corinna.network.RequestEvent;


public class WebRequestEvent extends RequestEvent<IWebBindletRequest, IWebBindletResponse>
{

	private static final long serialVersionUID = 6332175192769330587L;

	public WebRequestEvent( IWebBindletRequest request, IWebBindletResponse response )
	{
		super(request, response);
	}
	
}
