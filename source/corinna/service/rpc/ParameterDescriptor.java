package corinna.service.rpc;


public class ParameterDescriptor
{

	private int index;
	
	private String name;
	
	private Class<?> type;

	private boolean required;

	public ParameterDescriptor( String name, Class<?> type, int index, boolean required )
	{
		this.name = name;
		this.index = index;
		this.type = type;
		this.required = required;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public Class<?> getType()
	{
		return type;
	}

	public boolean isRequired()
	{
		return required;
	}
}
