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


public enum LifecycleState
{
	
	NEW(false, null, 0, true),
	
	INITIALIZING(false, LifecycleEventType.BEFORE_INIT_EVENT, 1, false),
	
	INITIALIZED(false, LifecycleEventType.AFTER_INIT_EVENT, 1, true),
	
	STARTING(true, LifecycleEventType.START_EVENT, 2, false),
	
	STARTED(true, LifecycleEventType.AFTER_START_EVENT, 2, true),
	
	STOPPING(false, LifecycleEventType.STOP_EVENT, 3, false),
	
	STOPPED(false, LifecycleEventType.AFTER_STOP_EVENT, 3, true),

	DESTROYING(false, LifecycleEventType.BEFORE_DESTROY_EVENT, 5, false),
	
	DESTROYED(false, LifecycleEventType.AFTER_DESTROY_EVENT, 5, true),
	
	FAILED(false, LifecycleEventType.UNSPECIFIED_EVENT, 6, true),
	
	MUST_STOP(true, LifecycleEventType.UNSPECIFIED_EVENT, 3, false),
	
	MUST_DESTROY(false, LifecycleEventType.UNSPECIFIED_EVENT, 5, false);
	
	public static final int WAIT_TIMEOUT = 20000;
	
	private final boolean available;

	private final LifecycleEventType eventType;
	
	private final int group;
	
	/**
	 * Indica se é um estado final no grupo. Tal estado comumente é o estado que se deseja alcançar
	 * após as transições automáticas.
	 */
	private final boolean endState;

	private LifecycleState( boolean available, LifecycleEventType eventType, int group, 
		boolean endState )
	{
		this.available = available;
		this.eventType = eventType;
		this.group = group;
		this.endState = endState;
	}

	/**
	 * May the public methods other than property getters/setters and lifecycle methods be called
	 * for a component in this state? It returns <code>true</code> for any component in any of the
	 * following states:
	 * <ul>
	 * <li>{@link #STARTING}</li>
	 * <li>{@link #STARTED}</li>
	 * <li>{@link #STOPPING_PREP}</li>
	 * <li>{@link #MUST_STOP}</li>
	 * </ul>
	 */
	public boolean isAvailable()
	{
		return available;
	}

	public boolean isEndState()
	{
		return endState;
	}
	
	public LifecycleEventType getEventType()
	{
		return eventType;
	}
	
	public int getGroup()
	{
		return group;
	}
	
	public boolean equalGroup( LifecycleState state )
	{
		return state.group == this.group;
	}
	
	public static boolean waitTransitions( ILifecycle object, LifecycleState state )
	{
		return waitTransitions(object, state, 250);
	}

	/**
	 * Causa a parada momentânea da thread atual até que o estado do ciclo de vida do objeto 
	 * indicado tenha alcançado um estado especificado. O estado alvo e o estado atual devem ser
	 * do mesmo grupo.
	 */
	// TODO: incluir timeout!
	public static boolean waitTransitions( ILifecycle object, LifecycleState state, long interval )
	{
		int fails = 0;
		
		// o estado atual precisa ser do mesmo grupo que o estado alvo
		if ( state.equalGroup( object.getLifecycleState() ) ) return false;
		
		while (object.getLifecycleState() != state)
		{
			try
			{
				Thread.sleep(interval);
			} catch (InterruptedException e)
			{
				++fails;
				if (fails > 5) return false;
			}
		}
		
		return true;
	}
	
	public static boolean waitState( ILifecycle object, LifecycleState state )
	{
		return waitState(object, state, 250);
	}
	
	/**
	 * Causa a parada momentânea da thread atual até que o estado do ciclo de vida do objeto 
	 * indicado tenha alcançado um estado especificado.
	 */
	// TODO: incluir timeout!
	public static boolean waitState( ILifecycle object, LifecycleState state, long interval )
	{
		int fails = 0;
		
		while (object.getLifecycleState() != state)
		{
			try
			{
				Thread.sleep(interval);
			} catch (InterruptedException e)
			{
				++fails;
				if (fails > 5) return false;
			}
		}
		
		return true;
	}
	
	
}
