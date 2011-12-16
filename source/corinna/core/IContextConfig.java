package corinna.core;


public interface IContextConfig
{

	public String getContextName();

	public String getInitParameter( String name );

	public String[] getInitParameterNames();

	public Class<?> getContextClass();

}
