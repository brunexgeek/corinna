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

import corinna.exception.ConfigurationNotFoundException;
import corinna.thread.ObjectLocker;


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
	protected Map<String, String> items;
	
	/**
	 * Lista contendo as sub-seções da seção.
	 */
	protected Map<String, ISection> sections;

	/**
	 * Indica se o conteúdo da seção é somente para leitura (<code>true</code>) ou pode ser 
	 * modificado (<code>false</code>).
	 */
	protected Boolean isReadOnly;
	
	protected ObjectLocker lock;

	
	/**
	 * Cria uma seção de configuração especificando seu nome.
	 * 
	 * @param name Nome da seção.
	 */
	public Section( String name )
	{
		if (name == null || name.isEmpty()) 
			throw new IllegalArgumentException("The section name can not be null or empty");

		this.name = name;
		this.items = new LinkedHashMap<String, String>();
		this.sections = new LinkedHashMap<String,ISection>();
		this.lock = new ObjectLocker();
	}

	@Override
	public String getName() 
	{
		lock.readLock();
		String result = name;
		lock.readUnlock();
		
		return result;
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
		lock.readLock();
		boolean status = items.containsKey(key);
		lock.readUnlock();
		
		return status;
	}
	
	@Override
	public void setValue( String key, String value )
	{
		if (key == null || key.isEmpty()) return;

		if (key.indexOf('.') < 0)
		{
			lock.writeLock();
			if (value != null)
				items.put(key, value);
			else
				items.remove(key);
			lock.writeUnlock();
		}
		else
		{
			String names[] = key.split("\\.");
			setValue(names, value);
		}
	}
	
	protected void setValue( String names[], String value )
	{
		ISection target = this;
		ISection parent = this;
		
		if (names == null || names.length == 0) return;
		
		for (int i = 0; i < names.length-1; i++)
		{
			target = parent.getSection(names[i], null);
			if (target == null) target = parent.addSection(names[i]);
			parent = target;
		}
		
		parent.setValue(names[names.length-1], value);
	}
	
	@Override
	public String[] getKeys()
	{
		String[] keys;
		
		lock.readLock();
		try
		{
			keys = items.keySet().toArray(new String[0]);
		}
		finally
		{
			lock.readUnlock();
		}
		
		return keys;
	}

	@Override
	public boolean isReadOnly()
	{
		lock.readLock();
		boolean result = isReadOnly;
		lock.readUnlock();
		
		return result;
	}

	@Override
	public void setReadOnly(boolean state, boolean recursive) 
	{
		lock.writeLock();
		try
		{
			isReadOnly = state;
			// define a propriedade nas sub-seções
			if (recursive)
				for (ISection section : sections.values())
					section.setReadOnly(state,true);
		} finally
		{
			lock.writeUnlock();
		}
	}

	@Override
	public void clear( boolean recursive ) 
	{
		lock.writeLock();
		try
		{
			// remove as entradas da seção atual
			items.clear();
			// remove as entradas das sub-seções recursivamente
			if (recursive)
				for (ISection section : sections.values())
					section.clear(true);
		} finally
		{
			lock.writeUnlock();
		}
	}

	@Override
	public void clearAll() 
	{
		lock.writeLock();
		try
		{
			// remove as entradas da seção atual
			items.clear();
			// remove as entradas das sub-seções recursivamente
			for (ISection section : sections.values()) section.clearAll();
			// remove as sub-seções
			sections.clear();
		} finally
		{
			lock.writeUnlock();
		}
	}

	@Override
	public boolean containsSection( String section ) 
	{
		lock.readLock();
		boolean result = sections.containsKey(section);
		lock.readUnlock();
		
		return result;
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
	public ISection getSection( String name, ISection defaultSection ) 
	{
		ISection result = this;
		
		if (name == null || name.isEmpty()) return defaultSection;
		
		if (name.indexOf('.') < 0)
		{
			lock.readLock();
			result = sections.get(name);
			lock.readUnlock();
		}
		else
		{
			String names[] = name.split("\\.");
			
			for (int i = 0; i < names.length && result != null; i++)
			{
				if (names[i].isEmpty()) continue;
				
				ISection current = result.getSection(names[i], null);
				result = (Section) current;
			}
		}
		
		if (result == null) return defaultSection;
		return result;
	}
	
	@Override
	public ISection addSection( String section ) 
	{
		lock.writeLock();
		try
		{
			ISection result = sections.get(section);
			if (result == null)
				sections.put( section, result = new Section(section) );
			return result;
		} finally
		{
			lock.writeUnlock();			
		}
	}

	@Override
	public void removeSection( ISection section ) 
	{
		if (section == null) return;
		
		lock.writeLock();
		sections.remove( section.getName() );
		lock.writeUnlock();	
	}

	@Override
	public void removeSection( String section ) 
	{
		if (section == null) return;
		
		lock.writeLock();
		sections.remove(section);
		lock.writeUnlock();	
	}

	@Override
	public ISection[] getSections() 
	{
		ISection[] result = null;
		
		lock.readLock();
		try
		{
			result = sections.values().toArray(new ISection[0]);
		}
		finally
		{
			lock.readUnlock();
		}
		
		return result;
	}

	@Override
	public String getValue( String key, String defaultValue ) 
	{
		if (key == null) return null;
		
		lock.readLock();
		String result = items.get(key);
		lock.readUnlock();

		if (result == null) return defaultValue;
		return result;
	}
	
}
