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

package corinna.util.conf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import corinna.exception.ConfigurationNotFoundException;


/**
 * Representa uma seção de configuração. As seções são utilizadas para armazenar as entradas de 
 * configuração e podem conter um número arbitrário de entradas. 
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public class Section implements ISection
{
	
	/**
	 * Nome da seção.
	 */
	private String name;
	
	/**
	 * Lista contendo as entradas da seção.
	 */
	private Map<String, String> items;
	
	/**
	 * Lista contendo as sub-seções da seção.
	 */
	protected Map<String, ISection> sections;

	/**
	 * Indica se o conteúdo da seção é somente para leitura (<code>true</code>) ou pode ser 
	 * modificado (<code>false</code>).
	 */
	private Boolean isReadOnly;
	
	private ReadLock readLock;
	
	private WriteLock writeLock;

	
	/**
	 * Cria uma seção de configuração especificando seu nome.
	 * 
	 * @param name Nome da seção.
	 */
	public Section( String name )
	{
		if (name == null) 
			this.name = "(null)";
		else
			this.name = name;

		items = new LinkedHashMap<String, String>();
		sections = new LinkedHashMap<String,ISection>();
		
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}

	@Override
	public String getName() 
	{
		synchronized (name)
		{
			return name;
		}
	}

	@Override
	public String getValue( String key ) throws ConfigurationNotFoundException
	{
		String value = getValue(key, null);
		if (value == null)
			throw new ConfigurationNotFoundException("Key '" + key + "' not found");
		return value;
	}
		
	@Override
	public boolean containsKey( String key )
	{
		readLock.lock();
		boolean status = items.containsKey(key);
		readLock.unlock();
		
		return status;
	}
	
	@Override
	public void setValue( String key, String value )
	{
		writeLock.lock();
		items.put(key, value);
		writeLock.unlock();
	}

	@Override
	public String[] getKeys()
	{
		String[] keys;
		
		readLock.lock();
		try
		{
			keys = items.keySet().toArray(new String[0]);
		}
		finally
		{
			readLock.unlock();
		}
		
		return keys;
	}

	@Override
	public boolean isReadOnly()
	{
		synchronized (isReadOnly)
		{
			return isReadOnly;
		}
	}

	@Override
	public void setReadOnly(boolean readOnly, boolean recursive) 
	{
		synchronized (isReadOnly)
		{
			isReadOnly = readOnly;
		}
		// define a propriedade nas sub-seções
		if (recursive)
			for (ISection section : sections.values()) section.setReadOnly(readOnly,true);
	}

	@Override
	public void clear( boolean recursive ) 
	{
		writeLock.lock();
		
		// remove as entradas da seção atual
		items.clear();
		// remove as entradas das sub-seções recursivamente
		if (recursive)
			for (ISection section : sections.values()) section.clear(true);
		
		writeLock.unlock();
	}

	@Override
	public void clearAll() 
	{
		writeLock.lock();
		
		// remove as entradas da seção atual
		items.clear();
		// remove as entradas das sub-seções recursivamente
		for (ISection section : sections.values()) section.clearAll();
		// remove as sub-seções
		sections.clear();
		
		writeLock.unlock();
	}

	@Override
	public boolean containsSection( String section ) 
	{
		readLock.lock();
		boolean output = sections.containsKey(section);
		readLock.unlock();
		
		return output;
	}

	@Override
	public ISection getSection( String section ) throws ConfigurationNotFoundException 
	{
		ISection output = getSection(section, null);
		if (output == null) 
			throw new ConfigurationNotFoundException("Section '" + section + "' not found");
		return output;
	}

	@Override
	public ISection getSection( String section, ISection defaultSection ) 
	{
		readLock.lock();
		ISection output = sections.get(section);
		readLock.unlock();
		
		if (output == null) return defaultSection;
		return output;
	}

	@Override
	public ISection addSection( String section ) 
	{
		readLock.lock();
		ISection output = sections.get(section);
		readLock.unlock();
		
		if (output != null) return output;
		
		writeLock.lock();
		sections.put( section, output = new Section(section) );
		writeLock.unlock();
		
		return output;
	}

	@Override
	public void removeSection( ISection section ) 
	{
		writeLock.lock();
		sections.remove( section.getName() );
		writeLock.unlock();
	}

	@Override
	public void removeSection( String section ) 
	{
		writeLock.lock();
		sections.remove(section);
		writeLock.unlock();
	}

	@Override
	public ISection[] getSections() 
	{
		ISection[] output = null;
		
		writeLock.lock();
		try
		{
			output = sections.values().toArray(new ISection[0]);
		}
		finally
		{
			writeLock.unlock();
		}
		
		return output;
	}

	@Override
	public String getValue( String key, String defaultValue ) 
	{
		readLock.lock();
		String output = items.get(key);
		readLock.unlock();

		if (output == null) return defaultValue;
		return output;
	}

	
}
