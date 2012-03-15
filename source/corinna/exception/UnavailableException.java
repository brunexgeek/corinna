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

package corinna.exception;


import javax.bindlet.IBindlet;
import javax.bindlet.exception.BindletException;


/**
 * Defines an exception that a bindlet or filter throws to indicate that it is permanently or
 * temporarily unavailable.
 * 
 * <p>
 * When a bindlet or filter is permanently unavailable, something is wrong with it, and it cannot
 * handle requests until some action is taken. For example, a bindlet might be configured
 * incorrectly, or a filter's state may be corrupted. The component should log both the error and
 * the corrective action that is needed.
 * 
 * <p>
 * A bindlet or filter is temporarily unavailable if it cannot handle requests momentarily due to
 * some system-wide problem. For example, a third-tier server might not be accessible, or there may
 * be insufficient memory or disk storage to handle requests. A system administrator may need to
 * take corrective action.
 * 
 * <p>
 * bindlet containers can safely treat both types of unavailable exceptions in the same way.
 * However, treating temporary unavailability effectively makes the bindlet container more robust.
 * Specifically, the bindlet container might block requests to the bindlet or filter for a period of
 * time suggested by the exception, rather than rejecting them until the bindlet container restarts.
 * 
 * 
 * @author Various
 * @version $Version$
 * 
 */
public class UnavailableException extends BindletException
{

	private static final long serialVersionUID = -7826664431870838411L;

	private boolean permanent;

	private int seconds;

	public UnavailableException( IBindlet<?, ?> bindlet, String msg )
	{
		super(msg);
		permanent = true;
	}

	/**
	 * 
	 * Constructs a new exception with a descriptive message indicating that the bindlet is
	 * permanently unavailable.
	 * 
	 * @param msg
	 *            a <code>String</code> specifying the descriptive message
	 * 
	 */

	public UnavailableException( String msg )
	{
		super(msg);

		permanent = true;
	}

	/**
	 * Constructs a new exception with a descriptive message indicating that the bindlet is
	 * temporarily unavailable and giving an estimate of how long it will be unavailable.
	 * 
	 * <p>
	 * In some cases, the bindlet cannot make an estimate. For example, the bindlet might know that
	 * a server it needs is not running, but not be able to report how long it will take to be
	 * restored to functionality. This can be indicated with a negative or zero value for the
	 * <code>seconds</code> argument.
	 * 
	 * @param msg
	 *            a <code>String</code> specifying the descriptive message, which can be written to
	 *            a log file or displayed for the user.
	 * 
	 * @param seconds
	 *            an integer specifying the number of seconds the bindlet expects to be unavailable;
	 *            if zero or negative, indicates that the bindlet can't make an estimate
	 * 
	 */

	public UnavailableException( String msg, int seconds )
	{
		super(msg);

		if (seconds <= 0)
			this.seconds = -1;
		else
			this.seconds = seconds;

		permanent = false;
	}

	/**
	 * 
	 * Returns a <code>boolean</code> indicating whether the bindlet is permanently unavailable. If
	 * so, something is wrong with the bindlet, and the system administrator must take some
	 * corrective action.
	 * 
	 * @return <code>true</code> if the bindlet is permanently unavailable; <code>false</code> if
	 *         the bindlet is available or temporarily unavailable
	 * 
	 */

	public boolean isPermanent()
	{
		return permanent;
	}

	/**
	 * Returns the number of seconds the bindlet expects to be temporarily unavailable.
	 * 
	 * <p>
	 * If this method returns a negative number, the bindlet is permanently unavailable or cannot
	 * provide an estimate of how long it will be unavailable. No effort is made to correct for the
	 * time elapsed since the exception was first reported.
	 * 
	 * @return an integer specifying the number of seconds the bindlet will be temporarily
	 *         unavailable, or a negative number if the bindlet is permanently unavailable or cannot
	 *         make an estimate
	 * 
	 */

	public int getUnavailableSeconds()
	{
		return permanent ? -1 : seconds;
	}
}
