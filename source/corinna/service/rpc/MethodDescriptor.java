package corinna.service.rpc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import corinna.exception.IncompleteInterfaceException;


public class MethodDescriptor
{

	private String name;
	
	private List<ParameterDescriptor> parameterList;

	private Class<?> returnType;
	
	public MethodDescriptor( Method method ) throws IncompleteInterfaceException
	{
		if (method == null)
			throw new NullPointerException("The method instance can not be null");

		name = method.getName();
		returnType = method.getReturnType();
		this.parameterList = new LinkedList<ParameterDescriptor>();
		
		int c = 0;

		Annotation[][] params = method.getParameterAnnotations();
		Class<?>[] types = method.getParameterTypes();
		
		for (Annotation[] annotations : params)
		{
			// verifica se existe a anotação com o nome do parâmetro
			Parameter annotation = null;
			for (Annotation current : annotations)
			{
				if (current instanceof Parameter)
				{
					annotation = (Parameter) current;
					break;
				}
			}
			if (annotation == null)
				throw new IncompleteInterfaceException("Some parameters were not annotated.");

			// insere o valor do parâmetro na requisição
			String name = annotation.name().trim();
			if (name == null || name.isEmpty())
				throw new IncompleteInterfaceException(
					"Some parameters were annotated with blank name.");
			
			ParameterDescriptor param = new ParameterDescriptor(name, types[c], /*c,*/ 
				annotation.required(), annotation.isPublic());
			parameterList.add(param);
			
			c++;
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getParameterCount()
	{
		return parameterList.size();
	}
	
	public ParameterDescriptor getParameter( int index )
	{
		if (index > parameterList.size() || index < 0) return null;
		return parameterList.get(index);
	}
	
	public ParameterDescriptor getParameter( String name )
	{
		for (ParameterDescriptor param : parameterList)
			if (param.getName().equalsIgnoreCase(name)) return param;
		return null;
	} 
	
	public Class<?> getReturnType()
	{
		return returnType;
	}
	
}
