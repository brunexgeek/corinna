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

import javax.bindlet.IComponentInformation;


public final class ContextInfo implements IComponentInformation
{
		
		private String name;
		
		private String version;
		
		private String implementor;
		
		public ContextInfo( String name, String version, String implementor )
		{
			this.name = name;
			this.version = version;
			this.implementor = implementor;
		}
		
		@Override
		public String getComponentName()
		{
			return name;
		}

		@Override
		public String getComponentVersion()
		{
			return version;
		}

		@Override
		public String getComponentImplementor()
		{
			return implementor;
		}
	
}
