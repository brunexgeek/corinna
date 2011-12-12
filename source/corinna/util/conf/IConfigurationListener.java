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

import corinna.exception.ConfigurationException;



/**
 * Define a interface para <i>listeners</i> do mecanismo de configuração.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public interface IConfigurationListener 
{

	/**
	 * Este método é invocado quando ocorre uma alteração no estado de uma seção de configuração
	 * a qual o objeto atual está associado.
	 * 
	 * @param event Instância {@link IConfigurationEvent} que contém as informações sobre o evento.
	 * @throws InvalidConfigurationException se o objeto atual considerar que a configuração
	 *     
	 * @throws ConfigurationNotFoundException
	 */
	public void configurationEventReceived( IConfigurationEvent event ) 
		throws ConfigurationException;
	
}
