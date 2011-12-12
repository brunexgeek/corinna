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

import corinna.core.LifecycleManager.StateTransition;
import corinna.exception.LifecycleException;


public abstract class Lifecycle implements ILifecycle
{

	protected LifecycleManager lifecycle;
	
	protected abstract void initInternal() throws LifecycleException;

	protected abstract void startInternal() throws LifecycleException;	

	protected abstract void stopInternal() throws LifecycleException;
	
	protected abstract void destroyInternal() throws LifecycleException;
	
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
			initInternal();
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
			startInternal();
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
			stopInternal();
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
			destroyInternal();
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
