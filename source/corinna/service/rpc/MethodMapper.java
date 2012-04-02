///*
// * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package corinna.service.rpc;
//
//
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//
//import corinna.exception.InvalidRpcClassException;
//import corinna.exception.InvalidRpcMethodException;
//
//
//
///**
// * Mapeia métodos de uma interface aos seus nomes totalmente qualificados. 
// *  
// * @author brunoc
// */
//public class MethodMapper implements IMethodMapper 
//{
//
//	Logger logger = Logger.getLogger(MethodMapper.class);
//	
//	/**
//	 * Classe que contém os métodos.
//	 */
//	private Class<?> intfClass;
//	
//	/**
//	 * Tabela de métodos da classe de interface.
//	 */
//	private Map<String, Method> mapping;
//
//	private IPrototypeFilter prototypeFilter;
//	
//	
//	/**
//	 * Cria um objeto especificando a classe que contém os métodos a serem mapeados.
//	 * 
//	 * @param intfClass Classe que contém os métodos a serem exportados.
//	 * @throws InvalidMethodException se for encontrado um método inválido para uso através de RPC.
//	 * @throws InvalidRpcClassException se a classe é inválida para uso através de RPC.
//	 */
//	public MethodMapper( Class<?> intfClass ) throws InvalidRpcMethodException,
//		InvalidRpcClassException
//	{
//		// verifica se as classes informadas são válidas para o uso através de RPC
//		RpcValidator.validateInterface(intfClass);
//
//		mapping = new HashMap<String, Method>();
//		
//		this.intfClass = intfClass;
//		this.prototypeFilter = new SimplePrototypeFilter();
//		registerInterface(intfClass);
//	}
//	
//	
//
//	/**
//	 * Registra os métodos públicos de uma interface.
//	 * 
//	 * @param intfClass Classe que define a interface.
//	 * @throws InvalidMethodException se for encontrado um método inválido para uso através de RPC.
//	 */
//	protected void registerInterface( Class<?> intfClass ) throws InvalidRpcMethodException 
//	{
//		Method[] methods = intfClass.getMethods();
//		String prototype;
//
//		// itera pelos métodos da interface
//		for (int i = 0; i < methods.length; i++)
//		{
//			Method intfMethod = methods[i];
//			
//			prototype = prototypeFilter.getMethodPrototype(methods[i]);
//			System.out.println("Added method " + prototype );
//			
//			// verifica se já existe algum método com mesmo nome
//			if (mapping.containsKey(prototype))
//				throw new InvalidRpcMethodException("Method '" + prototype + "' already registred.");
//			mapping.put(prototype, intfMethod);
//		}
//	}
//	
//	
//	@Override
//	public Class<?> getInterfaceClass() 
//	{
//		return intfClass;
//	}
//
//	@Override
//	public Method getMethod( String prototype ) 
//	{
//		if (prototype == null) return null;
//		
//		if (prototype.indexOf('.') < 0)
//			prototype = getInterfaceClass().getName() + "." + prototype;
//		
//		return mapping.get(prototype);
//	}
//		
//	@Override
//	public boolean containsMethod( String prototype )
//	{
//		return mapping.containsKey(prototype);
//	}
//	
//	@Override
//	public boolean containsMethod( Method method )
//	{
//		return mapping.containsKey( prototypeFilter.getMethodPrototype(method) );
//	}
//
//
//
//	@Override
//	public IPrototypeFilter getPrototypeFilter()
//	{
//		return prototypeFilter;
//	}
//	
//}
