package corinna.core.parser.xml;

import java.util.List;


public interface IFilterContainer
{

	public void addFilter( String className );
	
	public List<String> getFilters();
	
	
}
