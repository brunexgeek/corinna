//package corinna.soap.core;
//
//import javax.bindlet.IBindletContext;
//import javax.bindlet.IComponentInformation;
//import javax.bindlet.soap.ISoapBindletRequest;
//import javax.bindlet.soap.ISoapBindletResponse;
//
//import corinna.core.IBindletRegistration;
//import corinna.core.IContextConfig;
//import corinna.core.IService;
//import corinna.http.core.WebContext;
//import corinna.soap.bindlet.SoapBindletContext;
//
//
//public class SoapContext extends WebContext<ISoapBindletRequest,ISoapBindletResponse>
//{
//	
//	private SoapBindletContext bindletContext = null;
//	
//	public SoapContext( IContextConfig config, IService service )
//	{
//		super(config, service);
//	}
//
//	@Override
//	protected IBindletContext createBindletContext()
//	{
//		if (bindletContext == null)
//			bindletContext = new SoapBindletContext(this);
//		return bindletContext;
//	}
//
//	@Override
//	public IComponentInformation getContextInfo()
//	{
//		return SoapContextInfo.getInstance();
//	}
//
//	@Override
//	public Class<?> getRequestType()
//	{
//		return ISoapBindletRequest.class;
//	}
//	
//	@Override
//	public Class<?> getResponseType()
//	{
//		return ISoapBindletResponse.class;
//	}
//
//	@Override
//	public IBindletRegistration getBindletRegistration( ISoapBindletRequest request )
//	{
//		return findRegistration(request);
//	}
//
//	@Override
//	protected boolean acceptRequest( ISoapBindletRequest request )
//	{
//		return matchContextPath(request);
//	}
//	
//}
