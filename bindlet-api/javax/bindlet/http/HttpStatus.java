/*
 * Copyright 2011-2012 Bruno Ribeiro
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

package javax.bindlet.http;


public enum HttpStatus
{


	/*
	 * Server status codes; see RFC 2068.
	 */

	/**
	 * Status code (100) indicating the client can continue.
	 */

	CONTINUE(100, "Continue"),

	/**
	 * Status code (101) indicating the server is switching protocols according to Upgrade header.
	 */

	SWITCHING_PROTOCOLS(101, "Switching Protocols"),

	/**
	 * Status code (200) indicating the request succeeded normally.
	 */

	OK(200, "OK"),

	/**
	 * Status code (201) indicating the request succeeded and created a new resource on the server.
	 */

	CREATED(201, "Created"),

	/**
	 * Status code (202) indicating that a request was accepted for processing, but was not
	 * completed.
	 */

	ACCEPTED(202, "Accepted"),

	/**
	 * Status code (203) indicating that the meta information presented by the client did not
	 * originate from the server.
	 */

	NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),

	/**
	 * Status code (204) indicating that the request succeeded but that there was no new information
	 * to return.
	 */

	NO_CONTENT(204, "No Content"),

	/**
	 * Status code (205) indicating that the agent <em>SHOULD</em> reset the document view which
	 * caused the request to be sent.
	 */

	RESET_CONTENT(205, "Reset Content"),

	/**
	 * Status code (206) indicating that the server has fulfilled the partial GET request for the
	 * resource.
	 */

	PARTIAL_CONTENT(206, "Partial Content"),

	/**
	 * Status code (300) indicating that the requested resource corresponds to any one of a set of
	 * representations, each with its own specific location.
	 */

	MULTIPLE_CHOICES(300, "Multiple Choices"),

	/**
	 * Status code (301) indicating that the resource has permanently moved to a new location, and
	 * that future references should use a new URI with their requests.
	 */

	MOVED_PERMANENTLY(301, "Moved Permanently"),

	/**
	 * Status code (302) indicating that the resource has temporarily moved to another location, but
	 * that future references should still use the original URI to access the resource.
	 * 
	 * This definition is being retained for backwards compatibility. FOUND is now the preferred
	 * definition.
	 */

	MOVED_TEMPORARILY(302, "Moved Temporarily"),

	/**
	 * Status code (302) indicating that the resource reside temporarily under a different URI.
	 * Since the redirection might be altered on occasion, the client should continue to use the
	 * Request-URI for future requests.(HTTP/1.1) To represent the status code (302, ""), it is
	 * recommended to use this variable.
	 */

	FOUND(302, "Found"),

	/**
	 * Status code (303) indicating that the response to the request can be found under a different
	 * URI.
	 */

	SEE_OTHER(303, "See Other"),

	/**
	 * Status code (304) indicating that a conditional GET operation found that the resource was
	 * available and not modified.
	 */

	NOT_MODIFIED(304, "Not Modified"),

	/**
	 * Status code (305) indicating that the requested resource <em>MUST</em> be accessed through
	 * the proxy given by the <code><em>Location</em></code> field.
	 */

	USE_PROXY(305, "Use Proxy"),

	/**
	 * Status code (307) indicating that the requested resource resides temporarily under a
	 * different URI. The temporary URI <em>SHOULD</em> be given by the
	 * <code><em>Location</em></code> field in the response.
	 */

	TEMPORARY_REDIRECT(307, "Temporary Redirect"),

	/**
	 * Status code (400) indicating the request sent by the client was syntactically incorrect.
	 */

	BAD_REQUEST(400, "Bad Request"),

	/**
	 * Status code (401) indicating that the request requires HTTP authentication.
	 */

	UNAUTHORIZED(401, "Unauthorized"),

	/**
	 * Status code (402) reserved for future use.
	 */

	PAYMENT_REQUIRED(402, "Payment Required"),

	/**
	 * Status code (403) indicating the server understood the request but refused to fulfill it.
	 */

	FORBIDDEN(403, "Forbidden"),

	/**
	 * Status code (404) indicating that the requested resource is not available.
	 */

	NOT_FOUND(404, "Not Found"),

	/**
	 * Status code (405) indicating that the method specified in the
	 * <code><em>Request-Line</em></code> is not allowed for the resource identified by the
	 * <code><em>Request-URI</em></code>.
	 */

	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

	/**
	 * Status code (406) indicating that the resource identified by the request is only capable of
	 * generating response entities which have content characteristics not acceptable according to
	 * the accept headers sent in the request.
	 */

	NOT_ACCEPTABLE(406, "Not Acceptable"),

	/**
	 * Status code (407) indicating that the client <em>MUST</em> first authenticate itself with the
	 * proxy.
	 */

	PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),

	/**
	 * Status code (408) indicating that the client did not produce a request within the time that
	 * the server was prepared to wait.
	 */

	REQUEST_TIMEOUT(408, "Request Timeout"),

	/**
	 * Status code (409) indicating that the request could not be completed due to a conflict with
	 * the current state of the resource.
	 */

	CONFLICT(409, "Conflict"),

	/**
	 * Status code (410) indicating that the resource is no longer available at the server and no
	 * forwarding address is known. This condition <em>SHOULD</em> be considered permanent.
	 */

	GONE(410, "Gone"),

	/**
	 * Status code (411) indicating that the request cannot be handled without a defined
	 * <code><em>Content-Length</em></code>.
	 */

	LENGTH_REQUIRED(411, "Length Required"),

	/**
	 * Status code (412) indicating that the precondition given in one or more of the request-header
	 * fields evaluated to false when it was tested on the server.
	 */

	PRECONDITION_FAILED(412, "Precondition Failed"),

	/**
	 * Status code (413) indicating that the server is refusing to process the request because the
	 * request entity is larger than the server is willing or able to process.
	 */

	REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),

	/**
	 * Status code (414) indicating that the server is refusing to service the request because the
	 * <code><em>Request-URI</em></code> is longer than the server is willing to interpret.
	 */

	REQUEST_URI_TOO_LONG(414, "Rquest URI Too Long"),

	/**
	 * Status code (415) indicating that the server is refusing to service the request because the
	 * entity of the request is in a format not supported by the requested resource for the
	 * requested method.
	 */

	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

	/**
	 * Status code (416) indicating that the server cannot serve the requested byte range.
	 */

	REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),

	/**
	 * Status code (417) indicating that the server could not meet the expectation given in the
	 * Expect request header.
	 */

	EXPECTATION_FAILED(417, "Expectation Failed"),

	/**
	 * Status code (500) indicating an error inside the HTTP server which prevented it from
	 * fulfilling the request.
	 */

	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

	/**
	 * Status code (501) indicating the HTTP server does not support the functionality needed to
	 * fulfill the request.
	 */

	NOT_IMPLEMENTED(501, "Not Implemented"),

	/**
	 * Status code (502) indicating that the HTTP server received an invalid response from a server
	 * it consulted when acting as a proxy or gateway.
	 */

	BAD_GATEWAY(502, "Bad Gateway"),

	/**
	 * Status code (503) indicating that the HTTP server is temporarily overloaded, and unable to
	 * handle the request.
	 */

	SERVICE_UNAVAILABLE(503, "Service Unavailable"),

	/**
	 * Status code (504) indicating that the server did not receive a timely response from the
	 * upstream server while acting as a gateway or proxy.
	 */

	GATEWAY_TIMEOUT(504, "Gateway Timeout"),

	/**
	 * Status code (505) indicating that the server does not support or refuses to support the HTTP
	 * protocol version that was used in the request message.
	 */

	HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");
	
	private int code;
	
	private String description;
	
	private HttpStatus( int code, String description )
	{
		this.code = code;
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public int getCode()
	{
		return code;
	}
	
	@Override
	public String toString()
	{
		return code + " " + description;
	}

	public static HttpStatus valueOf( int code )
	{
		for ( HttpStatus entry : values() )
			if (entry.getCode() == code) return entry;
		return INTERNAL_SERVER_ERROR;
	}
	
}
