/*
 * Copyright 2011-2012 Bruno Ribeiro
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

package corinna.http.core;


import java.util.Collection;

import corinna.core.IBindletRegistration;


public interface IHttpBindletRegistration extends IBindletRegistration
{

	public void addMapping( String... urlPatterns );

	public Collection<String> getMappings();

	/**
	 * Gets the names of the methods supported by the underlying bindlet.
	 * 
	 * <p>
	 * This is the same set of methods included in the <code>Allow</code> response header in
	 * response to an <code>OPTIONS</code> request method processed by the underlying bindlet.
	 * </p>
	 * 
	 * @return array of names of the methods supported by the underlying bindlet
	 */
	public String[] getBindletMethods();

}
