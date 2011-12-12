///*
// * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package corinna.service.bindlet.rest;
//
//
//import org.apache.log4j.Logger;
//
//import corinna.exception.BindletException;
//import corinna.exception.IncompleteImplementationException;
//import corinna.exception.InvalidRpcClassException;
//import corinna.exception.InvocationTargetException;
//import corinna.service.bindlet.BindletContextInfo;
//import corinna.service.bindlet.IBindletConfig;
//import corinna.service.rpc.MethodRunner;
//import corinna.util.IComponentInformation;
//
//
//public class DefaultRestBindlet extends RestBindlet
//{
//
//	private static final long serialVersionUID = -579681351761760082L;
//
//	private static final Logger log = Logger.getLogger(DefaultRestBindlet.class);
//	
//	private MethodRunner runner = null;
//
//	public DefaultRestBindlet( IBindletConfig config ) throws BindletException
//	{
//		super(config);
//	}
//
//	
//	/*public DefaultRestBindlet( String name, Class<?> intfClass, Class<?> implClass, Object data )
//		throws InvocationTargetException, IncompleteImplementationException,
//		InvalidRpcClassException
//	{
//		super(name);
//		runner = new MethodRunner(intfClass, implClass, data);
//	}*/
//
//	@Override
//	public boolean process( RestBindletRequest request, RestBindletResponse response )
//		throws BindletException
//	{
//		if (log.isDebugEnabled())
//			log.debug("Received procedure call: " + request);
//
//		try
//		{
//			Object result = runner.callMethod(request);
//			response.setReturnValue(result);
//			System.out.println("Return: " + result);
//		} catch (Exception e)
//		{
//			throw new BindletException("Error invoking method", e);
//		}
//		return true;
//	}
//
//	@Override
//	public IComponentInformation getBindletInfo()
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void initInternal( ) throws BindletException
//	{
//		// TODO Auto-generated method stub
//	}
//
//
//	@Override
//	protected void destroyInternal( IBindletConfig config ) throws BindletException
//	{
//		// TODO Auto-generated method stub		
//	}
//
//}
