/*
 * Copyright 2011-2012 Bruno Ribeiro
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


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import corinna.core.IComponentInterface;
import corinna.exception.InvalidRpcClassException;
import corinna.exception.InvalidRpcMethodException;
import corinna.exception.InvalidRpcTypeException;
import corinna.rpc.annotation.Parameter;
import corinna.rpc.annotation.RemoteMethod;


/**
 * Classe utilitária para validação de métodos e classes utilizados em chamadas de procedimento
 * remoto.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public class RpcValidator
{

	private RpcValidator()
	{
	}

	/**
	 * Verifica se o método especificado pode ser usado através do mecanismo de RPC. Para ser
	 * compatível com mecanismo de RPC, o método deve:
	 * <ul>
	 * <li>Ter acessibilidade pública (modificador de acesso <code>public</code>);</li>
	 * <li>Ser um método dinâmico;</li>
	 * <li>Retornar algum tipo primitivo (<code>Long</code>, <code>String</code>, etc) ou uma
	 * instância de {@link MultipleValueReturn}. Não pode retornar <code>void</code>;</li>
	 * <li>Não ser um método da classe raiz (<code>Object</code>);</li>
	 * </ul>
	 * 
	 * @param method
	 *            Instância <code>Method</code> do método a ser validado.
	 * @throws InvalidRpcMethodException
	 *             se o método não for compatível com o mecanismo de RPC.
	 * @throws InvalidRpcTypeException
	 *             se o tipo de retorno ou de algum parâmetro do método não for compatível com o
	 *             mecanismo de RPC.
	 */
	public static void validateMethod( Method method ) throws InvalidRpcMethodException,
		InvalidRpcTypeException
	{
		if (!method.isAnnotationPresent(RemoteMethod.class))
			throw new InvalidRpcMethodException("The method must be anottated as public procedure");

		// the method must be public
		if ( !Modifier.isPublic(method.getModifiers()) )
			throw new InvalidRpcMethodException("The method access modifier must be public");
		// the method can not be static
		if ( Modifier.isStatic(method.getModifiers()) )
			throw new InvalidRpcMethodException("The method can not be static");
		// the return type can not be void
		if ( method.getReturnType() == Void.class || method.getReturnType() == void.class )
			throw new InvalidRpcTypeException("The method return type can not be void");

		Annotation[][] paramAnnots = method.getParameterAnnotations();
		Class<?>[] paramTypes = method.getParameterTypes();

		// verifica cada parâmetro do método
		for (int i = 0; i < paramTypes.length; ++i)
		{
			// obtém a anotação do parâmetro atual
			Parameter parameter = (Parameter) getAnnotation(paramAnnots[i], Parameter.class);
			if (parameter == null)
				throw new InvalidRpcMethodException("The parameter " + i + " of method "
					+ method.getName() + " has no 'Parameter' annotation");
			// verifica se o nome especificado é válido
			String paramName = parameter.name();
			if (paramName == null || paramName.trim() == "")
				throw new InvalidRpcMethodException("The name of parameter " + i
					+ " of the method " + method.getName() + " can not be empty");
			// se o parâmetro não é público, ignora a verificação de tipos
			if (!parameter.isPublic()) continue;
			// verifica se o tipo especificado é válido
			if (!parameter.required() && paramTypes[i].isPrimitive())
				throw new InvalidRpcMethodException("The parameter " + i
					+ " of the method " + method.getName() + " can not be optional");
			/*if ( !TypeConverter.isSupportedType(paramTypes[i]) )
				throw new InvalidRpcTypeException("The parameter " + i
					+ " of the method " + method.getName() + " has an unsupported type");*/
		}
	}
	
	/**
	 * Returns a boolean value to indicate whether the given method can be accessed remotely via
	 * the RPC mechanism. A method is not accessible if have not a {@link RemoteMethod} annotation
	 * or if have the annotation whith {@link RemoteMethod#export()} field setted to <code>false</code>.
	 * 
	 * @param method Method to be checked.
	 * @return <code>true</code> if the method can be accessed remotely or <code>false</code> otherwise.
	 */
	public static boolean isRemoteMethod( Method method )
	{
		RemoteMethod annot = method.getAnnotation(RemoteMethod.class);
		return (annot != null && annot.export());
	}
	
	/**
	 * Verifica se a classe especificada é uma interface válida para uso através do mecanismo de
	 * RPC. Para ser válida, a classe deve ser uma interface e ter todos os seus métodos acessíveis
	 * remotamente validados pelo método {@link #validateMethod(Method)}.
	 * 
	 * @param intfClass
	 *            Instância <code>Class&lt;?&gt;</code> da classe a ser verificada.
	 * @see #checkmethod(Method)
	 * @throws InvalidRpcClassException
	 *             se a classe não é compatível com o mecanismo de RPC.
	 */
	public static void validateInterface( Class<?> intfClass ) throws InvalidRpcClassException
	{
		if (intfClass == null)
			throw new NullPointerException("The interface class must not be null");
		if (!intfClass.isInterface())
			throw new InvalidRpcClassException("The interface class must be an interface");
		// TODO: move this restriction to 'core' (the RPC mechanism should only require the 'RemoteComponent' annotation 
		if (!IComponentInterface.class.isAssignableFrom(intfClass))
			throw new InvalidRpcClassException(
				"The interface class must extends 'IComponentInterface'");

		Method[] methods = intfClass.getMethods();
		String name = "";
		IPrototypeFilter prototypeFilter = new QualifiedPrototypeFilter();
		List<String> methodList = new ArrayList<String>(methods.length);

		try
		{
			for (Method current : methods)
			{
				// check whether the current method should be exported
				if (!isRemoteMethod(current)) continue;
				// validate the current method
				validateMethod(current);
				
				String prototype = prototypeFilter.getMethodPrototype(current);
				if (methodList.contains(prototype))
					throw new InvalidRpcClassException("Equivalent method already registred: "
						+ prototype);
				methodList.add(prototype);
			}
		} catch (Exception e)
		{
			throw new InvalidRpcClassException("Invalid RPC method '" + name + "' of interface '"
				+ intfClass.getName() + "'", e);
		}
	}

	private static Annotation getAnnotation( Annotation annots[], Class<?> annotClass )
	{
		for (Annotation annot : annots)
			if (annotClass.isInstance(annot)) return (Annotation) annot;
		return null;
	}

	/**
	 * Valida uma classe como sendo a implementação de uma interface RPC.
	 * 
	 * @param intfClass
	 * @param implClass
	 * @return
	 * @throws InvalidRpcClassException
	 */
	public static void validateImplementation( Class<?> intfClass, Class<?> implClass )
		throws InvalidRpcClassException
	{
		if (intfClass == null || implClass == null)
			throw new InvalidRpcClassException("Both classes must not be null");

		// verifica se possui o construtor que recebe um objeto arbitrário
		/*try
		{
			implClass.getConstructor(Object.class);
		} catch (Exception e)
		{
			throw new InvalidRpcClassException("Missing implementation constructor in class '"
				+ implClass.getName() + "'", e);
		}*/

		// verifica se a classe de implementação é na verdade uma interface
		if (implClass.isInterface())
			throw new InvalidRpcClassException("The implementation class must be a concrete class");

		// verifica se a classe de implementação deriva da interface indicada
		if (!haveInterface(implClass, intfClass))
			throw new InvalidRpcClassException("The implementation class must implements the "
				+ "interface class");
	}

	/**
	 * Retorna um valor lógico indicando ser uma classe implementa a interface especificada.
	 * 
	 * @param classRef
	 *            Classe que será avaliada.
	 * @param intfRef
	 *            Interface que será procurada.
	 * @return <code>True</code> se a classe implementa a interface ou <code>false</cdoe> caso 
	 *     contrário.
	 */
	public static boolean haveInterface( Class<?> classRef, Class<?> intfRef )
	{
		return intfRef.isAssignableFrom(classRef);
	}

	/**
	 * Retorna um valor lógico indicando se uma classe possui um construtor público que aceite os
	 * argumentos especificados.
	 * 
	 * @param classRef
	 *            Classe que será avaliada.
	 * @param args
	 *            Argumentos que o construtor deve possuir.
	 * @return <code>True</code> se a classe implementa um construtor público com os argumentos
	 *         indicados ou <code>false</code> caso contrário.
	 */
	public static boolean haveConstructor( Class<?> classRef, Class<?> args[] )
	{
		try
		{
			Constructor<?> ctor = classRef.getConstructor(args);
			return Modifier.isPublic(ctor.getModifiers());
		} catch (Exception e)
		{
			return false;
		}
	}


}
