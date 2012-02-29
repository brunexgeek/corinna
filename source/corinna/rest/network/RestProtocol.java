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

package corinna.rest.network;

import corinna.network.Protocol;
import corinna.rest.bindlet.IRestBindletRequest;
import corinna.rest.bindlet.IRestBindletResponse;


public final class RestProtocol extends Protocol<IRestBindletRequest, IRestBindletResponse>
{

	private static final String PROTOCOL_SCHEME = "http";

	private static final String PROTOCOL_VERSION = "1.0";

	private static final String PROTOCOL_NAME = "REST";

	private static final String PROTOCOL_IMPLEMENTOR = "Bruno Ribeiro";

	private static final String PROTOCOL_LICENSE = "Apache License";
	
	private static RestProtocol instance = null;

	private RestProtocol()
	{
	}
	
	public static RestProtocol getInstance()
	{
		if (instance == null)
			instance = new RestProtocol();
		return instance;
	}
	
	@Override
	public String getScheme()
	{
		return PROTOCOL_SCHEME;
	}

	@Override
	public String getVersion()
	{
		return PROTOCOL_VERSION;
	}

	@Override
	public String getName()
	{
		return PROTOCOL_NAME;
	}

	@Override
	public String getImplementor()
	{
		return PROTOCOL_IMPLEMENTOR;
	}

	@Override
	public String getDescription()
	{
		return "";
	}

	@Override
	public String getLicense()
	{
		return PROTOCOL_LICENSE;
	}
	
	
}
