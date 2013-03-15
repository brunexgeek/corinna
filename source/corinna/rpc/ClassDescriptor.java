/*
 * Copyright 2011-2013 Bruno Ribeiro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package corinna.rpc;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import corinna.exception.InvalidRpcClassException;
import corinna.rpc.annotation.RemoteComponent;


/**
 * Hold information about a class to be used by the RPC mechanism.
 * 
 * @author Bruno Ribeiro
 */
// TODO: rename to 'ComponentDescriptor'
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
		if (annotation == null)
			throw new InvalidRpcClassException("Component classes must be annotated with 'RemoteComponent' annotation");
		if (annotation.name() != null && !annotation.name().isEmpty())
			componentName = cleanName( annotation.name() );
		else
			componentName = type.getSimpleName();
		
		Method[] methods = clazz.getMethods();
		try
		{
			for (Method current : methods)
			{
				// check whether the current method should be exported
				if (!RpcValidator.isRemoteMethod(current)) continue;

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
