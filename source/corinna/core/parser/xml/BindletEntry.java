package corinna.core.parser.xml;

import corinna.util.conf.ISection;
import corinna.util.conf.Section;


public class BindletEntry extends BasicEntry
{

	private ISection config;
	
	public BindletEntry( String name, String className, ISection config )
	{
		super(name, className);
		if (config == null) config = new Section("Parameters");
		this.config = config;
	}

	public ISection getConfig()
	{
		return config;
	}
	
}
