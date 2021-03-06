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

package corinna.core.parser;

import corinna.core.IDomain;
import corinna.exception.ParseException;


/**
 * Parse a configuration file to get the required information to create a domain and all
 * related elements.
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 */
public interface IDomainParser
{

	public IDomain parse()  throws ParseException;
	
}
