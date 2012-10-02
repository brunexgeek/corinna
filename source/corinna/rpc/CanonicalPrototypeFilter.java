package corinna.rpc;

import java.lang.reflect.Method;


public class CanonicalPrototypeFilter implements IPrototypeFilter
{

	@Override
	public String getMethodPrototype( Method method )
	{
		if (method == null) throw new NullPointerException("The method instance can not be null");
		
		return method.getName();
	}

}
