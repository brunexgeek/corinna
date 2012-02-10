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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import corinna.core.IServiceInterface;
import corinna.exception.IncompleteImplementationException;
import corinna.exception.IncompleteInterfaceException;
import corinna.exception.InternalException;
import corinna.exception.InvalidRpcClassException;
import corinna.exception.InvocationTargetException;
import corinna.exception.MethodNotFoundException;
import corinna.exception.ParameterNotFoundException;

import corinna.util.StringResource;


/**
 * Permite executar métodos de uma implementação através de seus nomes totalmente qualificados. O
 * nome do método deve ser fornecido em função de uma interface e mapeado para sua implementação
 * correspondente. <br/>
 * <br/>
 * Uma vez que os métodos devem ser dinâmicos, é necessário uma instância da classe de
 * implementação. A instância da implementação pode ser reusável, ou seja, uma única instância será
 * utilizada para efetuar várias invocações independentes. Caso a a implementação não seja reusável,
 * a cada invocação de método um novo objeto de implementação é criado e após a chamada o mesmo é
 * descartado (e liberado pelo GC no momento apropriado). Adicionalmente, na instânciação da classe
 * de implementação é possível especificar um objeto de dados, de tipo arbitrário, que os métodos
 * podem utilizar.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public class MethodRunner implements IMethodRunner
{

	private Logger serverLog = Logger.getLogger(MethodRunner.class);

	private static final Class<?>[] ARGS_IMPL = new Class<?>[] { Object.class };

	/**
	 * Tabela que associa os nomes de métodos contidos na interface com suas respectivas
	 * implementações.
	 */
	protected Map<String, Method> procedures;

	/**
	 * Classe que define a interface do provedor.
	 */
	private Class<?> intfClass;

	/**
	 * Classe que implementa a interface do provedor.
	 */
	private Class<?> implClass;

	/**
	 * Objeto genérico que precisa estar disponível à classe de implementação durante a execução de
	 * seus métodos.
	 */
	private Object data;

	private IServiceInterface implementation = null;

	private IPrototypeFilter prototypeFilter;

	/**
	 * Cria uma instância da classe mapeando a interface à sua implementação.
	 * 
	 * @param intfClass
	 *            Classe de interface.
	 * @param implClass
	 *            Classe de implementação, que deve implementar a interface.
	 * @throws IncompleteImplementationException
	 * @throws InvocationTargetException
	 * 
	 * @throws InvalidMethodException
	 * @throws InvalidRpcClassException
	 */
	public MethodRunner( Class<?> intfClass, Class<?> implClass, IPrototypeFilter filter, Object data )
		throws InvocationTargetException, IncompleteImplementationException,
		InvalidRpcClassException
	{
		if (filter == null)
			filter = new SimplePrototypeFilter();
		
		// verifica se as classes informadas são válidas para o uso através de RPC
		RpcValidator.validateInterface(intfClass);
		RpcValidator.validateImplementation(intfClass, implClass);

		procedures = new HashMap<String, Method>();

		this.intfClass = intfClass;
		this.implClass = implClass;
		this.prototypeFilter = filter;
		this.data = data;
		registerImplementation(intfClass, implClass);

		// verifica se a implementação do serviço é stateless
		ServiceImplementation annot = implClass.getAnnotation(ServiceImplementation.class);
		if (annot != null && annot.isStateless())
		{
			implementation = createImplementation();
			serverLog.debug(StringResource.get(MethodRunner.class, 0, implClass.getName()));
		}
	}

	/**
	 * Registra os métodos de um manipulador XML-RPC.
	 * 
	 * @param intfClass
	 *            Classe que define a interface do manipulador.
	 * @param implClass
	 *            Classe que implementa a interface do manipulador.
	 * @throws InvalidRpcClassException
	 * @throws XmlRpcException
	 */
	protected void registerImplementation( Class<?> intfClass, Class<?> implClass )
		throws InvalidRpcClassException
	{
		Method[] methods = intfClass.getMethods();
		String prototype;

		// itera pelos métodos da interface
		for (int i = 0; i < methods.length; i++)
		{
			Method intrMethod = methods[i];
			Method implMethod;

			try
			{
				// procura a implementação do método
				implMethod = implClass.getMethod(intrMethod.getName(),
					intrMethod.getParameterTypes());
			} catch (Exception e)
			{
				continue;
			}

			// Nota: sempre registramos o método através de seu nome de interface, pois um cliente
			// usando um "IServiceConsumer" só tem acesso às interfaces.
			prototype = prototypeFilter.getMethodPrototype(intrMethod);

			// serverLog.debug( StringResource.get(MethodRunner.class, 1, name) );
			System.out.println(prototype);

			// verifica se já existe algum método com mesmo nome
			if (procedures.containsKey(prototype))
				throw new InvalidRpcClassException("Equivalent method already registred: " + prototype);
			procedures.put(prototype, implMethod);
		}
	}

	@Override
	public Class<?> getImplementationClass()
	{
		return implClass;
	}

	@Override
	public Class<?> getInterfaceClass()
	{
		return intfClass;
	}

	@Override
	public Object getData()
	{
		return data;
	}

	@Override
	public Method getMethod( String prototype )
	{
		if (prototype == null) return null;

		// NOT NECESSARY CAUSE WE HAVE PROTOTYPE FILTERS
		//if (prototype.indexOf('.') < 0) prototype = getInterfaceClass().getName() + "." + prototype;

		return procedures.get(prototype);
	}

	@Deprecated
	@Override
	public Object callMethod( Method method, Object[] values )
		throws IncompleteImplementationException, InvocationTargetException,
		MethodNotFoundException, IncompleteInterfaceException, InternalException
	{
		String name = getInterfaceClass().getName() + "." + method.getName();
		Method methodRef = getMethod(name);
		if (methodRef == null)
			throw new MethodNotFoundException("Method \"" + name + "\" not found.");

		// verifica se o objeto de implementação existe (consequentemente pode ser reusado)
		IServiceInterface impl = getImplementation();

		// procura o construtor padrão
		try
		{
			return methodRef.invoke(impl, values);
		} catch (SecurityException e)
		{
			throw new MethodNotFoundException("Method '" + method.getName() + "' not found",
				e.getCause());
		} catch (java.lang.reflect.InvocationTargetException e)
		{
			throw new InvocationTargetException("The method '" + method.getName()
				+ "' throw an exception", e.getCause());
		} catch (Exception e)
		{
			throw new InternalException("Internal error while invoking method'" + method.getName(),
				e.getCause());
		}
	}

	@Override
	public Object callMethod( String methodPrototype, Object[] values ) throws InternalException,
		IncompleteImplementationException, InvocationTargetException, MethodNotFoundException,
		IncompleteInterfaceException
	{
		Method methodRef;

		// NOT NECESSARY CAUSE WE HAVE PROTOTYPE FILTERS
		//if (methodPrototype.indexOf('.') < 0)
		//	methodPrototype = getInterfaceClass().getName() + "." + methodPrototype;
		
		methodRef = procedures.get(methodPrototype);
		if (methodRef == null)
			throw new MethodNotFoundException("Method \"" + methodPrototype + "\" not found.");

		// verifica se o objeto de implementação existe (consequentemente pode ser reusado)
		IServiceInterface impl = getImplementation();

		// procura o construtor padrão
		try
		{
			return methodRef.invoke(impl, values);
		} catch (SecurityException e)
		{
			throw new MethodNotFoundException("Method '" + methodRef.getName() + "' not found",
				e.getCause());
		} catch (java.lang.reflect.InvocationTargetException e)
		{
			throw new InvocationTargetException("The method '" + methodRef.getName()
				+ "' throw an exception", e.getTargetException());
		} catch (Exception e)
		{
			throw new InternalException("Internal error while invoking method'" + methodRef.getName(),
				e.getCause());
		}
	}

	@Override
	public Object callMethod( IProcedureCall request )
		throws IncompleteImplementationException, InvocationTargetException,
		MethodNotFoundException, IncompleteInterfaceException, InternalException
	{
		// obtém o método de implementação
		Method methodRef = getMethod(request.getMethodPrototype());
		if (methodRef == null)
			throw new MethodNotFoundException("Method \"" + request.getMethodPrototype()
				+ "\" not found.");
		try
		{
			methodRef = intfClass.getMethod( methodRef.getName(), methodRef.getParameterTypes() );
		} catch (Exception e)
		{
			throw new MethodNotFoundException("Method \"" + request.getMethodPrototype()
				+ "\" not found.", e);
		}

		// avalia se os parâmetros requeridos pelo método foram informados e ordena-os num vetor
		// para efetuar a chamada
		Annotation[][] params = methodRef.getParameterAnnotations();
		Object[] args = new Object[params.length];
		Class<?>[] types = methodRef.getParameterTypes();
		int c = 0;
		Object value;

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
				throw new IncompleteInterfaceException("Some parameters has been not annotated.");

			// insere o valor do parâmetro no vetor de argumentos para o método Java
			String name = annotation.name();
			value = request.getParameter(name);

			if (value != null)
				args[c] = TypeConverter.convert(types[c], value.toString());
			else
			{
				if ( annotation.required() )
					throw new InvocationTargetException("Method invocation exception",
						new ParameterNotFoundException("Missing value for parameter \"" + name
							+ "\""));
				else
					args[c] = null;
			}
			c++;
		}

		return callMethod(request.getMethodPrototype(), args);
	}

	@Override
	public boolean containsMethod( String methodPrototype )
	{
		return procedures.containsKey(methodPrototype);
	}

	private IServiceInterface createImplementation() throws IncompleteImplementationException,
		InvocationTargetException
	{
		IServiceInterface impl;

		try
		{
			Constructor<?> ctor = getImplementationClass().getConstructor(ARGS_IMPL);
			impl = (IServiceInterface) ctor.newInstance(getData());
		} catch (NoSuchMethodException e)
		{
			throw new IncompleteImplementationException("Standard constructor not found.");
		} catch (Exception e)
		{
			serverLog.error("Constructor exception", e);
			throw new InvocationTargetException("Constructor invocation exception", e);
		}

		return impl;
	}

	private IServiceInterface getImplementation() throws IncompleteImplementationException,
		InvocationTargetException, InternalException
	{
		// verifica se já possui um objeto de implementação reusável
		if (implementation != null) return implementation;
		return createImplementation();
	}

	@Override
	public boolean containsMethod( Method method )
	{
		return containsMethod(prototypeFilter.getMethodPrototype(method));
	}

	@Override
	public IPrototypeFilter getPrototypeFilter()
	{
		return prototypeFilter;
	}

}
