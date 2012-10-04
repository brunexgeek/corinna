/*
 * Copyright 2011-2012 Bruno Ribeiro
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;


/**
 * Classe utilitária responsável por localizar e carregar recursos.
 * 
 * @author Bruno Ribeiro
 * @since 1.0
 */
public class ResourceLoader
{

	//private static Logger serverLog = Logger.getLogger( ResourceLoader.class.getName() );
	
	/**
	 * Retorna um <code>InputStream</code> associado a um recurso. O método tenta localizar o 
	 * recurso através do <code>ClassLoader</code> especificado. Caso o recurso não seja encontrado, 
	 * é feita uma tentativa em carregar o recurso a partir do disco.
	 * 
	 * @param loader Instância do <i>class loader</i> a ser utilizado na busca pelo recurso.
	 * @param name Nome do recurso.
	 * @return Instância <code>InputStream</code> associada ao um recurso.
	 * @throw FileNotFoundException se o recurso não foi encontrado.
	 * @throws IOException se ocorrer algum erro de leitura/conexão.
	 */
	public static InputStream getResourceAsStream( ClassLoader loader, String resourceName ) 
		throws IOException
	{
		/*String resourceName = name;
		
		// tenta obter o recurso pelo 'class loader' indicado
		URL url = loader.getResource(resourceName);
		if (url != null)
		{
			//serverLog.info("Resource '" + name  + "' founded [" + url.toString() + "]");
			return url.openStream();
		}
		
		// tenta obter o recurso direto do disco
		try
		{
			InputStream is = new FileInputStream(name);
			//serverLog.info("Resource '" + name  + "' founded on disk");
			return is;
		}
		catch (IOException e)
		{
			// suprime os erros
		}
		
		if (loader instanceof URLClassLoader)
		{
			URL[] urls = ((URLClassLoader)loader).getURLs();
			StringBuilder full = new StringBuilder();
			for (int c = 0; c < urls.length; c++)
			{
				full.append( urls[c] );
				if (c < urls.length-1) full.append("; ");
			}
			//serverLog.info("Resource '" + name + "' not found in [" + full + "]");
			throw new FileNotFoundException("Resource '" + resourceName + "' not found in [" + full + "]");
		}*/
		URL url = findResource(loader, resourceName);
		if (url != null)
			return url.openStream();
		
		throw new FileNotFoundException("Resource '" + resourceName + "' not found");
	}
	
	/**
	 * Retorna um <code>InputStream</code> associado a um recurso. O método tenta localizar o 
	 * recurso através de todas instâncias <code>ClassLoader</code> registradas. Caso o recurso não 
	 * seja encontrado, é feita uma tentativa em carregar o recurso a partir do disco.
	 * 
	 * @param name Nome do recurso.
	 * @return Instância <code>InputStream</code> associada ao um recurso.
	 * @throw FileNotFoundException se o recurso não foi encontrado.
	 * @throws IOException se ocorrer algum erro de leitura/conexão.
	 */
	public static InputStream getResourceAsStream( String resourceName ) throws IOException
	{
		/*List<URL> list = new LinkedList<URL>();
		ClassLoader current;
		
		// itera entre os 'class loaders' registrados
		for (int i = 0; null != (current = ClassLoaderManager.get(i)); ++i)
		{
			list = findResources(current, resourceName);
			if (list.size() > 0)
			{
				try
				{
					return getResourceAsStream(current, resourceName);
				} catch (IOException e)
				{ 
					// supmrime os erros 
				}
			}
			
			if (current instanceof URLClassLoader)
			{
				URL[] urls = ((URLClassLoader)current).getURLs();
				StringBuilder full = new StringBuilder();
				for (int c = 0; c < urls.length; c++)
				{
					full.append( urls[c] );
					if (c < urls.length-1) full.append("; ");
				}
				//serverLog.info("Resource '" + resourceName + "' not found in [" + full + "]");
			}
		}*/
		URL url = findResource(resourceName);
		if (url != null)
			return url.openStream();
		
		throw new FileNotFoundException("Resource '" + resourceName + "' not found");
	}

