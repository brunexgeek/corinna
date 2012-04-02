package corinna.service.rpc;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import corinna.exception.InvalidRpcClassException;
import corinna.service.rpc.annotation.RemoteComponent;
import corinna.service.rpc.annotation.RemoteMethod;


public class ClassDescriptor
{

	private List<MethodDescriptor> methodList;
	
	private Class<?> type;
	
	private String componentName = "";
	
	public ClassDescriptor( Class<?> clazz ) throws InvalidRpcClassException
	{
		String name = "";
		
		methodList = new LinkedList<MethodDescriptor>();
		type = clazz;
		
		RemoteComponent annotation = clazz.getAnnotation(RemoteComponent.class);
		if (annotation != null && annotation.name() != null && !annotation.name().isEmpty())
			componentName = cleanName( annotation.name() );
		else
			componentName = type.getSimpleName();
		
		Method[] methods = clazz.getMethods();
		try
		{
			for (Method current : methods)
			{
				if (!current.isAnnotationPresent(RemoteMethod.class)) continue;

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
	
	private String cleanName( String name )
	{
		int c = 0;
		byte text[] = name.getBytes();
		
		for (c = 0; c < text.length; ++c)
			if ( !( (text[c] >= 65 && text[c] <= 90) || (text[c] >= 97 && text[c] <= 122)) )
				text[c] = 95;
		
		return new String(text);
	}

	public Class<?> getType()
	{
		return type;
	}
	
	public String getClassName()
	{
		return type.getName();
	}
	
	public String getName()
	{
		return componentName;
	}
	
	public MethodDescriptor[] getMethods()
	{
		return methodList.toArray( new MethodDescriptor[0] );
	}
	
}
