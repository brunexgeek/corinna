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
	 * <code>String</code> que corresponde ao valor lógico <code>true</code>.
	 */
	private static final String LOGIC_TRUE = Boolean.TRUE.toString();
	
	/**
	 * <code>String</code> que corresponde ao valor lógico <code>true</code>.
	 */
	private static final String LOGIC_FALSE = Boolean.FALSE.toString();
	
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

	/*@Override
	public String getValue( String key ) throws ConfigurationNotFoundException
	{
		String value = getValue(key, null);
		if (value == null)
			throw new ConfigurationNotFoundException("Key '" + key + "' not found");
		return value;
	}*/
		
	@Override
	public boolean containsKey( String key )
	{
		lock.readLock();
		boolean status = items.containsKey(key);
		lock.readUnlock();
		
		return status;
	}
	
	@Override
	public void setValue( String key, Object value )
	{
		if (key == null || key.isEmpty()) return;

		if (key.indexOf('.') < 0)
		{
			lock.writeLock();
			if (value != null)
				items.put(key, value.toString());
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
	
	protected void setValue( String names[], Object value )
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

	/**
	 * Retorna o valor de uma entrada de configuração como sendo uma <code>String</code>.
	 * 
	 * @param key Chave da entrada.
	 * @param defaultValue Valor padrão a ser retornado caso não exista um entrada com a chave 
	 *     informada. 
	 * @return <code>String</code> contendo o valor da entrada de configuração ou o valor padrão, 
	 *     caso não exista uma entrada com a chave informada.
	 */
	@Override
	public String getString( String key, String defaultValue ) 
	{
		if (key == null || key.isEmpty()) return defaultValue;
		
		String result = defaultValue;
		
		if (key.indexOf('.') < 0)
		{
			lock.readLock();
			result = items.get(key);
			lock.readUnlock();
			if (result == null) return defaultValue;
		}
		else
		{
			String names[] = key.split("\\.");
			result = getString(names, defaultValue);
		}
		return result;
	}

	@Override
	public String getString( String names[], String defaultValue )
	{
		ISection target = this;
		ISection parent = this;
		
		if (names == null || names.length == 0) return defaultValue;
		
		for (int i = 0; i < names.length-1; i++)
		{
			target = parent.getSection(names[i], null);
			if (target == null) return defaultValue;
			parent = target;
		}
		
		return parent.getString(names[names.length-1], defaultValue);
	}

	/**
	 * Retorna o valor de uma entrada de configuração como sendo uma <code>String</code>.
	 * 
	 * @param key Chave da entrada.
	 * @return <code>String</code> contendo o valor da entrada de configuração. Caso não exista uma 
	 *     entrada com a chave informada, retorna <code>null</code>.
	 * @throws ConfigurationNotFoundException se nenhuma chave com o nome indicado for encontrada.
	 */
	@Override
	public String getString( String key ) throws ConfigurationNotFoundException
	{
		String value = getString(key, null);
		if (value == null) 
			throw new ConfigurationNotFoundException("Key '" + key + "' not found");
		return value;
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo um <code>Integer</code>.
	 * 
	 * @param key Chave da entrada.
	 * @return Instância <code>Integer</code> contendo o valor da entrada de configuração.
	 * @throws ConfigurationNotFoundException se nenhuma chave com o nome indicado for encontrada ou
	 *     se o valor não pode ser convertido para <code>Integer</code>.
	 */
	@Override
	public Integer getInteger( String key ) throws ConfigurationNotFoundException
	{
		Integer value = getInteger(key, null);
		if (value == null)
			throw new ConfigurationNotFoundException("Key '" + key + "' not found");
		return value;
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo um <code>Integer</code>.
	 * 
	 * @param key Chave da entrada.
	 * @param defaultValue Valor padrão a ser retornado caso não exista uma entrada com a chave 
	 *     informada ou caso seu valor não seja um <code>Integer</code>. 
	 * @return <code>Integer</code> contendo o valor da entrada de configuração. Caso não exista uma 
	 *     entrada com a chave informada ou se o valor não pode ser convertido para 
	 *     <code>Integer</code>, retorna o valor padrão.
	 */
	@Override
	public Integer getInteger( String key, Integer defaultValue )
	{
		String result = getString(key, null);
		if (result == null) return defaultValue;
		try 
		{
			return Integer.valueOf(result);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo um <code>Integer</code>. 
	 * Adicionalmente o valor obtido é ajustado para para estar entre o intervalor definido pelo
	 * valor mínimo e máximo informados. Esse intervalo também é aplicado sobre o valor padrão.
	 * 
	 * @param key Chave da entrada.
	 * @param defaultValue Valor padrão a ser retornado caso não exista uma entrada com a chave 
	 *     informada ou caso seu valor não seja um <code>Integer</code>. 
	 * @param minValue Valor mínimo a ser retornado. Este parâmetro é opcional e quando definido 
	 *     como <code>null</code> faz com que nenhum ajuste de limite inferior seja aplicado.
	 * @param minValue Valor máximo a ser retornado. Este parâmetro é opcional e quando definido 
	 *     como <code>null</code> faz com que nenhum ajuste de limite superior seja aplicado.
	 * @return <code>Integer</code> contendo o valor da entrada de configuração. Caso não exista uma 
	 *     entrada com a chave informada ou se o valor não pode ser convertido para 
	 *     <code>Integer</code>, retorna o valor padrão.
	 */
	@Override
	public Integer getInteger( String key, Integer defaultValue, Integer minValue,
		Integer maxValue )
	{
		// garante que 'minValue' é menor que 'maxValue'
		if (minValue > maxValue)
		{
			int value = minValue;
			minValue = maxValue;
			maxValue = value;
		}
		
		// obtém o valor da configuração
		Integer value = getInteger(key, defaultValue);

		// garante que o valor obtido está no intervalo
		return normalize(minValue, maxValue, value);
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo um <code>Long</code>.
	 * 
	 * @param key Chave da entrada.
	 * @return Instância <code>Long</code> contendo o valor da entrada de configuração.
	 * @throws ConfigurationNotFoundException se nenhuma chave com o nome indicado for encontrada ou
	 *     se o valor não pode ser convertido para <code>Long</code>.
	 */
	@Override
	public Long getLong( String key ) throws ConfigurationNotFoundException
	{
		Long value = getLong(key, null);
		if (value == null)
			throw new ConfigurationNotFoundException("Key '" + key + "' not found");
		return value;
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo uma <code>Long</code>.
	 * 
	 * @param key Chave da entrada.
	 * @param defaultValue Valor padrão a ser retornado caso não exista um entrada com a chave 
	 *     informada. 
	 * @return <code>Long</code> contendo o valor da entrada de configuração. Caso não exista uma 
	 *     entrada com a chave informada ou se o valor não pode ser convertido para 
	 *     <code>Long</code>, retorna o valor padrão.
	 */
	@Override
	public Long getLong( String key, Long defaultValue )
	{
		String result = getString(key, null);
		if (result == null) return defaultValue;
		try 
		{
			return Long.valueOf(result);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retorna o valor de uma entrada de configuração como sendo um <code>Long</code>. 
	 * Adicionalmente o valor obtido é ajustado para para estar entre o intervalor definido pelo
	 * valor mínimo e máximo informados. Esse intervalo também é aplicado sobre o valor padrão.
	 * 
	 * @param key Chave da entrada.
	 * @param defaultValue Valor padrão a ser retornado caso não exista uma entrada com a chave 
	 *     informada ou caso seu valor não seja um <code>Long</code>. 
	 * @param minValue Valor mínimo a ser retornado. Este parâmetro é opcional e quando definido 
	 *     como <code>null</code> faz com que nenhum ajuste de limite inferior seja aplicado.
	 * @param minValue Valor máximo a ser retornado. Este parâmetro é opcional e quando definido 
	 *     como <code>null</code> faz com que nenhum ajuste de limite superior seja aplicado.
	 * @return <code>Long</code> contendo o valor da entrada de configuração. Caso não exista uma 
	 *     entrada com a chave informada ou se o valor não pode ser convertido para 
	 *     <code>Long</code>, retorna o valor padrão.
	 */
	@Override
	public Long getLong( String key, Long defaultValue, Long minValue, Long maxValue )
	{
		// garante que 'minValue' é menor que 'maxValue'
		if (minValue > maxValue)
		{
			long value = minValue;
			minValue = maxValue;
			maxValue = value;
		}
		
		// obtém o valor da configuração
		Long value = getLong(key, defaultValue);

		// garante que o valor obtido está no intervalo
		return normalize(minValue, maxValue, value);
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo um <code>Double</code>.
	 * 
	 * @param key Chave da entrada.
	 * @return Instância <code>Double</code> contendo o valor da entrada de configuração.
	 * @throws ConfigurationNotFoundException se nenhuma chave com o nome indicado for encontrada ou
	 *     se o valor não pode ser convertido para <code>Double</code>.
	 */
	@Override
	public Double getDouble( String key ) throws ConfigurationNotFoundException
	{
		Double value = getDouble(key, null);
		if (value == null)
			throw new ConfigurationNotFoundException("Key '" + key + "' not found");
		return value;
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo uma <code>Double</code>.
	 * 
	 * @param key Chave da entrada.
	 * @param defaultValue Valor padrão a ser retornado caso não exista um entrada com a chave 
	 *     informada. 
	 * @return <code>Double</code> contendo o valor da entrada de configuração. Caso não exista uma 
	 *     entrada com a chave informada ou se o valor não pode ser convertido para 
	 *     <code>Double</code>, retorna o valor padrão.
	 */
	@Override
	public Double getDouble( String key, Double defaultValue )
	{
		String result = getString(key, null);
		if (result == null) return defaultValue;
		try 
		{
			return Double.valueOf(result);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo um <code>Double</code>.
	 * 
	 * @param key Chave da entrada.
	 * @return Instância <code>Float</code> contendo o valor da entrada de configuração.
	 * @throws ConfigurationNotFoundException se nenhuma chave com o nome indicado for encontrada ou
	 *     se o valor não pode ser convertido para <code>Float</code>.
	 */
	@Override
	public Float getFloat( String key ) throws ConfigurationNotFoundException
	{
		Float value = getFloat(key, null);
		if (value == null)
			throw new ConfigurationNotFoundException("Key '" + key + "' not found");
		return value;
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo uma <code>Float</code>.
	 * 
	 * @param key Chave da entrada.
	 * @param defaultValue Valor padrão a ser retornado caso não exista um entrada com a chave 
	 *     informada. 
	 * @return <code>Float</code> contendo o valor da entrada de configuração. Caso não exista uma 
	 *     entrada com a chave informada ou se o valor não pode ser convertido para 
	 *     <code>Float</code>, retorna o valor padrão.
	 */
	@Override
	public Float getFloat( String key, Float defaultValue )
	{
		String result = getString(key, null);
		if (result == null) return defaultValue;
		try 
		{
			return Float.valueOf(result);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo um <code>Boolean</code>.
	 * 
	 * @param key Chave da entrada.
	 * @return Instância <code>Boolean</code> contendo o valor da entrada de configuração.
	 * @throws ConfigurationNotFoundException se não existir uma entrada com a chave informada ou 
	 *     se o valor não pode ser convertido para <code>Boolean</code>.
	 */
	@Override
	public Boolean getBoolean( String key ) throws ConfigurationNotFoundException
	{
		Boolean value = getBoolean(key, null);
		if (value == null)
			throw new ConfigurationNotFoundException("Key '" + key + "' not found");
		return value;
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo uma <code>Boolean</code>. Se o 
	 * valor da entrada é um número inteiro, o método retorna <code>true</code> se o valor é
	 * diferente de zero. Caso contrário, o valor é avaliado como uma <code>String</code> e o
	 * método retorna <code>true</code> se o valor é igual a palavra "true" (ignorando a caixa).
	 * 
	 * @param key Chave da entrada.
	 * @param defaultValue Valor padrão a ser retornado caso não exista um entrada com a chave 
	 *     informada. 
	 * @return <code>Boolean</code> contendo o valor da entrada de configuração. Caso não exista uma 
	 *     entrada com a chave informada ou se o valor não pode ser convertido para 
	 *     <code>Boolean</code>, retorna o valor padrão.
	 */
	@Override
	public Boolean getBoolean( String key, Boolean defaultValue )
	{
		String result = getString( key, null);
		if (result == null) return defaultValue;
		
		try
		{
			int value = Integer.parseInt(result);
			return (value != 0);
		} catch (NumberFormatException e)
		{
			return Boolean.valueOf(result);
		}
	}
	
	/**
	 * Retorna um valor da configuração inferindo o seu tipo de dado. Se nenhum tipo de
	 * dado puder ser inferido, o método retorna o valor como uma <code>String</code>.
	 * 
	 * @param key Nome da chave.
	 * @param defaultValue Valor padrão que será retornado caso a chave não exista.
	 * @return Objeto de tipo arbitrário contendo o valor da configuração. 
	 * @throws ConfigurationNotFoundException se nenhuma chave com o nome especificado for 
	 *     encontrada.
	 */
	@Override
	public Object getValue( String key ) throws ConfigurationNotFoundException
	{
		Object value = getValue(key, null);
		if (value == null)
			throw new ConfigurationNotFoundException("Key '" + key + "' not found");
		return value;
	}
		
	/**
	 * Retorna um valor da configuração inferindo o seu tipo de dado. Se nenhum tipo de
	 * dado puder ser inferido, o método retorna o valor como uma <code>String</code>.
	 * 
	 * @param key Nome da chave.
	 * @param defaultValue Valor padrão que será retornado caso a chave não exista.
	 * @return Objeto de tipo arbitrário contendo o valor da configuração. Caso nenhuma chave seja
	 *     encontrada, o método retorna o valor padrão.
	 */
	@Override
	public Object getValue( String key, Object defaultValue )
	{
		// tenta recuperar o valor como um inteiro longo
		Object value = getLong(key, null);
		if (value != null) return value;
		
		// tenta recuperar o valor como um ponto-flutuante longo
		value = getDouble(key, null);
		if (value != null) return value;
		
		// tenta recuperar o valor como um valor lógico
		value = getString(key, null);
		if (value == null)
			return defaultValue;
		else
		if ( value != null &&
		     ((String)value).equalsIgnoreCase(LOGIC_TRUE) ||
		     ((String)value).equalsIgnoreCase(LOGIC_FALSE) )
			return Boolean.valueOf((String)value);
		else
			return value;
	}
	
	@Override
	public void setString( String key, String value )
	{
		setValue(key, value);
	}
	
	/*
	public void setInteger( String key, Integer value )
	{
		setValue(key, value);
	}
	
	
	public void setLong( String key, Long value )
	{
		setValue(key, value);
	}
	
	
	public void setDouble( String key, Double value )
	{
		setValue(key, value);
	}
	
	public void setFloat( String key, Float value )
	{
		setValue(key, value);
	}
	
	public void setBoolean( String key, Boolean value )
	{
		setValue(key, value);
	}*/
		
	/**
	 * Ajusta um valor numérico <code>Long</code> de forma que o memso permaneca entre os limites
	 * inferior e superior informados.
	 * 
	 * @param minValue Limíte inferior. Este parâmetro é opcional e quando definido como 
	 *     <code>null</code> faz com que nenhum ajuste de limite inferior seja aplicado.
	 * @param maxValue Limite superior. Este parâmetro é opcional e quando definido como 
	 *     <code>null</code> faz com que nenhum ajuste de limite superior seja aplicado.
	 * @param value Valor numérico a ser ajustado.
	 * @return Instância <code>Long</code> contendo o valor numérico ajustado ou <code>null</code>
	 *     se o valor informado é nulo.
	 */
	public static Long normalize( Long minValue, Long maxValue, Long value )
	{
		if (value == null) return null;
		
		// garante que o valor obtido está no intervalo
		if (minValue != null && value < minValue)
			value = minValue;
		else
		if (maxValue != null && value > maxValue)
			value = maxValue;
		return value;
	}
	
	/**
	 * Ajusta um valor numérico <code>Integer</code> de forma que o memso permaneca entre os limites
	 * inferior e superior informados.
	 * 
	 * @param minValue Limíte inferior. Este parâmetro é opcional e quando definido como 
	 *     <code>null</code> faz com que nenhum ajuste de limite inferior seja aplicado.
	 * @param maxValue Limite superior. Este parâmetro é opcional e quando definido como 
	 *     <code>null</code> faz com que nenhum ajuste de limite superior seja aplicado.
	 * @param value Valor numérico a ser ajustado.
	 * @return Instância <code>Integer</code> contendo o valor numérico ajustado ou <code>null</code>
	 *     se o valor informado é nulo.
	 */
	public static Integer normalize( Integer minValue, Integer maxValue, Integer value )
	{
		if (value == null) return null;
		
		// garante que o valor obtido está no intervalo
		if (minValue != null && value < minValue)
			value = minValue;
		else
		if (maxValue != null && value > maxValue)
			value = maxValue;
		return value;
	}
	
}
