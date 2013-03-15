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


import java.util.ArrayList;
import java.util.List;

import corinna.exception.LifecycleException;
import corinna.thread.ObjectLocker;



/**
 * Implementa os métodos básicos de gerenciamento de ciclo de vida. A classe
 * {@link LifecycleManager} permite reusar a implementação do gerenciamento de ciclo de vida em
 * classes cuja hierarquia de heranças já esteja estabelecida, ou seja, onde a única opção é 
 * implementar a interface {@link ILifecycle}.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 2.0
 * @version 2.0
 */
public class LifecycleManager implements ILifecycleBase
{

	private LifecycleState state = LifecycleState.NEW;

	private ObjectLocker stateLocker;

	private List<ILifecycleListener> listeners;

	private ObjectLocker listenersLocker;

	/**
	 * Define as transições válidas entre os estados do ciclo de vida. O valor numérico para cada
	 * transição correspondem ao valor ordinal de entradas da enumeração {@link StateTransition}.
	 */
	private static final int transitionTable[][] =
	{
		{2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1 }, // NEW
		{0, 2, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0 }, // INITIALIZING
		{0, 0, 2, 1, 1, 0, 0, 1, 1, 1, 0, 1 }, // INITIALIZED
		{0, 0, 0, 2, 1, 0, 0, 0, 0, 1, 0, 0 }, // STARTING
		{0, 0, 0, 0, 2, 1, 1, 0, 0, 1, 0, 0 }, // STARTED
		{0, 0, 0, 0, 0, 2, 1, 0, 0, 1, 0, 0 }, // STOPPING
		{0, 0, 0, 1, 1, 0, 2, 1, 1, 1, 0, 1 }, // STOPPED
		{0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 0, 0 }, // DESTROYING
		{0, 1, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0 }, // DESTROYED
		{0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 0, 1 }, // FAILED
		{0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 2, 0 }, // MUST_STOP
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 }, // MUST_DESTROY
	};
	
	/**
	 * Cria um gerenciador de ciclo de vida. O estado inicial é definido como
	 * {@link LifecycleState#NEW}.
	 */
	public LifecycleManager()
	{
		listeners = new ArrayList<ILifecycleListener>();
		listenersLocker = new ObjectLocker();
		stateLocker = new ObjectLocker();
	}

	@Override
	public void addLifecycleListener( ILifecycleListener listener )
	{
		listenersLocker.writeLock();
		try
		{
			listeners.add(listener);
		} finally
		{
			listenersLocker.writeUnlock();
		}
	}

	@Override
	public void removeLifecycleListener( ILifecycleListener listener )
	{
		listenersLocker.writeLock();
		try
		{
			listeners.remove(listener);
		} finally
		{
			listenersLocker.writeUnlock();
		}
	}

	@Override
	public LifecycleState getLifecycleState()
	{
		LifecycleState state;
		stateLocker.readLock();
		try
		{
			state = this.state;
		} finally
		{
			stateLocker.readUnlock();
		}
		return state;
	}

	public void setLifecycleState( LifecycleState state )
	{
		stateLocker.writeLock();
		try
		{
			this.state = state;
		} finally
		{
			stateLocker.writeUnlock();
		}
	}

	/**
	 * Atomicaly, check if can change current lifecycle state to the specified and do the changes.
	 * 
	 * @param state New state
	 * @throws LifecycleException if the transition is forbbiden by lifecycle.
	 */
	public StateTransition changeLifecycleState( LifecycleState state ) throws LifecycleException
	{
		stateLocker.writeLock();
		try
		{
			StateTransition result = canChangeState(this.state, state);
			if (result == StateTransition.DENY)
				throw new LifecycleException("Can not change from " + this.state + " to " + state);
			if (result == StateTransition.ACCEPT) this.state = state;
			return result;
		} finally
		{
			stateLocker.writeUnlock();
		}
	}
	
	@Override
	public String getLifecycleStateName()
	{
		return getLifecycleState().name();
	}

	protected static StateTransition canChangeState( LifecycleState fromState, LifecycleState toState )
	{
		int value = transitionTable[fromState.ordinal()][toState.ordinal()];
		switch (value)
		{
			case 0: return StateTransition.DENY;
			case 1: return StateTransition.ACCEPT;
			case 2: return StateTransition.IGNORE;
			default: return StateTransition.DENY;
		}
	}

	
	public enum StateTransition
	{

		/**
		 * The transition was denied (ordinal 0).
		 */
		DENY,
		
		/**
		 * The transition was accepted (ordinal 1).
		 */
		ACCEPT,
		
		/**
		 * The transition must be ignored (ordinal 2).
		 */
		IGNORE

		
	}
	
}