	/**
	 * Retorna um <code>InputStream</code> associado a um recurso. O método tenta localizar o 
	 * recurso através do <code>ClassLoader</code> especificado. Caso o recurso não seja encontrado, 
	 * é feita uma tentativa em carregar o recurso a partir do disco.
	 * 
	 * @param loader Instância do <i>class loader</i> a ser utilizado na busca pelo recurso.
	 * @param name URL do recurso.
	 * @return Instância <code>InputStream</code> associada ao um recurso.
	 * @throws IOException se o recurso não foi encontrado ou ocorrer algum erro de leitura.
	 */
	public static InputStream getResourceAsStream( ClassLoader loader, URL resource ) 
		throws IOException
	{
		return resource.openStream();
	}

	/**
	 * Retorna um <code>InputStream</code> associado a um recurso. O método tenta localizar o 
	 * recurso através do <code>ClassLoader</code> do framework. Caso o recurso não seja encontrado, 
	 * é feita uma tentativa em carregar o recurso a partir do disco.
	 * 
	 * @param name URL do recurso.
	 * @return Instância <code>InputStream</code> associada ao um recurso.
	 * @throws IOException se o recurso não foi encontrado ou ocorrer algum erro de leitura.
	 */
	public static InputStream getResourceAsStream( URL resource ) throws IOException
	{
		return resource.openStream();
	}
	
	/**
	 * Localiza todos os recursos que coincidem com o nome especificado e retorna-os como uma lista 
	 * de URL's. 
	 * 
	 * @param loader Instância <code>ClassLoader</code> do <i>class loader</i> através do qual os 
	 *     recursos serão acessados.
	 * @param resourceName Nome do recurso.
	 * @return instância <code>List&lt;URL&gt;</code> contendo as URL's dos recursos encontrados.
	 * @throws IOException se ocorrer algum erro de E/S ao localizar os recursos.
	 */
	public static List<URL> findResources( ClassLoader loader, String resourceName ) 
		throws IOException
	{
		List<URL> list = new ArrayList<URL>();
		
		// procura o recurso através do 'class loader'
		if (loader instanceof URLClassLoader)
		{
			Enumeration<URL> it = ((URLClassLoader)loader).findResources(resourceName);
			while ( it.hasMoreElements() ) list.add( it.nextElement() );
		}
		else
			list.add( loader.getResource(resourceName) );
	
		// tenta localizar o recurso em disco
		File file = new File(resourceName);
		if ( file.exists() ) list.add( file.toURI().toURL() );
		
		return list;
	}

	/**
	 * Localiza todos os recursos que coincidem com o nome especificado e retorna-os como uma lista 
	 * de URL's. Os recursos serão localizados através dos <i>class loaders</i> registrados.  
	 * 
	 * @param resourceName Nome do recurso.
	 * @return instância <code>List&lt;URL&gt;</code> contendo as URL's dos recursos encontrados.
	 * @throws IOException se ocorrer algum erro de E/S ao localizar os recursos.
	 */
	public static List<URL> findResources( String resourceName ) throws IOException
	{
		List<URL> list = new LinkedList<URL>();
		ClassLoader current;
		
		// itera entre os 'class loaders' registrados
		for (int i = 0; null != (current = ClassLoaderManager.get(i)); ++i)
			list.addAll( findResources(current, resourceName) );
		
		return list;		
	}
	
	public static URL findResource( String resourceName ) throws IOException
	{
		List<URL> temp = findResources(resourceName);
		if ( temp.size() == 0 ) return null;
		return temp.get(0);
	}
	
	public static URL findResource( ClassLoader loader, String resourceName ) throws IOException
	{
		List<URL> temp = findResources(loader, resourceName);
		if ( temp.size() == 0 ) return null;
		return temp.get(0);
	}
	
	public static String getCurrentDirectory()
	{
		File current = new File(".");
		return current.getAbsolutePath();
	}
	
}
