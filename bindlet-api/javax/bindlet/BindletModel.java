package javax.bindlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BindletModel
{

	public Model value() default Model.STATEFULL;
	
	public enum Model
	{
		
		STATELESS,
		
		STATEFULL,
		
		RECYCLABLE
				
	}
	
}
