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

/**
 * Generate a full qualified prototype for a method. The prototype only will contain the characters
 * allowed by the RFC 3986 (URI) standard.
 * 
 * @author Bruno Ribeiro
 */
public class QualifiedPrototypeFilter implements IPrototypeFilter
{

	private static final Class<?>[] TYPE_CLASSES = { Integer.class, Float.class, Long.class,
		String.class, Double.class, Boolean.class, Byte.class,
		Short.class, int.class, float.class, long.class, double.class, boolean.class, 
		byte.class, short.class };

	private static final String TYPE_NAMES[] = { "I", "F", "L", "T", "D", "N", "B", "S", "I", 
		"F", "L", "D", "N", "B", "S" };

	@Override
	public String getMethodPrototype( Method method )
	{
		if (method == null) throw new NullPointerException("The method instance can not be null");

		StringBuffer sb = new StringBuffer();
		sb.append(method.getDeclaringClass().getName());
		sb.append(".");
		sb.append(method.getName());
		sb.append("(");

		Class<?> paramTypes[] = method.getParameterTypes();

		for (int i = 0; i < paramTypes.length; ++i)
			sb.append(getTypeName(paramTypes[i]));
		sb.append(")");
		sb.append(getTypeName(method.getReturnType()));

		return sb.toString();
	}

	protected static String getTypeName( Class<?> type )
	{
		int k;

		for (k = 0; k < TYPE_NAMES.length && !TYPE_CLASSES[k].equals(type); ++k);

		if (k < TYPE_NAMES.length)
			return TYPE_NAMES[k];
		else
			return "C" + type.getName() + ";";
	}
}
