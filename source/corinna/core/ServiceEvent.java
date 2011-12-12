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


import corinna.core.TypedEvent;


public class ServiceEvent extends TypedEvent<ServiceEventType>
{

	private static final long serialVersionUID = 8340262072426339114L;

	/**
	 * Constrói um novo evento de serviço com os argumentos especificados.
	 * 
	 * @param sender
	 *            Instância {@link IService} do serviço que disparou o evento.
	 * @param type
	 *            Valor da enumeração {@link ServiceEventType} indicando o tipo de evento
	 *            (requerido).
	 * @param data
	 *            Objeto de tipo arbitrário contendo dados adicionais, se existirem.
	 */
	public ServiceEvent( ServiceEventType type )
	{
		super(type);
	}

}
