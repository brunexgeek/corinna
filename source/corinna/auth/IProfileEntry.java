package corinna.auth;

import java.util.Map;


public interface IProfileEntry
{

	public abstract Map<String, String> getOptions();

	//public abstract ProfileEntryFlags getFlags();

	public abstract Class<?> getModuleClass();

	public ILoginModule getModule();
		
	/*public enum ProfileEntryFlags
	{
		
		REQUIRED,
		
		OPTIONAL, 
		
		SUFFICIENT, 
		
		REQUISITE
		
	}*/
	
}
