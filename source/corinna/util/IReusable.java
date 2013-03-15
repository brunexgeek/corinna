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
package corinna.util;

/**
 * Define a interface para objetos que podem ser reusáveis.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 2.0
 * @version 2.0
 */
public interface IReusable
{

	/**
	 * Retorna um valor lógico indicando se o objeto pode ser reutilizado em outras tarefas
	 * além da tarefa para qual foi criado.
	 * 
	 * @return <code>True</code> se o objeto pode ser reusado ou <code>false</code> caso contrário.
	 */
	public boolean isReusable();
	
	public boolean isRecycled();
	
	/**
	 * Recicla o objeto atual removendo as referências internas atuais e liberando todos recursos 
	 * alocados. Após a execução do método, o objeto poderá ser reutilizado em uma nova tarefa.
	 * 
	 * @throws OperationNotSupportedException se o objeto não é reusável.
	 * @throws Exception se ocorrer algum erro ao reciclar o objeto.
	 */
	public void recycle() throws UnsupportedOperationException, Exception;
	
	
}
