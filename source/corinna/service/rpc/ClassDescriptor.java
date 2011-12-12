package corinna.service.rpc;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import corinna.exception.InvalidRpcClassException;


public class ClassDescriptor
{

	private List<MethodDescriptor> methodList;
	
	public ClassDescriptor( Class<?> clazz ) throws InvalidRpcClassException
	{
		String name = "";
		
		methodList = new LinkedList<MethodDescriptor>();
		
		Method[] methods = clazz.getMethods();
		try
		{
			for (Method current : methods)
			{
				if (!current.isAnnotationPresent(PublicProcedure.class)) continue;

				name = current.getName();
				MethodDescriptor method = new MethodDescriptor(current);
				methodList.add(method);
			}
		} catch (Exception e)
		{
			throw new InvalidRpcClassException("Invalid RPC method '" + name + "' of interface '"
				+ clazz.getName() + "'", e);
		}
	}
	
	public MethodDescriptor[] getMethods()
	{
		return methodList.toArray( new MethodDescriptor[0] );
	}
	
}
