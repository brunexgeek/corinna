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
 * Disparada quando um recurso está indisponibilidade ou ocupado. Esta exceção é representada
 * pelo valor inteiro 0x04.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public class BusyException extends GenericException
{

	private static final long serialVersionUID = -3441862887731242958L;
	
	private static final int ERROR_CODE = 0x1001;

	public BusyException()
	{
		super();
	}

	public BusyException( String message )
	{
		super(message);
	}

	public BusyException( String message, Throwable cause )
	{
		super(message, cause);
	}

	public BusyException( Throwable cause )
	{
		super(cause);
	}

	@Override
	public int getErrorCode()
	{
		return ERROR_CODE;
	}
	
}
