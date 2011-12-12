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

package corinna.util;




public interface IObjectPool<V extends IReusable>
{

	/**
	 * Retorna o valor do elemento de cache que possui a chave especificada. O elemento que
	 * corresponde a chave é atualizado pela estratégia de cache utilizada.
	 * 
	 * @param key
	 *            Chave que identifica o elemento de cache.
	 * @return Valor do elemento de cache. Caso nenhum elemento de cache corresponda a chave
	 *         especificada, retorna <code>null</code>.
	 * @see ICacheStrategy#update(CacheElement)
	 */
	public abstract V getObject();

	/**
	 * Cria ou atualiza um elemento de cache com a chave e valor informados. O novo elemento é
	 * atualizado pela estratégia de cache adotada.
	 * 
	 * @param key
	 *            Chave que identifica o elemento de cache.
	 * @param value
	 *            Valor a ser armazenado.
	 * @see ICacheStrategy#update(CacheElement)
	 */
	public abstract boolean putObject( V value );

	/**
	 * Retorna um valor lógico indicando se o número máximo de elementos permitidos no pool foi
	 * alcançado.
	 * 
	 * @return <code>True</code> se o número máximo de elementos permitidos no pool foi alcançado.
	 *         Caso contrário, retorna <code>false</code>.
	 */
	public abstract boolean isFull();

}
