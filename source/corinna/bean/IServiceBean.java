/*
 * Copyright 2011-2013 Bruno Ribeiro
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

package corinna.bean;

import corinna.core.parser.IDomainParser;


public interface IServiceBean
{

	/**
	 * Argument types for default constructor of a bindlet. This constructor is used by a
	 * {@link IDomainParser} implementation.
	 */
	public static final Class<?>[] CONSTRUCTOR_ARGS = { IBeanConfig.class };
	
	public String getName();
	
	public IBeanConfig getConfig();
	
}
