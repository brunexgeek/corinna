package corinna.auth;


public interface IModulePipeline
{

	public ILoginModule getModule( String name );

	public ILoginModule[] getModules();
	
	public String getName();

}
