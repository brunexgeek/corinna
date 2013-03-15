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
 * Allows mapping method prototypes to classes that defines theirs interface.
 * 
 * @author brunoc
 */
public interface IMethodMapper
{

	
	/**
	 * Retorna a interface de serviço mapeada.
	 * 
	 * @return Instância {@link Class<?>} da classe que define a interface.
	 */
	public Class<?> getInterfaceClass();
	
	
	/**
	 * Retorna a instância de um método através de seu protótipo.
	 * 
	 * @param methodName Method prototype.
	 * @return Instância do método.
	 */
	public Method getMethod( String prototype );
	
	
	/**
	 * Retorna um valor lógico indicando se a interface de serviço contém o método informado. 
	 * 
	 * @param methodName Nome totalmente qualificado do método.
	 * @return <code>True</code> se a interface contém o método especificado ou <code>false</code>
	 *     caso contrário.
	 */
	public boolean containsMethod( String prototype );

	public boolean containsMethod( Method method );
	
	public IPrototypeFilter getPrototypeFilter();
}
