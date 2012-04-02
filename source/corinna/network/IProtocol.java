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

package corinna.network;


/**
 * Interface that encapsulate some informations about a protocol.
 * 
 * @author Bruno Ribeiro
 * @since 1.0
 * @version 1.0
 */
public interface IProtocol<R, P>
{

	public boolean haveCapability( String capability );

	/**
	 * Returns the protocol scheme. The protocol scheme is the first part in a URI (like
	 * <code>http</code> in <code>http://example.com</code>).
	 * 
	 * @return
	 */
	public String getScheme();

	/**
	 * Returns the protocol version, like <code>1.1</code> for HTTP.
	 */
	public String getVersion();

	/**
	 * Returns the protocol name.
	 */
	public String getName();
	
	/**
	 * Returns the name of the protocol implementator.
	 * 
	 * @return
	 */
	public String getImplementor();
	
	/**
	 * Returns a description of the protocol, including third-part license informations.
	 * @return
	 */
	public String getDescription();
	
	
	/**
	 * Returns the license name which the protocol is distributed.
	 * 
	 * @return
	 */
	public String getLicense();
	
	public Class<?> getRequestClass();
	
	public Class<?> getResponseClass();

}
