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


import javax.bindlet.IBindletService;

import corinna.service.bean.IServiceBean;
import corinna.util.IComponentInformation;
import corinna.util.conf.ISection;


/**
 * Um serviço é uma abstração de uma funcionalidade que o servidor deve prover. Serviços típicos são
 * o fornecimento de TTS e ASR. <br/>
 * <br/>
 * Um serviço pode ter um ou mais manipuladores para poder atender requisições sob diversos
 * protocolos. Um serviço de TTS, por exemplo, pode ser oferecido através de uma interface SOAP e
 * uma interface MRCP (cada qual através de um manipulador individual).
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 2.0
 * @version 2.0
 * @see IServiceHandler
 */
public interface IService extends IServerRequestListener, ILifecycle
{

	Class<?>[] CONSTRUCTOR_ARGS = { IServiceConfig.class, IServer.class };

	public String getName();
	
	public IBindletService getBindletService();
	
	public IServer getServer();

	public IContext<?,?> removeContext( String name );

	public IContext<?,?> removeContext( IContext<?,?> context );

	public IContext<?,?> getContext( String name );
		
	public void addContext( IContext<?,?> context );
	
	public IComponentInformation getServiceInfo();
	
}
