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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class BeanCollection implements IBeanCollection
{

	protected LinkedList<Object> entries;
	
	public BeanCollection( Collection<Object> source )
	{
		entries = new LinkedList<Object>();
		
		if (source == null) return;
		extract(source);		
	}

	public void extract( Collection<Object> source  )
	{
		for (Object value : source)
		{
			value = BeanObject.extractValue(value);
			entries.add(value);
		}
	}
	
	@Override
	public int size()
	{
		return entries.size();
	}

	@Override
	public boolean isEmpty()
	{
		return entries.isEmpty();
	}

	@Override
	public boolean contains( Object o )
	{
		return entries.contains(o);
	}

	@Override
	public Iterator<Object> iterator()
	{
		return entries.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return entries.toArray();
	}

	@Override
	public <T> T[] toArray( T[] a )
	{
		return entries.toArray(a);
	}

	@Override
	public boolean add( Object e )
	{
		return entries.add(e);
	}

	@Override
	public boolean remove( Object o )
	{
		return entries.remove(o);
	}

	@Override
	public boolean containsAll( Collection<?> c )
	{
		return entries.containsAll(c);
	}

	@Override
	public boolean addAll( Collection<? extends Object> c )
	{
		return entries.addAll(c);
	}

	@Override
	public boolean removeAll( Collection<?> c )
	{
		return entries.removeAll(c);
	}

	@Override
	public boolean retainAll( Collection<?> c )
	{
		return entries.retainAll(c);
	}

	@Override
	public void clear()
	{
		entries.clear();
	}
		
}
