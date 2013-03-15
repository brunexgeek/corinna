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


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Permite obter mensagens de texto em um arquivo de recurso a partir de um identificador único.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public final class StringResource 
{

	private static final Logger log = LoggerFactory.getLogger(StringResource.class);
	
	
	/**
	 * Código IETF do idioma padrão.
	 */
	private static String DEFAULT_LANG = "en-us";
	
	
	/**
	 * Instância do objeto <code>Properties</code> que contém o conteúdo do arquivo de recurso.
	 */
	private static Properties resource = new Properties();

	
	/**
	 * Código IETF do idioma atual.
	 */
	private static String language = "";
	
	
	/**
	 * Nome base do arquivo de recurso.
	 */
	private static String BASENAME = "StringResource.properties";

	
	protected StringResource( )
	{
	}
	
	
	/**
	 * Retorna um texto a partir da classe associada e de sua chave numérica.
	 * 
	 * @param clazz Referência para a classe cujo texto está associado.
	 * @param key Chave numérica do texto.
	 * @return Texto contido no arquivo de recurso.
	 */
	public static String get( Class<?> clazz, int key )
	{
		return get(clazz.getName() + "."  + String.valueOf(key));
	}
	
	
	/**
	 * Retorna um texto a partir da classe associada e de sua chave numérica.
	 * 
	 * @param clazz Referência para a classe cujo texto está associado.
	 * @param key Chave numérica do texto.
	 * @return Texto contido no arquivo de recurso.
	 */
	/*public static String get( Class<?> clazz, String key )
	{
		return get(clazz.getName() + "." + key);
	}*/
		

	/**
	 * Retorna um texto a partir de sua chave textual.
	 * 
	 * @param key Chave do texto.
	 * @return Texto contido no arquivo de recurso.
	 */
	public static String get( String key )
	{
		synchronized (resource)
		{
			try
			{
				// se o recurso não foi carregado...
				if (language.isEmpty()) setLanguage(DEFAULT_LANG);
				
				// tenta retornar o valor correspondente à chave
				return resource.get(key).toString();
			}
			catch (Exception error)
			{
				// suprime qualquer erro e retorna uma String vazia
				return "";
			}
		}
	}

	
	/**
	 * Retorna um texto a partir da classe associada e de sua chave numérica. Adicionalmente,
	 * o método permite formatar o texto baseado nos argumentos extras fornecidos.
	 * 
	 * @param clazz Referência para a classe cujo texto está associado.
	 * @param key Chave numérica do texto.
	 * @param args Argumentos extras para a formatação do texto.
	 * @return Texto contido no arquivo de recurso.
	 */
	public static String get( Class<?> clazz, int key, Object... args ) 
	{
		return get(clazz.getName() + "." + String.valueOf(key), args);
	}
	
	

	/*public static String get( Class<?> clazz, String key, Object... args ) 
	{
		return get(clazz.getName() + "." + key, args);
	}*/
	
	
	/**
	 * Retorna um texto a partir de sua chave textual. Adicionalmente, o método permite formatar 
	 * o texto baseado nos argumentos extras fornecidos.
	 * 
	 * @param key Chave do texto.
	 * @param args Argumentos extras para a formatação do texto.
	 * @return Texto contido no arquivo de recurso.
	 */
	public static String get( String key, Object... args ) 
	{
		String value = get(key);
		try
		{
			return String.format(value, args);
		}
		catch (Exception error)
		{
			// suprime qualquer erro e retorna uma String vazia
			return "";
		}
	}


	/**
	 * Retorna o código IETF do idioma corrente.
	 * 
	 * @return Código IETF do idioma.
	 */
	public static String getLanguage()
	{
		return language;
	}


	/**
	 * Define o idioma dos textos a serem obtidos. Caso não exista algum arquivo de recurso para
	 * o idioma indicado, será utilizado o idioma padrão.
	 * 
	 * @param language Código IETF do idioma.
	 */
	public static synchronized void setLanguage( String language )
	{
		synchronized (resource)
		{
			if (!StringResource.language.equals(language))
			{
				StringResource.language = language;
				
				// efetua o carretamento de acordo com as constantes
				resource = null;
				resource = new Properties();
				try 
				{
					// localiza todos os recursos existentes
					List<URL> list = ResourceLoader.findResources(BASENAME);
					
					for (URL current : list)
					{
						InputStream is = ResourceLoader.getResourceAsStream(current);
						resource.load(is);
						is.close();
					}
				} 
				catch (IOException e) 
				{
					log.error("Error setting language", e);
				}
			}
		}
	}
	
	
}
