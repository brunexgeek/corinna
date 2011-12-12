package corinna.util;

import javax.bindlet.ILogger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public final class ApacheLogger implements ILogger
{

	private static final String FQCN = ApacheLogger.class.getName();
	
	private Logger log = null;
	
	public ApacheLogger( Class<?> clazz )
	{
		log = Logger.getLogger(clazz);
	}
	
	public ILogger getLogger( Class<?> clazz )
	{
		return new ApacheLogger(clazz);
	}
	
	@Override
	public void debug( Throwable cause, String format, Object... values )
	{
		log.log(FQCN, Level.DEBUG, format, cause);
	}

	@Override
	public void debug( String format, Object... values )
	{
		debug((Throwable)null, format);
	}

	@Override
	public void error( Throwable cause, String format, Object... values )
	{
		log.log(FQCN, Level.ERROR, format, cause);
	}

	@Override
	public void error( String format, Object... values )
	{
		error((Throwable)null, format);
	}

	@Override
	public void info( String format, Object... values )
	{
		log.log(FQCN, Level.INFO, format, null);
	}

	@Override
	public void warn( Throwable cause, String format, Object... values )
	{
		log.log(FQCN, Level.WARN, format, cause);
	}

	@Override
	public void warn( String format, Object... values )
	{
		warn((Throwable)null, format);
	}

}
