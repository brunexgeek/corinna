package corinna.soap.bindlet;

import java.lang.reflect.Method;

import corinna.rpc.IPrototypeFilter;

// TODO: mover para 'corinna.soap.rpc'
public class SoapPrototypeFilter implements IPrototypeFilter
{

	@Override
	public String getMethodPrototype( Method method )
	{
		if (method == null) throw new NullPointerException("The method instance can not be null");
		
		return method.getName();
	}

}
