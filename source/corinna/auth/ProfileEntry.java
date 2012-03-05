package corinna.auth;

import java.util.Map;

import javax.security.auth.login.LoginException;

// TODO: rename to 'ModulePipelineEntry'
public class ProfileEntry implements IProfileEntry
{
	
	private Class<?> classRef;
	
	//private ProfileEntryFlags flags;
	
	private Map<String, String> options;

	private ILoginModule instance;

	public ProfileEntry( Class<?> classRef, Map<String, String> options ) throws LoginException
	{
		if (classRef == null)
			throw new IllegalArgumentException("The class reference can not be null");
		
		this.classRef = classRef;
		this.options = options;

		try
		{
			this.instance = (ILoginModule) classRef.newInstance();
		} catch (Exception e)
		{
			throw new LoginException("Error creating module instance: " + e.getMessage());
		}
	}
	
	@Override
	public Class<?> getModuleClass()
	{
		return classRef;
	}

	/*@Override
	public ProfileEntryFlags getFlags()
	{
		return flags;
	}*/

	@Override
	public Map<String, String> getOptions()
	{
		return options;
	}

	@Override
	public ILoginModule getModule()
	{
		return instance;
	}
		
}
