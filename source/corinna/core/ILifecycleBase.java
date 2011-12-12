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


public interface ILifecycleBase
{

	/**
	 * Add a LifecycleEvent listener to this component.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addLifecycleListener( ILifecycleListener listener );

	/**
	 * Get the life cycle listeners associated with this life cycle. If this component has no
	 * listeners registered, a zero-length array is returned.
	 */
	// public ILifecycleListener[] findLifecycleListeners();

	/**
	 * Remove a LifecycleEvent listener from this component.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeLifecycleListener( ILifecycleListener listener );

	/**
	 * Obtain the current state of the source component.
	 * 
	 * @return The current state of the source component.
	 */
	public LifecycleState getLifecycleState();

	/**
	 * Obtain a textual representation of the current component state. Useful for JMX.
	 */
	public String getLifecycleStateName();
}
