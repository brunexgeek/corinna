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

package javax.bindlet;


import java.io.IOException;

import corinna.exception.BindletException;
import corinna.exception.UnavailableException;
import corinna.util.IComponentInformation;


/**
 * Defines methods that all bindlets must implement.
 * 
 * <p>
 * A bindlet is a small module that runs within a service. Bindlets receive and respond to requests
 * from clients and can be implemented to support any TCP/IP or UDP/IP protocol.
 * 
 * <p>
 * To implement this interface, you can write a generic bindlet that extends
 * <code>corinna.service.bindlet.Bindlet</code> or use one of pre-implemented protocol specific
 * bindlets, like <code>corinna.service.bindlet.rest.RestBindlet</code>.
 * 
 * <p>
 * This interface defines methods to initialize a bindletlet and to process requests. The bindlet is
 * constructed through a non-parametrized constructor and then initialized with the
 * <code>init</code> method. All requests received from a network listner are handled by
 * <code>process</code> method. When the bindlet is taken out of service, it's destroyed with the
 * <code>destroy</code> method. The bindlet can be stateless, in this case your instance will be
 * retained for future use.
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 * 
 * @see Bindlet
 * @see corinna.service.bindlet.http.HttpBindlet
 */
public interface IBindlet<R, P>
{

	/**
	 * Argument types for default constructor of a bindlet. This constructor is used by a
	 * {@link IDomainParser} implementation.
	 */
	// XXX public static final Class<?>[] CONSTRUCTOR_ARGS = { IBindletConfig.class };

	/**
	 * Called by the bindlet container to indicate that the bindlet is being placed into service.
	 * 
	 * <p>
	 * The bindlet container calls the <code>init</code> method exactly once after instantiating the
	 * bindlet. The <code>init</code> method must complete successfully before the servlet can
	 * receive any requests.
	 * </p>
	 * 
	 * <p>
	 * The bindlet container cannot place the bindlet into service if the <code>init</code> method
	 * <ol>
	 * <li>Throws a <code>BindletException</code>
	 * <li>Does not return within a time period defined by the container
	 * </ol>
	 * </p>
	 * 
     * @param config
     *            the <code>ServletConfig</code> object that contains
     *            configuration information for this servlet
	 * @exception BindletException
	 *                if an exception has occurred that interferes with the bindlet's normal
	 *                operation
	 * 
	 * @see UnavailableException
	 * @see #getConfiguration
	 */
	public void init( IBindletConfig config ) throws BindletException;
	
    /**
     * A convenience method which can be overridden so that there's no need to
     * call <code>super.init(config)</code>.
     * <p>
     * Instead of overriding {@link #init(ServletConfig)}, simply override this
     * method and it will be called by
     * <code>GenericServlet.init(ServletConfig config)</code>. The
     * <code>ServletConfig</code> object can still be retrieved via
     * {@link #getServletConfig}.
     * 
     * @exception ServletException
     *                if an exception occurs that interrupts the servlet's
     *                normal operation
     */
	public void init() throws BindletException;

	/**
	 * 
	 * Returns a {@link IBindletConfig} object, which contains initialization and startup parameters
	 * for this bindlet. The <code>IBindletConfig</code> object returned is the one passed to the
	 * bindlet default constructor.
	 * 
	 * <p>
	 * Implementations of this interface are responsible for storing the <code>IBindletConfig</code>
	 * object so that this method can return it. The {@link Bindlet} class, which implements this
	 * interface, already does this.
	 * </p>
	 * 
	 * @return the <code>IBindletConfig</code> object that initializes this bindlet
	 * @see #CONSTRUCTOR_ARGS
	 */
	public IBindletConfig getBindletConfig();

	/**
	 * Called by the bindlet container to allow the bindlet to respond to a request.
	 * 
	 * <p>
	 * This method is only called after the bindlet's <code>init()</code> method has completed
	 * successfully.
	 * </p>
	 * 
	 * <p>
	 * The status code of the response always should be set for a servlet that throws or sends an
	 * error.
	 * 
	 * <p>
	 * Bindlet typically run inside multithreaded bindlet containers that can handle multiple
	 * requests concurrently. Developers must be aware to synchronize access to any shared resources
	 * such as files, network connections, and as well as the bindlet's class and instance
	 * variables. More information on multithreaded programming in Java is available in <a
	 * href="http://java.sun.com/Series/Tutorial/java/threads/multithreaded.html"> the Java tutorial
	 * on multi-threaded programming</a>.
	 * </p>
	 * 
	 * @param req
	 *            the <code>ServletRequest</code> object that contains the client's request
	 * 
	 * @param res
	 *            the <code>ServletResponse</code> object that contains the servlet's response
	 * @return
	 * 
	 * @exception BindletException
	 *                if an exception occurs that interferes with the servlet's normal operation
	 * 
	 * @exception IOException
	 *                if an input or output exception occurs
	 */
	public void process( R req, P res ) throws BindletException, IOException;

	/**
	 * Returns <code>true</code> if the bindlet accepts to process this request.
	 *  
	 * @param req
	 * @return
	 * @throws BindletException
	 */
	//public boolean accept( R req ) throws BindletException;
	
	/**
	 * Returns a instance of {@link IComponentInformation} containing the information about the bindlet, such as implementor and version.
	 * 
	 * @return a <code>IComponentInformation</code> containing bindlet information
	 */
	public IComponentInformation getBindletInfo();

	/**
	 * Called by the bindlet container to indicate that the bindlet is being taken out
	 * of service. This method is only called once all threads within the bindlet's
	 * <code>process</code> method have exited or after a timeout period has passed. After the
	 * bindlet container calls this method, it will not call the <code>process</code> method again
	 * on this bindlet.
	 * 
	 * <p>
	 * This method gives the bindlet an opportunity to clean up any resources that are being held
	 * (for example, memory, file handles, threads) and make sure that any persistent state is
	 * synchronized with the bindlet's current state in memory.</p>
	 */

	public void destroy();

	/**
	 * Returns the bindlet name as specified in service deployment descriptor.
	 * 
	 * @return a <code>String</code> containing the bindlet name
	 */
	public String getBindletName();

}
