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


public enum LifecycleEventType
{

	/**
	 * The LifecycleEvent type for the "component after init" event.
	 */
	BEFORE_INIT_EVENT("before_init"),

	/**
	 * The LifecycleEvent type for the "component after init" event.
	 */
	AFTER_INIT_EVENT("after_init"),

	/**
	 * The LifecycleEvent type for the "component start" event.
	 */
	START_EVENT("start"),

	/**
	 * The LifecycleEvent type for the "component before start" event.
	 */
	BEFORE_START_EVENT("before_start"),

	/**
	 * The LifecycleEvent type for the "component after start" event.
	 */
	AFTER_START_EVENT("after_start"),

	/**
	 * The LifecycleEvent type for the "component stop" event.
	 */
	STOP_EVENT("stop"),

	/**
	 * The LifecycleEvent type for the "component before stop" event.
	 */
	BEFORE_STOP_EVENT("before_stop"),

	/**
	 * The LifecycleEvent type for the "component after stop" event.
	 */
	AFTER_STOP_EVENT("after_stop"),

	/**
	 * The LifecycleEvent type for the "component after destroy" event.
	 */
	AFTER_DESTROY_EVENT("after_destroy"),

	/**
	 * The LifecycleEvent type for the "component before destroy" event.
	 */
	BEFORE_DESTROY_EVENT("before_destroy"),

	/**
	 * The LifecycleEvent type for the "periodic" event.
	 */
	PERIODIC_EVENT("periodic"),

	/**
	 * The LifecycleEvent type for the "configure_start" event. Used by those components that
	 * use a separate component to perform configuration and need to signal when configuration
	 * should be performed - usually after {@link #BEFORE_START_EVENT} and before
	 * {@link #START_EVENT}.
	 */
	CONFIGURE_START_EVENT("configure_start"),

	/**
	 * The LifecycleEvent type for the "configure_stop" event. Used by those components that use
	 * a separate component to perform configuration and need to signal when de-configuration
	 * should be performed - usually after {@link #STOP_EVENT} and before
	 * {@link #AFTER_STOP_EVENT}.
	 */
	CONFIGURE_STOP_EVENT("configure_stop"),

	UNSPECIFIED_EVENT("unspecified");

	private String name;

	private LifecycleEventType( String name )
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

}