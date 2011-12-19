package corinna.bindlet;

import javax.bindlet.IBindletService;

import corinna.core.IService;
import corinna.util.IComponentInformation;


public class BindletService extends ObjectSharing implements IBindletService
{

	private IService service;

	public BindletService( IService service )
	{
		this.service = service;
	}
	
	@Override
	public IComponentInformation getServiceInfo()
	{
		return service.getServiceInfo();
	}

	@Override
	public String getServiceName()
	{
		return service.getName();
	}

}
