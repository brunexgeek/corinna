package corinna.http.jsp;


import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import corinna.exception.JSPParserException;


// not the fastest parser on earth
public class JSPParser
{

	public JSPParser( Reader input )
	{
		initReader(input, 4);
	}

	public List<JSPElement> elements = new ArrayList<JSPElement>();

	public void parse() throws IOException, JSPParserException
	{
		try
		{
			parseJstp();
		} catch (IOExceptionWrapper e)
		{
			throw (IOException) e.getCause();
		} catch (JSPExceptionWrapper e)
		{
			throw (JSPParserException) e.getCause();
		}
	}

	void parseJstp()
	{
		while (!eof())
		{
			parseElement();
			elements.add(element);
		}
	}

	JSPElement element = null;

	void parseElement()
	{
		if (lookahead("<%--"))
			parseComment();
		else
			if (lookahead("<%@"))
				parseDirective();
			else
				if (lookahead("<%!"))
					parseDeclaration();
				else
					if (lookahead("<%="))
						parseExpression();
					else
						if (lookahead("<%"))
							parseScriptlet();
						else
							parseText();
	}

	void newElement( int type )
	{
		JSPElement newElement = new JSPElement(type, row, col);
		if (element != null)
		{
			element.next = newElement;
			newElement.prev = element;
		}
		element = newElement;
	}

	void parseComment()
	{
		newElement(JSPElement.TYPE_COMMENT);

		consume("<%--");
		while (!lookahead("--%>"))
		{
			element.content.append(next());
		}
		consume("--%>");
	}

	void parseDirective()
	{
		newElement(JSPElement.TYPE_DIRECTIVE);

		consume("<%@");

		blank();
		element.directiveName = name();

		while (true)// attributes. not exactly as spec, but fine for our purpose
		{
			int B = blank();
			String attrName = name();
			if (attrName.length() == 0) // no more attributes
				break;
			else
				// a new attribute. blank is required in front of it
				if (B == 0) parserError("whitespace expected before attribute " + attrName);

			blank();
			if (next() != '=') parserError("missing '='");
			blank();

			String attrValue = string();

			element.addAttribute(attrName, attrValue);
		}

		if (lookahead("%>"))
			consume("%>");
		else
			parserError("parser error in directive '" + element.directiveName + "'");
	}

	void parseDeclaration()
	{
		newElement(JSPElement.TYPE_DECLARATION);
		consume("<%!");
		parseRestOfDeclExprScr();
	}

	void parseExpression()
	{
		newElement(JSPElement.TYPE_EXPRESSION);
		consume("<%=");
		parseRestOfDeclExprScr();
	}

	void parseScriptlet()
	{
		newElement(JSPElement.TYPE_SCRIPTLET);
		consume("<%");
		parseRestOfDeclExprScr();
	}

	void parseRestOfDeclExprScr()
	{
		while (!lookahead("%>"))
		{
			if (lookahead("%\\>")) // %\> => %>
			{
				consume("%\\>");
				element.content.append("%>");
			}
			else
				element.content.append(next());
		}
		consume("%>");
	}

	void parseText()
	{
		newElement(JSPElement.TYPE_TEXT);
		while (!eof() && !lookahead("<%"))
		{
			// each line is a seperate text element
			if (lookahead("\r\n"))
			{
				element.content.append(next()).append(next());
				break;
			}
			else
				if (lookahead("\r") || lookahead("\n"))
				{
					element.content.append(next());
					break;
				}
				else
					if (lookahead("<\\%")) // <\% => <%
					{
						consume("<\\%");
						element.content.append("<%");
					}
					else
						element.content.append(next());
		}
	}

	int blank()
	{
		for (int i = 0;; i++)
		{
			if (Character.isSpaceChar(peek()))
				next();
			else
				return i;
		}
	}

	String name()
	{
		StringBuffer buf = new StringBuffer();
		while (Character.isLetter(peek()) || '-' == peek())
		{
			buf.append(next());
		}
		return buf.toString();
	}

	String string()
	{
		String delim = null;
		if (lookahead("\""))
			delim = "\"";
		else
			if (lookahead("\'"))
				delim = "\'";
			else
				parserError("\" or \' expected at start of attribute value");

		StringBuffer buf = new StringBuffer();

		consume(delim);
		while (!lookahead(delim))
		{
			buf.append(next());
		}
		consume(delim);

		return buf.toString();
	}

	// == exception wrapper =================================
	// use unchecked exception wrappers internally
	// unwrap to check exception at published interface
	static class IOExceptionWrapper extends RuntimeException
	{

		private static final long serialVersionUID = 1248152260466278505L;

		public IOExceptionWrapper( IOException e )
		{
			super(e);
		}
	}

	static class JSPExceptionWrapper extends RuntimeException
	{

		private static final long serialVersionUID = -3620058579854268684L;

		public JSPExceptionWrapper( JSPParserException e )
		{
			super(e);
		}
	}

	void parserError( String message ) throws JSPExceptionWrapper
	{
		throw new JSPExceptionWrapper(new JSPParserException(message, row, col));
	}

	// == reader =================================

	PushbackReader pbr;

	char[] buf;

	void initReader( Reader reader, int lookahead )
	{
		this.pbr = new PushbackReader(reader, lookahead);
		this.buf = new char[lookahead];
	}

	boolean eof()
	{
		try
		{
			int c = pbr.read();
			if (c == -1) return true;

			pbr.unread(c);
			return false;
		} catch (IOException e)
		{
			throw new IOExceptionWrapper(e);
		}
	}

	boolean _r = false;

	int row = 1;

	int col = 1;

	void count( int x )
	{
		for (int i = 0; i < x; i++)
		{
			char c = buf[i];
			boolean r = (c == '\r');
			if (c == '\n' || (_r && r))
			{
				row = row + 1;
				col = 1;
			}
			else
				if (_r && !r)
				{
					row = row + 1;
					col = 2;
				}
				else
				{
					col = col + 1;
				}
			_r = r;
		}
	}

	char next()
	{
		try
		{
			int n = pbr.read(buf, 0, 1);
			if (n == -1) unexpectedEOF();

			count(1);
			return buf[0];
		} catch (IOException e)
		{
			throw new IOExceptionWrapper(e);
		}
	}

	char peek()
	{
		try
		{
			int n = pbr.read(buf, 0, 1);
			if (n == -1) unexpectedEOF();
			pbr.unread(buf, 0, 1);

			return buf[0];
		} catch (IOException e)
		{
			throw new IOExceptionWrapper(e);
		}
	}

	void unexpectedEOF() throws JSPExceptionWrapper
	{
		if (element != null && element.type != JSPElement.TYPE_TEXT)
			parserError("unclosed " + element.getTypeStartToken() + " at " + element.row + ":"
				+ element.col);
		else
			parserError("unexpected EOF");
	}

	boolean lookahead( String s )
	{
		try
		{
			int n = pbr.read(buf, 0, s.length());
			if (n == -1) return false;
			pbr.unread(buf, 0, n);

			if (n != s.length()) return false;

			for (int i = 0; i < n; i++)
				if (buf[i] != s.charAt(i)) return false;

			return true;
		} catch (IOException e)
		{
			throw new IOExceptionWrapper(e);
		}
	}

	void consume( String s )
	{
		try
		{
			int n = pbr.read(buf, 0, s.length());

			if (n != s.length()) throw new Error("assertion fails");

			count(n);

			for (int i = 0; i < n; i++)
				if (buf[i] != s.charAt(i)) throw new Error("assertion fails");
		} catch (IOException e)
		{
			throw new IOExceptionWrapper(e);
		}
	}

}
