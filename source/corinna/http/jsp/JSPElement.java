package corinna.http.jsp;


import java.util.HashMap;
import java.util.Map;

// TODO: create child classes to implement the various types of elements
public class JSPElement
{
    public static final int TYPE_TEXT        = 0;  // blah
    public static final int TYPE_EXPRESSION  = 1;  // <%= expr %>

    public static final int TYPE_COMMENT     = 11;  // <%-- blah --%>
    public static final int TYPE_DIRECTIVE   = 12;  // <%@ name attr='value' ... %>
    public static final int TYPE_DECLARATION = 13;  // <%! decl %>
    public static final int TYPE_SCRIPTLET   = 14;  // <%  stmt %>
    
    public int type;
    public StringBuffer content;
    //follow 2 only make sense for directive type
    public String directiveName;
    public Map<String,String> directiveAttributes;
    
    public int row;
    public int col;

    public JSPElement(int type, int row, int col)
    {
        this.type = type;
        if(type==TYPE_DIRECTIVE)
            this.directiveAttributes = new HashMap<String,String>();
        else
            this.content = new StringBuffer();
        
        this.row = row;
        this.col = col;
    }
    
    public void addAttribute( String name, String value )
    {
    	directiveAttributes.put(name, value);
    }
    
    public String getTypeStartToken()
    {
        switch(type)
        {
            case TYPE_TEXT : return "";
            case TYPE_COMMENT : return "<%--";
            case TYPE_DIRECTIVE : return "<%@";
            case TYPE_DECLARATION : return "<%!";
            case TYPE_EXPRESSION : return "<%=";
            case TYPE_SCRIPTLET : return "<%";
            default : throw new Error("assertion fails");
        }
    }
    
    public JSPElement prev = null;
    public JSPElement next = null;

	public String getAttribute( String name )
	{
		if (name == null) return null;
		return directiveAttributes.get(name);
	}
}