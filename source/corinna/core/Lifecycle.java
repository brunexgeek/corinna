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

package corinna.core;

import corinna.core.LifecycleManager.StateTransition;
import corinna.exception.LifecycleException;


public abstract class Lifecycle implements ILifecycle
{

	protected LifecycleManager lifecycle;
	
	protected void onInit() throws LifecycleException
	{
		// nothing to do
	}

	protected void onStart() throws LifecycleException
	{
		// nothing to do
	}

	protected void onStop() throws LifecycleException
	{
		// nothing to do
	}
	
	protected void onDestroy() throws LifecycleException
	{
		// nothing to do
	}
	
	public Lifecycle()
	{
		lifecycle = new LifecycleManager();
	}
	
	@Override
	public void init() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.INITIALIZING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			onInit();
			lifecycle.changeLifecycleState(LifecycleState.INITIALIZED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
			throw new LifecycleException("Error initializing component", e);
		}
	}

	@Override
	public void start() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.STARTING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			onStart();
			lifecycle.changeLifecycleState(LifecycleState.STARTED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
			throw new LifecycleException("Error starting component", e);
		}
	}

	@Override
	public void stop() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.STOPPING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			onStop();
			lifecycle.changeLifecycleState(LifecycleState.STOPPED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
			throw new LifecycleException("Error stopping component", e);
		}
	}

	@Override
	public void destroy() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.DESTROYING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			onDestroy();
			lifecycle.changeLifecycleState(LifecycleState.DESTROYED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
			throw new LifecycleException("Error destroying component", e);
		}
	}
	
	@Override
	public final void addLifecycleListener( ILifecycleListener listener )
	{
		lifecycle.addLifecycleListener(listener);
	}

	@Override
	public final void removeLifecycleListener( ILifecycleListener listener )
	{
		lifecycle.removeLifecycleListener(listener);
	}

	@Override
	public final LifecycleState getLifecycleState()
	{
		return lifecycle.getLifecycleState();
	}

	@Override
	public final String getLifecycleStateName()
	{
		return lifecycle.getLifecycleStateName();
	}

}
