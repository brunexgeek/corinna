package corinna.auth;

import java.util.Map;


public class ProfileEntry implements IProfileEntry
{
	
	private Class<?> classRef;
	
	private ProfileEntryFlags flags;
	
	private Map<String, String> options;

	@Override
	public Class<?> getModuleClass()
	{
		return classRef;
	}

	@Override
	public ProfileEntryFlags getFlags()
	{
		return flags;
	}

	@Override
	public Map<String, String> getOptions()
	{
		return options;
	}

	@Override
	public ILoginModule getModule()
	{
		return null;
	}
		
}
