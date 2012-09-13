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
//package corinna.core;
//
//
//import java.net.URL;
//
//import corinna.core.IRemoteServiceStatistics;
//import corinna.network.IProtocol;
//
//
//
///**
// * 
// * @author Bruno Ribeiro
// * @since 2.0
// * @version 2.0
// */
//public interface IRemoteService extends IService
//{
//
//	/**
//	 * Return the location of remote service. The interpretation of URL is protocol dependent.
//	 * 
//	 * @return Port number of remote service.
//	 */
//	public URL getUrl();
//
//	/**
//	 * Return the protocol used to communicate with the service.
//	 * 
//	 * @return {@link IProtocol} instance of protocol.
//	 */
//	public IProtocol<?,?> getProtocol();
//
//	/**
//	 * Return statistical informations about the service execution.
//	 * 
//	 * @return {@link IRemoteServiceStatistics} instance containing the statistics.
//	 */
//	public IRemoteServiceStatistics getStatistics();
//
//}
