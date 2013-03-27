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
package corinna.util;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;


public class LogbackConfigurator
{

	public static final String LOGBACK_FILE = "config/logback.xml";
	
	protected LogbackConfigurator()
	{
	}

	public static void configure( )
	{
		configure(null);
	}
	
	public static void configure( String path )
	{
		if (path == null || path.isEmpty()) path = LOGBACK_FILE;

		try
		{
			LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
			
			JoranConfigurator conf = new JoranConfigurator();
			conf.setContext(context);
			context.reset();
			conf.doConfigure( ResourceLoader.findResource(path) );
		} catch (Exception e)
		{
			BasicConfigurator.configureDefaultContext();
		}
	}
	
}
