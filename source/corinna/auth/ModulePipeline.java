package corinna.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ModulePipeline implements IModulePipeline
{

	private List<ILoginModule> modules;

	private String name;
	
	private static Map<String, ModulePipeline> pipelines;
	
	static
	{
		pipelines = new HashMap<String, ModulePipeline>();
	}
	
	public ModulePipeline( String name )
	{
		if (name == null)
			throw new IllegalArgumentException("The pipeline name can not be null");
		
		this.modules = new ArrayList<ILoginModule>();
		this.name = name;
		
		pipelines.put(name, this);
	}
	
	public void addModule( ILoginModule module )
	{
		modules.add(module);
	}
	
	public void removeModule( ILoginModule module )
	{
		modules.add(module);
	}
	
	public void removeModule( String name )
	{
		ILoginModule module = getModule(name);		
		if (module != null) modules.remove(module);
	}
	
	@Override
	public ILoginModule getModule( String name )
	{
		for (ILoginModule entry : modules)
			if (entry.getName().equals(name))
				return entry;
		return null;
	}
	
	
	public static IModulePipeline getModulePipeline( String name )
	{
		return pipelines.get(name);
	}
	
	public static IModulePipeline[] getModulePipelines( )
	{
		return pipelines.values().toArray( new ModulePipeline[0] );
	}
	
	@Override
	public ILoginModule[] getModules()
	{
		return modules.toArray( new ILoginModule[0] );
	}

	@Override
	public String getName()
	{
		return name;
	}
	

	
}
