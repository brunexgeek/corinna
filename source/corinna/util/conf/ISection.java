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
 * Define a interface para seções de uma configuração. Seções são utilizadas para encapsular as 
 * entradas de configuração de um mesmo domínio.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public interface ISection
{
	
	
	/**
	 * Retorna o valor de uma entrada a partir de uma chave.
	 * 
	 * @param key Nome da chave.
	 * @return Instância <code>String</code> representando o valor da entrada.
	 * @throws ConfigurationNotFoundException se nenhuma entrada foi encontrada. 
	 */
	public String getValue( String key ) throws ConfigurationNotFoundException;
	
	/**
	 * Retorna o valor de uma entrada a partir de uma chave.
	 * 
	 * @param key Nome da chave.
	 * @param defaultValue Valor padrão a ser retornado caso não exista um entrada com a chave 
	 *     informada. Pode ser <code>null</code>.
	 * @return Instância <code>String</code> contendo o valor da entrada ou o valor padrão.
	 */
	public String getValue( String key, String defaultValue );

	/**
	 * Define o valor de uma entrada a partir de uma chave. Se a entrada já existir, seu valor é 
	 * atualizado, e caso a entrada ainda não exista, uma nova entrada é criada com o valor informado.
	 * <br/><br/>
	 * Não são permitidas entradas com valor <code>null</code>. Caso o valor especificado seja
	 * <code>null</code> e nenhuma entrada com a chave indicada exista, nada é feito. Entretanto, 
	 * caso já exista uma entrada com a chave, o método irá remove-la.
	 * 
	 * @param key Nome da chave.
	 * @param value Novo valor da entrada. Use <code>null</code> para remover entradas existentes.
	 */
	public void setValue( String key, String value );

	/**
	 * Retorna uma valor lógico indicando se existe alguma entrada com a chave especificada.
	 *
	 * @param key Nome da chave.
	 * @return <code>True</code> se existir alguma entrada com chave informado. Caso 
	 *     contrário, retorna <code>false</code>.
	 */
	public boolean containsKey( String key );
	
	/**
	 * Retorna o nome da seção.
	 * 
	 * @return Instância <code>String</code> indicando o nome da seção.
	 */
	public String getName();
	
	/**
	 * Retorna um vetor contendo todas as chaves das entradas armazenadas na seção.
	 * 
	 * @return Vetor de <code>String</code> contendo as chaves das entradas de configuração.
	 */
	public String[] getKeys();

	/**
	 * Retorna um valor lógico indicando se as entradas são somente para leitura.
	 * 
	 * @return <code>True</code> se as entradas são somente para leitura ou <code>false</code>
	 *     caso contrário.
	 */
	public boolean isReadOnly();

	/**
	 * Define um valor lógico indicando se as entradas devem ser somente para leitura.
	 * 
	 * @param readOnly <code>True</code> se as entradas devem ser somente para leitura ou 
	 *     <code>false</code> caso contrário.
	 * @param recursive <code>True</code> se as entradas das sub-seções também devem ser
	 *     afetadas ou <code>false</code> caso contrário.
	 */
	public void setReadOnly( boolean readOnly, boolean recursive ); 
	
	/**
	 * Remove todas as entradas da seção. Opcionalmente, pode-se indicar que a remoção será 
	 * recursiva, fazendo com que as entradas das sub-seções também sejam removidas. 
	 * Caso seja especificado que a remoção seja recursiva, somente as entradas serão removidas
	 * e não as sub-seções em si. Para remover também as subseções, utilize o método 
	 * {@link clearAll()}.
	 * 
	 * @param recursive <code>True</code> se as entradas das sub-seções também devem ser
	 *     removidas ou <code>false</code> caso contrário. 
	 */
	public void clear( boolean recursive );
	
	/**
	 * Remove todas as entradas e sub-seções da seção atual.
	 */
	public void clearAll();
	
	/**
	 * Retorna um valor lógico indicando se existe alguma seção com o nome informado.
	 * 
	 * @param section Nome da seção.
	 * @return <code>True</code> se existir alguma seção com o nome informado. Caso 
	 *     contrário, retorna <code>false</code>.
	 */
	public boolean containsSection( String section );
	
	/**
	 * Retorna a seção que corresponde ao nome informado.
	 * 
	 * @param section Nome da seção.
	 * @return Instância {@link ISection} da seção que corresponde ao nome informado.
	 * @throws ConfigurationNotFoundException se nenhuma seção não foi encontrada.
	 */
	public ISection getSection( String section ) throws ConfigurationNotFoundException;
		
	/**
	 * Retorna a seção que corresponde ao nome informado.
	 * 
	 * @param section Nome da seção.
	 * @param defaultSection Instância {@link ISection} da seção que será retornada caso a seção
	 *     especificada não exista. Pode ser <code>null</code>.
	 * @return Instância {@link ISection} da seção que corresponde ao nome informado ou 
	 *     a seção padrão caso a seção não exista.
	 */
	public ISection getSection( String section, ISection defaultSection );
	
	/**
	 * Adiciona uma nova sub-seção. Caso já exista uma sub-seção com o mesmo nome, o método
	 * irá retorna-la. 
	 *  
	 * @param name Nome da seção que será adicionada.
	 * @return Instância {@link ISection} da seção adicionada. 
	 */
	public ISection addSection( String section );
	
	public void removeSection( ISection section );
	
	public void removeSection( String section );
	
	/**
	 * Retorna um vetor contendo as sub-seções.
	 * 
	 * @return Vetor de {@link ISection}.
	 */
	public ISection[] getSections();
	
}
