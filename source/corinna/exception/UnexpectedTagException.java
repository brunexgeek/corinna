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
package corinna.exception;

import org.w3c.dom.Element;


public class UnexpectedTagException extends ParseException
{

	private static final long serialVersionUID = 181050241205051493L;

	public UnexpectedTagException( String wrongTag )
	{
		super("Unexpected tag '" + wrongTag + "'");
	}
	
	public UnexpectedTagException( Element wrongElement )
	{
		super("Unexpected tag '" + wrongElement.getLocalName() + "'");
	}
	
	public UnexpectedTagException( String wrongTag, String correctTag )
	{
		super("Unexpected tag '" + wrongTag + "' -- should be '" + correctTag + "'");
	}
	
	public UnexpectedTagException( Element wrongElement, String correctTag )
	{
		super("Unexpected tag '" + wrongElement.getLocalName() + "' -- should be '" + correctTag + "'");
	}
	
}
