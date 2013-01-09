///*
// * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package corinna.util.conf;
//
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
//
///**
// * Define a implementação básica para um provedor de configurações.
// * 
// * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
// * @since 1.1
// * @version 1.2
// */
//public abstract class ConfigurationProvider implements IConfigurationProvider 
//{
//
//	/**
//	 * Lista de <i>listeners</i> registrador no provedor.
//	 */
//	private Map<ISection,List<IConfigurationListener>> listeners;
//	
//	private ReadLock readLock;
//	
//	private WriteLock writeLock;
//	
//	/**
//	 * Objeto de configuração associado ao provedor.
//	 */
//	private IConfiguration config;
//	
//	/**
//	 * Cria um provedor de confgiuração definindo o objeto de configuração associado.
//	 * 
//	 * @param config Instância {@link IConfiguration} do objeto de configuração.
//	 */
//	public ConfigurationProvider( IConfiguration config )
//	{
//		if (config == null)
//			throw new IllegalArgumentException("Configuration can't be 'null'.");
//		this.config = config;
//
//		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//		readLock = lock.readLock();
//		writeLock = lock.writeLock();
//		
//		listeners = new HashMap<ISection,List<IConfigurationListener>>();
//	}
//	
//	@Override
//	public IConfiguration getConfiguration() 
//	{
//		return config;
//	}
//
//	@Override
//	public final void addListener( IConfigurationListener listener, ISection target ) 
//	{
//		if (listener == null || target == null) return;
//
//		writeLock.lock();
//		
//		// tenta obter a lista de 'listeners' associados ao alvo indicado
//		List<IConfigurationListener> list = listeners.get(target);
//		if (list == null)
//		{
//			// cria uam lista de 'listeners' vazia para o alvo
//			list = new ArrayList<IConfigurationListener>(1);
//			listeners.put(target, list);
//		}
//		// adiciona o 'listener' caso ainda não esteja registrado 
//		if (list.indexOf(listener) < 0) list.add(listener);
//		
//		writeLock.unlock();
//	}
//
//	@Override
//	public final void removeListener( IConfigurationListener listener, ISection target ) 
//	{
//		if (listener == null || target == null) return;
//		
//		writeLock.lock();
//		listeners.remove(target);
//		writeLock.unlock();
//	}	
//	
//	/**
//	 * Notifica cada <i>listener</i> registrado a respeito de um evento. Serão notificados somente
//	 * os <i>listeners</i> associados ao objeto de configuração definido pelo evento.
//	 * 
//	 * @param event Instância {@link IConfigurationEvent} contendo as informações do evento.
//	 */
//	protected final void notifyListeners( IConfigurationEvent event )
//	{
//		if (event == null || event.getTarget() == null) return;
//
//		readLock.lock();
//		List<IConfigurationListener> list = listeners.get( event.getTarget() );
//		if (list == null) 
//		{
//			readLock.unlock();
//			return;
//		}
//		
//		for (IConfigurationListener current : list)
//		{
//			try
//			{
//				current.configurationEventReceived(event);
//			}
//			catch (Exception e)
//			{
//				// suprime qualquer erro
//			}
//		}
//		readLock.unlock();
//	}
//
//	
//}
