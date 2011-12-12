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

package corinna.core;


/**
 * Evento que sinaliza que um serviço teve seu servidor (proprietário) alterado. Quando um servidor
 * recebe este evento, o serviço em questão deve ser removido de sua lista.
 * 
 * @author bruno
 *
 */
public class ServiceOwnerChangeEvent extends ServiceEvent
{

	private static final long serialVersionUID = -5474653206717024221L;
	
	private boolean result;
	
	public ServiceOwnerChangeEvent( ServiceEventType type )
	{
		super(type);
		this.result = false;
	}

	public void setResult( boolean result )
	{
		this.result = result;
	}

	public boolean getResult()
	{
		return result;
	}

	
	
}
