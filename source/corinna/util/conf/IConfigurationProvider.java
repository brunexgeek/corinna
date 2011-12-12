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

import java.io.IOException;


/**
 * Define a interface de um provedor de configurações. Um provedor de configuração é responsável 
 * pelo acesso e manipulação de entradas de configuração a partir de um repositório arbitrário
 * (arquivo em disco, banco de dados, etc).
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.1
 * @see IConfiguration
 */
public interface IConfigurationProvider
{
	
	
	public ISection getConfiguration();

	public void update() throws IOException;
	
	public void update( String section ) throws IOException; 
	
	/**
	 * Recupera todas as entradas de configuração a partir do armazenamento persistente.
	 *  
	 * @param section Nome da seção.
	 * @throws IOException se ocorrer algum erro na recuperação das entradas.
	 */
	public void refresh() throws IOException;
	
	/**
	 * Recupera as entradas de configuração de uma seção específica a partir do armazenamento 
	 * persistente.
	 *  
	 * @param section Nome da seção.
	 * @throws IOException se ocorrer algum erro na recuperação das entradas.
	 */
	public void refresh( String section ) throws IOException;
	
	public void addListener( IConfigurationListener listener, ISection target );
	
	public void removeListener( IConfigurationListener listener, ISection target );
	
	//public boolean hasListener( IConfigurationListener listener );
	
	//public ISection getListenerTarget( IConfigurationListener listener );
	
}
