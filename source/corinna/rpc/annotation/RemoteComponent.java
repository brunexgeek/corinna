package corinna.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Allow to identify the service component in the protocol interface.
 * 
 * @author Bruno Ribeiro
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RemoteComponent
{

	public String name() default "";
		
}
