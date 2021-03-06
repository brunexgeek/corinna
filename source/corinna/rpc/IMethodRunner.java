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


/**
 * Interface para classes que permitem executar métodos de uma implementação através do nome 
 * totalmente qualificado. O nome do método deve ser fornecido em função de uma interface e mapeado
 * para sua implementação correspondente.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public interface IMethodRunner extends IMethodMapper, IMethodCaller
{

	
	/**
	 * Retorna a classe que implementa a interface mapeada.
	 * 
	 * @return
	 */
	public Class<?> getImplementationClass();


	/**
	 * Retorna o objeto genérico que precisa estar disponível à classe de implementação durante a 
	 * execução de seus métodos.
	 */
	public Object getData();

	
}