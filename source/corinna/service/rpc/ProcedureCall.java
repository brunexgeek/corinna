/*
 * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
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

package corinna.service.rpc;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import corinna.exception.IncompleteInterfaceException;
import corinna.exception.MethodNotFoundException;


/**
 * Armazena as informações de uma chamada de método remoto.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 * @version 1.0
 */
public class ProcedureCall implements IProcedureCall
{

	public static final String PAIR_SEPARATOR = "&";

	public static final String KEYVALUE_SEPARATOR = "=";

	private LinkedHashMap<String, Object> parameterValues = null;

	private LinkedList<String> parameterNames = null;

	private String methodPrototype = null;

	public ProcedureCall( String prototype )
	{
		this(prototype, null);
	}
	
	public ProcedureCall( String prototype, ParameterList parameters )
	{
		methodPrototype = prototype;
		
		parameterValues = new LinkedHashMap<String, Object>(4);
		parameterNames = new LinkedList<String>();
		
		if (parameters != null)
			for (String key : parameters.getParameterNames())
				setParameter(key, parameters.getParameter(key, "").toString());
	}

	@Override
	public String getMethodPrototype()
	{
		return methodPrototype;
	}

	@Override
	public Object getParameter( String name )
	{
		return parameterValues.get(name);
	}

	@Override
	public Object getParameter( int index )
	{
		if (parameterNames.size() <= index) return null;
		return getParameter(parameterNames.get(index));
	}

	@Override
	public Object[] getParameterValues()
	{
		return parameterValues.values().toArray();
	}

	@Override
	public String[] getParameterNames()
	{
		return parameterNames.toArray( new String[0] );
	}

	public void setParameter( String name, Object value )
	{
		if (name == null) return;
		parameterValues.put(name, value);
		if (!parameterNames.contains(name))
			parameterNames.add(name);
	}

	public Object removeParameter( String name )
	{
		if (name == null) return null;
		parameterNames.remove(name);
		return parameterValues.remove(name);
	}
	
	@Override
	public String getParameterName( int index )
	{
		if (parameterNames.size() <= index) return null;
		return parameterNames.get(index);
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("[Method: ");
		sb.append(getMethodPrototype());
		
		String[] params = getParameterNames();
		if (params.length > 0)
		{
			sb.append("; Parameters: ");
		
			for (int c = 0; c < params.length; ++c)
			{
				String param = params[c];

				sb.append(param);
				sb.append("=\"");
				sb.append(getParameter(param));
				sb.append("\"");
				if (c < params.length-1) sb.append(" ");
			}
		}
		sb.append("]");

		return sb.toString();
	}
	
	/**
	 * Retorna uma requisição de chamada de procedimento contendo as informações necessárias para
	 * invocar um método especificado.
	 * 
	 * @param method
	 * @param values
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IncompleteInterfaceException
	 */
	public static IProcedureCall fromMethod( IPrototypeFilter filter, Method method, Object[] values )
		throws MethodNotFoundException, IncompleteInterfaceException
	{
		int c = 0;

		Annotation[][] params = method.getParameterAnnotations();
		if (params.length != values.length)
			throw new MethodNotFoundException("Incompatible number of arguments");
		String prototype = filter.getMethodPrototype(method);
		ProcedureCall req = new ProcedureCall(prototype);
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
			if (values[c] != null) req.setParameter(name, values[c].toString());
			c++;
		}

		return req;
	}


}
