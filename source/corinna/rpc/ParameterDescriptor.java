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
package corinna.rpc;


public class ParameterDescriptor
{

	//private int index;
	
	private String name;
	
	private Class<?> type;

	private boolean required;
	
	private boolean isPublic;

	public ParameterDescriptor( String name, Class<?> type, /*int index,*/ boolean required, boolean isPublic )
	{
		this.name = name;
		//this.index = index;
		this.type = type;
		this.required = required;
		this.isPublic = isPublic;
	}
	
	/*public int getIndex()
	{
		return index;
	}*/
	
	
	public String getName()
	{
		return name;
	}
	
	public Class<?> getType()
	{
		return type;
	}

	public boolean isRequired()
	{
		return required;
	}
	
	public boolean isPublic()
	{
		return isPublic;
	}
}
