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


import corinna.exception.LifecycleException;


/**
 * Common interface for component life cycle methods. Catalina components may implement this
 * interface (as well as the appropriate interface(s) for the functionality they support) in order
 * to provide a consistent mechanism to start and stop the component. <br>
 * The valid state transitions for components that support {@link ILifecycle} are:
 * 
 * <pre>
 *            start()
 *  -----------------------------
 *  |                           |
 *  | init()                    |
 * NEW ->-- INITIALIZING        |
 * | |           |              |     ------------------<-----------------------
 * | |           |auto          |     |                                        |
 * | |          \|/    start() \|/   \|/     auto          auto         stop() |
 * | |      INITIALIZED -->-- STARTING_PREP -->- STARTING -->- STARTED -->---  |
 * | |         |                                                  |         |  |
 * | |         |                                                  |         |  |
 * | |         |                                                  |         |  |
 * | |destroy()|                                                  |         |  |
 * | -->-----<--       auto                    auto               |         |  |
 * |     |       ---------<----- MUST_STOP ---------------------<--         |  |
 * |     |       |                                                          |  |
 * |    \|/      ---------------------------<--------------------------------  ^
 * |     |       |                                                             |
 * |     |      \|/            auto                 auto              start()  |
 * |     |  STOPPING_PREP ------>----- STOPPING ------>----- STOPPED ---->------
 * |     |                                ^                  |  |  ^
 * |     |               stop()           |                  |  |  |
 * |     |       --------------------------                  |  |  |
 * |     |       |                                  auto     |  |  |
 * |     |       |                  MUST_DESTROY------<-------  |  |
 * |     |       |                    |                         |  |
 * |     |       |                    |auto                     |  |
 * |     |       |    destroy()      \|/              destroy() |  |
 * |     |    FAILED ---->------ DESTROYING ---<-----------------  |
 * |     |                        ^     |                          |
 * |     |     destroy()          |     |auto                      |
 * |     -------->-----------------    \|/                         |
 * |                                 DESTROYED                     |
 * |                                                               |
 * |                            stop()                             |
 * --->------------------------------>------------------------------
 *   
 * Any state can transition to FAILED.
 * 
 * Calling start() while a component is in states STARTING_PREP, STARTING or
 * STARTED has no effect.
 * 
 * Calling start() while a component is in state NEW will cause init() to be
 * called immediately after the start() method is entered.
 * 
 * Calling stop() while a component is in states STOPPING_PREP, STOPPING or
 * STOPPED has no effect.
 * 
 * Calling stop() while a component is in state NEW transitions the component
 * to STOPPED. This is typically encountered when a component fails to start and
 * does not start all its sub-components. When the component is stopped, it will
 * try to stop all sub-components - even those it didn't start.
 * 
 * MUST_STOP is used to indicate that the {@link #stop()} should be called on
 * the component as soon as {@link #start()} exits. It is typically used when a
 * component has failed to start.
 * 
 * MUST_DESTROY is used to indicate that the {@link #stop()} should be called on
 * the component as soon as {@link #stop()} exits. It is typically used when a
 * component is not designed to be restarted.
 * 
 * Attempting any other transition will throw {@link LifecycleException}.
 * 
 * </pre>
 * 
 * The {@link ILifecycleEvent}s fired during state changes are defined in the methods that trigger
 * the changed. No {@link ILifecycleEvent}s are fired if the attempted transition is not valid.
 * 
 * TODO: Not all components may transition from STOPPED to STARTING_PREP. These components should
 * use MUST_DESTROY to signal this.
 * 
 * @author Craig R. McClanahan
 * @version $Id: Lifecycle.java 1151016 2011-07-26 08:25:46Z markt $
 */
public interface ILifecycle extends ILifecycleBase
{
	
	/**
	 * Prepare the component for starting. This method should perform any initialization required
	 * post object creation. The following {@link ILifecycleEvent}s will be fired in the following
	 * order:
	 * <ol>
	 * <li>INIT_EVENT: On the successful completion of component initialization.</li>
	 * </ol>
	 * 
	 * @exception LifecycleException
	 *                if this component detects a fatal error that prevents this component from
	 *                being used
	 */
	public void init() throws LifecycleException;

	/**
	 * Prepare for the beginning of active use of the public methods other than property
	 * getters/setters and life cycle methods of this component. This method should be called before
	 * any of the public methods other than property getters/setters and life cycle methods of this
	 * component are utilized. The following {@link ILifecycleEvent}s will be fired in the following
	 * order:
	 * <ol>
	 * <li>BEFORE_START_EVENT: At the beginning of the method. It is as this point the state
	 * transitions to {@link LifecycleState#STARTING_PREP}.</li>
	 * <li>START_EVENT: During the method once it is safe to call start() for any child components.
	 * It is at this point that the state transitions to {@link LifecycleState#STARTING} and that
	 * the public methods other than property getters/setters and life cycle methods may be used.</li>
	 * <li>AFTER_START_EVENT: At the end of the method, immediately before it returns. It is at this
	 * point that the state transitions to {@link LifecycleState#STARTED}.</li>
	 * </ol>
	 * 
	 * @exception LifecycleException
	 *                if this component detects a fatal error that prevents this component from
	 *                being used
	 */
	public void start() throws LifecycleException;

	/**
	 * Gracefully terminate the active use of the public methods other than property getters/setters
	 * and life cycle methods of this component. Once the STOP_EVENT is fired, the public methods
	 * other than property getters/setters and life cycle methods should not be used. The following
	 * {@link ILifecycleEvent}s will be fired in the following order:
	 * <ol>
	 * <li>BEFORE_STOP_EVENT: At the beginning of the method. It is at this point that the state
	 * transitions to {@link LifecycleState#STOPPING_PREP}.</li>
	 * <li>STOP_EVENT: During the method once it is safe to call stop() for any child components. It
	 * is at this point that the state transitions to {@link LifecycleState#STOPPING} and that the
	 * public methods other than property getters/setters and life cycle methods may no longer be
	 * used.</li>
	 * <li>AFTER_STOP_EVENT: At the end of the method, immediately before it returns. It is at this
	 * point that the state transitions to {@link LifecycleState#STOPPED}.</li>
	 * </ol>
	 * 
	 * Note that if transitioning from {@link LifecycleState#FAILED} then the three events above
	 * will be fired but the component will transition directly from {@link LifecycleState#FAILED}
	 * to {@link LifecycleState#STOPPING}, bypassing {@link LifecycleState#STOPPING_PREP}
	 * 
	 * @exception LifecycleException
	 *                if this component detects a fatal error that needs to be reported
	 */
	public void stop() throws LifecycleException;

	/**
	 * Prepare to discard the object. The following {@link ILifecycleEvent}s will be fired in the
	 * following order:
	 * <ol>
	 * <li>DESTROY_EVENT: On the successful completion of component destruction.</li>
	 * </ol>
	 * 
	 * @exception LifecycleException
	 *                if this component detects a fatal error that prevents this component from
	 *                being used
	 */
	public void destroy() throws LifecycleException;
	


}
