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


/**
 * Representa um objeto que armazena informações de configuração. As informações de configuração 
 * consistem em um conjunto de pares chave-valor organizadas em forma hierarquica através de seções.
 *  
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 * @see ISection
 */
public interface IConfiguration extends ISection
{
	
	/*public IConfigurationProvider getProvider();
	
	public void setProvider( IConfigurationProvider provider );*/
	
}
