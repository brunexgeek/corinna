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

import java.lang.reflect.Method;

import corinna.exception.IncompleteImplementationException;
import corinna.exception.IncompleteInterfaceException;
import corinna.exception.InternalException;
import corinna.exception.MethodCallException;



/**
 * Interface para classes que oferecem a capacidade de invocar procedimentos remotos.
 * 
 * @author bruno
 */
public interface IMethodCaller 
{

	/**
	 * Invoca um procedimento remoto a partir de uma instância <c>Method</c>. O procedimento a ser 
	 * executado precisa constar na interface de serviço utilizada pelo nó remoto.
	 *  
	 * @param method Instância de <c>Method</c> relativa ao método a ser chamado. A instância do 
	 *     método pode ser obtida a partir da interface, através de reflexão.
	 * @param values Valor dos parâmetros a serem passados na chamada.
	 * @return Valor de retornado pelo procedimento remoto.
	 * 
	 * @throws IncompleteImplementationException se a implementação do serviço possuir erros.
	 * @throws MethodCallException se ocorreu algum erro numa chamada de método.
	 * @throws IncompleteInterfaceException se a interface do serviço possuir erros.
	 * @throws InternalException se ocorreu algum erro interno.
	 */
	Object callMethod( Method method, Object values[] ) throws MethodCallException, 
		IncompleteImplementationException, IncompleteInterfaceException, InternalException;
	
	
	/**
	 * Invoca um procedimento remoto. O procedimento a ser executado precisa constar na interface
	 * de serviço utilizada pelo nó remoto.
	 *  
	 * @param methodName Nome do procedimento a ser chamado.
	 * @param values Valor dos parâmetros a serem passados na chamada do procedimento.
	 * @return Valor de retornado pelo procedimento remoto.
	 * 
	 * @throws IncompleteImplementationException se a implementação do serviço possuir erros.
	 * @throws MethodCallException se ocorreu algum erro numa chamada de método.
	 * @throws IncompleteInterfaceException se a interface do serviço possuir erros.
	 * @throws InternalException se ocorreu algum erro interno.
	 */
	Object callMethod( String prototype, Object values[] ) throws MethodCallException, 
		IncompleteImplementationException, IncompleteInterfaceException, InternalException;

	/**
	 * Invoca um procedimento remoto a partir de uma requisição <code>ProcedureCallRequest</code>.
	 * O procedimento a ser executado precisa constar na interface de serviço utilizada pelo nó 
	 * remoto.
	 *  
	 * @param request Requisição de chamada de procedimento.
	 * @return Valor de retornado pelo procedimento remoto.
	 * 
	 * @throws IncompleteImplementationException se a implementação do serviço possuir erros.
	 * @throws MethodCallException se ocorreu algum erro numa chamada de método.
	 * @throws IncompleteInterfaceException se a interface do serviço possuir erros.
	 * @throws InternalException se ocorreu algum erro interno.
	 */
	Object callMethod( IProcedureCall request ) throws MethodCallException, 
		IncompleteImplementationException, IncompleteInterfaceException, InternalException;
	
}
