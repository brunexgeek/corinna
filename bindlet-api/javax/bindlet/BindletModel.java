package javax.bindlet;


public @interface BindletModel
{

	public Model model() default Model.STATEFULL;
	
	public enum Model
	{
		
		STATELESS,
		
		STATEFULL,
		
		RECYCLABLE
				
	}
	
}
