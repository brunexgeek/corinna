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

package corinna.exception;

/**
 * Disparada quando ocorre algum erro na operação de um DAO. Esta exceção é representada pelo valor 
 * inteiro 0x30.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.2
 */
public class DaoException extends GenericException 
{
	
	/**
	 * Identificador único para serialização.
	 */
	private static final long serialVersionUID = 893019454939080012L;

	/**
	 * Código numérico que representa a exceção.
	 */
	private static final int ERROR_CODE = 0x30;

	
	/**
	 * Constroi uma exceção sem uma descrição.
	 */
	public DaoException() 
	{
		super();
	}
	
	/**
	 * Constroi uma nova exceção especificando a descricao e a referência da causa.
	 * 
	 * @param message Descrição da exceção.
	 * @param cause Referência ao objeto <code>Throwable</code> que causou a exceção.
	 */
	public DaoException( String message, Throwable cause ) 
	{
		super(message, cause);
	}
	
	/**
	 * Constroi uma nova exceção especificando a referência da causa. A descrição da exceção recém 
	 * criada será a mesma da causa especificada. 
	 * 
	 * @param cause Referência ao objeto <code>Throwable</code> que causou a exceção.
	 */
	public DaoException( Throwable cause ) 
	{
		super(cause);
	}

	/**
	 * Constroi uma nova exceção especificando a descrição.
	 * 
	 * @param message Descrição da exceção.
	 */
	public DaoException( String message ) 
	{
		super(message);
	}

	@Override
	public int getErrorCode() 
	{
		return ERROR_CODE;
	}
	
}
