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

package javax.bindlet.http;


/**
 * This is the class representing event notifications for changes to sessions within a web
 * application.
 */
public class SessionEvent extends java.util.EventObject
{

	private static final long serialVersionUID = 4927629962482607929L;

	/** Construct a session event from the given source. */
	public SessionEvent( ISession source )
	{
		super(source);
	}

	/** Return the session that changed. */
	public ISession getSession()
	{
		return (ISession) super.getSource();
	}
}
