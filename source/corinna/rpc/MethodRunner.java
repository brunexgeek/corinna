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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.bindlet.BindletModel.Model;
import javax.bindlet.rpc.IProcedureCall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.bean.BeanManager;
import corinna.core.IComponentInterface;
import corinna.exception.ComponentException;
import corinna.exception.IncompleteImplementationException;
import corinna.exception.IncompleteInterfaceException;
import corinna.exception.InternalException;
import corinna.exception.InvalidRpcClassException;
import corinna.exception.InvocationTargetException;
import corinna.exception.MethodNotFoundException;
import corinna.exception.ParameterNotFoundException;
import corinna.rpc.annotation.Parameter;
import corinna.util.StateModel;


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
 * @author Bruno Ribeiro
 * @since 1.0
 */
public class MethodRunner implements IMethodRunner
{

	private Logger serverLog = LoggerFactory.getLogger(MethodRunner.class);

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

	private IComponentInterface implementation = null;

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
			filter = new QualifiedPrototypeFilter();
		
		// verifica se as classes informadas são válidas para o uso através de RPC
		RpcValidator.validateInterface(intfClass);
		RpcValidator.validateImplementation(intfClass, implClass);

		procedures = new HashMap<String, Method>();

		this.intfClass = intfClass;
		this.implClass = implClass;
		this.prototypeFilter = filter;
		this.data = data;
		registerImplementation(intfClass, implClass);

		// verifica se a implementação do serviço é STATELESS
		StateModel model = (StateModel) ReflectionUtil.getAnnotation(implClass, StateModel.class);
		if (model != null && model.value() == Model.STATELESS)
		{
			implementation = createImplementation();
			serverLog.debug("Created stateless component of '" + implClass.getName() + "'");
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
		IComponentInterface impl = getImplementation();

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
		
		methodRef = procedures.get(methodPrototype);
		if (methodRef == null)
			throw new MethodNotFoundException("Method \"" + methodPrototype + "\" not found.");

		// verifica se o objeto de implementação existe (consequentemente pode ser reusado)
		IComponentInterface impl = getImplementation();

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

			if (value != null && !annotation.isPublic())
				args[c] = value;
			else
			if (value != null)
				args[c] = TypeConverter.convert(types[c], value);
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

	private IComponentInterface createImplementation() throws IncompleteImplementationException,
		InvocationTargetException
	{
		IComponentInterface impl;

		try
		{
			//Constructor<?> ctor = getImplementationClass().getConstructor(ARGS_IMPL);
			//impl = (IComponentInterface) ctor.newInstance(getData());
			impl = (IComponentInterface) getImplementationClass().newInstance();
			// inject all referenced service beans
			BeanManager.getInstance().inject(impl);
			// initialize the component
			impl.init(getData());
		} catch (ComponentException e)
		{
			throw new IncompleteImplementationException("Component initialization exception", e);
		} catch (Exception e)
		{
			serverLog.error("Component instantiation error", e);
			throw new InvocationTargetException("Component instantiation error", e);
		}

		return impl;
	}

	private IComponentInterface getImplementation() throws IncompleteImplementationException,
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
