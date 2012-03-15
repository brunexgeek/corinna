package corinna.bindlet;

import javax.bindlet.IBindletService;
import javax.bindlet.IComponentInformation;

import corinna.core.IService;


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
