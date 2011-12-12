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

import corinna.exception.ConfigurationNotFoundException;


/**
 * Permite recuperar valores de entradas em um objeto de configuração de acordo com o tipo de dados.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 * @version 1.2
 */
public class ConfigurationReader 
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
	 * Objeto de configuração associado.
	 */
	protected ISection configuration;
	
	
	public ConfigurationReader( ISection config ) throws NullPointerException
	{
		if (config == null) throw new NullPointerException("Invalid configuration object");
		configuration = config;
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo uma <code>String</code>.
	 * 
	 * @param key Chave da entrada.
	 * @return <code>String</code> contendo o valor da entrada de configuração. Caso não exista uma 
	 *     entrada com a chave informada, retorna <code>null</code>.
	 * @throws ConfigurationNotFoundException se nenhuma chave com o nome indicado for encontrada.
	 */
	public String getString( String key ) throws ConfigurationNotFoundException
	{
		return configuration.getValue(key);
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
	public String getString( String key, String defaultValue )
	{
		String result = configuration.getValue(key, null);
		if (result == null) return defaultValue;
		return result;
	}
	
	/**
	 * Retorna o valor de uma entrada de configuração como sendo um <code>Integer</code>.
	 * 
	 * @param key Chave da entrada.
	 * @return Instância <code>Integer</code> contendo o valor da entrada de configuração.
	 * @throws ConfigurationNotFoundException se nenhuma chave com o nome indicado for encontrada ou
	 *     se o valor não pode ser convertido para <code>Integer</code>.
	 */
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
	public Integer getInteger( String key, Integer defaultValue )
	{
		String result = configuration.getValue(key, null);
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
	public Long getLong( String key, Long defaultValue )
	{
		String result = configuration.getValue(key, null);
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
	public Double getDouble( String key, Double defaultValue )
	{
		String result = configuration.getValue(key, null);
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
	public Float getFloat( String key, Float defaultValue )
	{
		String result = configuration.getValue(key, null);
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
	public Boolean getBoolean( String key, Boolean defaultValue )
	{
		String result = configuration.getValue( key, null);
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
