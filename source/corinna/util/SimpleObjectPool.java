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


import java.util.Deque;
import java.util.LinkedList;

import corinna.thread.ObjectLocker;

public class SimpleObjectPool<V extends IReusable> implements IObjectPool<V>
{

	//private Logger log = Logger.getLogger(SimpleObjectPool.class);

	public static final int DEFAULT_LIMIT = 16;
	
	/**
	 * Lista contendo elementos não utilizados.
	 */
	protected Deque<V> pool;

	protected ObjectLocker poolLocker;

	/**
	 * Maximum number of elements that should stored by the pool.
	 */
	protected long maxLimit;
	
	/**
	 * Minimum number of elements that should stored by the pool when automatic release of 
	 * objects are active. This value is always defined as being approximately 10% of the maximum
	 * limit.
	 */
	//protected long minLimit;
	
	//protected long avgTime;
	
	//protected long lastTime;

	//private boolean autoRelease = false;

	public SimpleObjectPool( long limit, boolean autoRelease )
	{
		this.pool = new LinkedList<V>();
		this.poolLocker = new ObjectLocker();
		this.maxLimit = (limit < 0) ? DEFAULT_LIMIT : limit;
		//this.autoRelease  = autoRelease;
		//this.lastTime = System.currentTimeMillis();
	}
	
	public SimpleObjectPool( long limit )
	{
		this(limit, false);
	}

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
	@Override
	public V getObject( )
	{
		poolLocker.writeLock();
		try
		{
			return pool.poll();
		} finally
		{
			poolLocker.writeUnlock();
		}
	}

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
	@Override
	public boolean putObject( V value )
	{
		boolean result;
		
		poolLocker.writeLock();
		try
		{
			result = (maxLimit == 0) || (maxLimit > pool.size());
			// check if offered object is already recycled
			if (!value.isRecycled())
				throw new IllegalArgumentException("The volatile object must be recycled");
			if (result) pool.offerLast(value);
			return result;
		} finally
		{
			poolLocker.writeUnlock();
		}
	}

	/**
	 * Retorna um valor lógico indicando se o número máximo de elementos permitidos no pool foi
	 * alcançado.
	 * 
	 * @return <code>True</code> se o número máximo de elementos permitidos no pool foi alcançado.
	 *         Caso contrário, retorna <code>false</code>.
	 */
	@Override
	public boolean isFull()
	{
		boolean result;

		poolLocker.readLock();
		try
		{
			result = pool.size() > getPoolLimit();
		} finally
		{
			poolLocker.readUnlock();
		}

		return result;
	}

	public long getPoolLimit()
	{
		long result;
		
		poolLocker.readLock();
		result = maxLimit;
		poolLocker.readUnlock();

		return result;
	}

	/**
	 * Define o limite de elementos que o pool deve manter. Se o novo limite for menor que a
	 * quantidade atual de elementos em cache, os elementos excedentes não serão imediatamente
	 * removidos. Além disso, pool não irá reter novos elementos até que a quantidade de elementos
	 * torne-se menor que o novo limite.
	 * 
	 * @param limit
	 *            Número máximo de elementos que devem ser retidos no cache.
	 */
	public void setPoolLimit( long limit )
	{
		poolLocker.writeLock();
		maxLimit = (limit <= 0) ? DEFAULT_LIMIT : limit;
		//minLimit = (long)(limit * 0.1) + 1;
		poolLocker.writeUnlock();
	}
/*
	public void setAutoRelease( boolean state )
	{
		poolLocker.writeLock();
		this.autoRelease = state;
		poolLocker.writeUnlock();
	}

	public boolean isAutoRelease()
	{
		boolean result;
		poolLocker.readLock();
		result = autoRelease;
		poolLocker.readUnlock();
		
		return result;
	}*/

}
