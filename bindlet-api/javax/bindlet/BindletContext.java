package javax.bindlet;



public abstract class BindletContext implements IBindletContext
{
	
	private String name;
	
	public BindletContext( String name )
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("The context name can not be null or empty");
		this.name = name;
	}
	
	@Override
	public String getContextName()
	{
		return name;
	}

}
