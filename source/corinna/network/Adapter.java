package corinna.network;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import corinna.thread.ObjectLocker;


public abstract class Adapter implements IAdapter
{

	private static final Logger serverLog = Logger.getLogger(Adapter.class);
	
	private IAdapterConfig config;
	
	private List<IAdapterFilter> filters;
	
	private ObjectLocker filtersLock;
	
	public Adapter( IAdapterConfig config )
	{
		if (config == null)
			throw new IllegalArgumentException("The adapter configuration object can not be null");
		
		this.config = config;
		this.filters = new LinkedList<IAdapterFilter>();
		this.filtersLock = new ObjectLocker();
	}
	
	public IAdapterConfig getConfig()
	{
		return config;
	}
	
	@Override
	public String getName()
	{
		return config.getAdapterName();
	}
	
	@Override
	public boolean evaluate( Object request, Object response )
	{
		if (request == null || response == null) return false;
		
		// check if the supported input types are compatible with the specified objects
		Class<?> inputRequest = getInputRequestType();
		Class<?> inputResponse = getInputResponseType();
		if (inputRequest == null || inputResponse == null) return false;
		if (!inputRequest.isAssignableFrom(request.getClass()) ||
			!inputResponse.isAssignableFrom(response.getClass())) return false;

		filtersLock.readLock();
		try
		{
			serverLog.trace("Calling adapter filters for '" + getName() + "'");
			for (IAdapterFilter filter : filters)
				if (filter.evaluate(request, response)) return true;
		} catch (Throwable e)
		{
			// supress any error
			return false;
		} finally
		{
			filtersLock.readUnlock();
		}

		return false;
	}
	
	@Override
	public void addFilter( IAdapterFilter filter )
	{
		filtersLock.readLock();
		try
		{
			filters.add(filter);
		} finally
		{
			filtersLock.readUnlock();
		}
	}
	
	@Override
	public void removeFilter( IAdapterFilter filter )
	{
		filtersLock.readLock();
		try
		{
			filters.remove(filter);
		} finally
		{
			filtersLock.readUnlock();
		}
	}
	
}
