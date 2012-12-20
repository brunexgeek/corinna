package corinna.http.bindlet;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.bindlet.BindletModel;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.IComponentInformation;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpBindlet;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.io.BindletOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.http.jsp.IServerPageRender;
import corinna.http.jsp.JSPGenerator;
import corinna.rpc.ReflectionUtil;
import corinna.thread.ObjectLocker;


@BindletModel(Model.STATELESS)
public class ServerPageBindlet extends HttpBindlet
{

	private static Logger serverLog = LoggerFactory.getLogger(ServerPageBindlet.class);
	
	private static final long serialVersionUID = 773622322084886911L;

	private static final String CONFIG_DOCUMENT_ROOT = "documentRoot";

	private static final String HTTP_FIELD_DATE = "Date";

	private static final String HTTP_FIELD_KEEP_ALIVE = "Keep-Alive";

	private static final String HTTP_FIELD_CONNECTION = "Connection";

	private static final String HTTP_FIELD_SERVER = "Server";
	
	private static Map<String, PrecompiledClass>compiledPages = new HashMap<String, PrecompiledClass>();

	private static ObjectLocker lock = new ObjectLocker();
	
	/**
	 * Tempo máximo, em segundos, na qual um cliente deve manter a conexão aberta sem atividade.
	 * Esse valor é utilizado no campo de cabeçalho HTTP {@link HTTP_FIELD_KEEP_ALIVE}.
	 */
	private static final int HTTP_KEEP_ALIVE_TIMEOUT = 10;

	/**
	 * Número máximo de requisições que um cliente pode enviar através de uma mesma conexão. Esse
	 * valor é utilizado no campo de cabeçalho HTTP {@link HTTP_FIELD_KEEP_ALIVE}.
	 */
	private static final int HTTP_KEEP_ALIVE_MAX = 10;

	public ServerPageBindlet() throws BindletException
	{
		super();
	}

	public String getDocumentRoot()
	{
		return getInitParameter(CONFIG_DOCUMENT_ROOT);
	}

	@Override
	public IComponentInformation getBindletInfo()
	{
		return null;
	}

	@Override
	public void doGet( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		if (getDocumentRoot() == null)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
			return;
		}

		// limpa o nome para evitar construções que permitam acessar arquivos do sistema que não
		// estejam no diretório permitido
		String fileName = request.getResourcePath();
		fileName = fileName.replace("..", "");

		// localiza o arquivo indicando
		File file = findFile(fileName);
		if (file == null || file.length() == 0)
		{
			response.sendError(HttpStatus.NOT_FOUND);
			return;
		}
		// obtém a classe responsável por renderizar a página
		Class<?> classRef = findRender(file);
		if (classRef == null)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
			return;
		}
		//serverLog.info("Requesting page '" + file.getAbsolutePath() + "'");

		BindletOutputStream out = null;
				
		try
		{
			// define a data na qual o recurso foi fornecido e a data do recurso em si
			response.setDateHeader(HTTP_FIELD_DATE, System.currentTimeMillis());
			// response.setDateHeader( HTTP_FIELD_LAST_MODIFIED, file.lastModified() );
			// define o tipo de conteúdo
			response.setContentType("text/html");
			// define as configurações relativas ao uso de conexões com o servidor
			response.setHeader(HTTP_FIELD_CONNECTION, "keep-alive");
			response.setHeader(HTTP_FIELD_KEEP_ALIVE, "timeout=" + HTTP_KEEP_ALIVE_TIMEOUT
				+ ", max=" + HTTP_KEEP_ALIVE_MAX);
			// define o nome do servidor (inibe informações do AS)
			response.setHeader(HTTP_FIELD_SERVER, "CPqD VaaS");

			// diretiva HTTP para informar o tamanho do conteúdo
			//response.setContentLength(outputLength);

			// prepara para enviar os dados
			out = response.getOutputStream();
			PrintWriter writer = new PrintWriter(out);
			// processa a página JSP
			IServerPageRender render = (IServerPageRender) classRef.newInstance();
			render.request = request;
			render.render(writer);
			writer.flush();
			out.flush();
		} catch (Exception e)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
			serverLog.error("Internal JSP error", e);
		} finally
		{
			if (out != null) out.close();
			out = null;
		}
	}

	/**
	 * Procura por um arquivo no diretório e retorna sua instância <code>File</code> associada.
	 * 
	 * @param fileName
	 *            Nome do arquivo.
	 * @return Instância <code>File</code> associada ao arquivo encontrado. Caso o arquivo não
	 *         exista ou seja um diretório, retorna <code>null</code>.
	 */
	private File findFile( String fileName )
	{
		File file = new File(getDocumentRoot() + File.separatorChar + fileName);
		if (!file.exists() || file.isDirectory()) return null;
		return file;
	}

	private Class<?> findRender( File file ) throws BindletException, IOException
	{
		PrecompiledClass entry = null;
		Class<?> classRef = null;

		if (file == null || !file.canRead()) return null;

		String className = file.getAbsolutePath().replaceAll("[^a-zA-Z0-9]", "_");
		className = "jsp_" + className;
		long date = file.lastModified();
		
		// check if we have some class with generated name
		lock.readLock();
		try
		{
			entry = compiledPages.get(className);
			if (entry != null && entry.lastModified() == date) return entry.getCompiledClass();
		} catch (Exception e)
		{
			// nothing to do
		} finally 
		{
			lock.readUnlock();
		}
		
		// load and process the JSP file
		try
		{
			Reader reader = new FileReader(file);
			classRef = JSPGenerator.compile(null, className + date, reader);
			reader.close();
		} catch (FileNotFoundException e)
		{
			throw new IOException("Error reading JSP page", e);
		} catch (Exception e)
		{
			serverLog.error("Error processing JSP page", e);
			throw new BindletException("Error processing JSP page", e);
		}
		// add the new JSP class to the list and return
		lock.writeLock();
		try
		{
			entry = new PrecompiledClass(classRef, date);
			compiledPages.put(className, entry);
			return classRef;
		} finally
		{
			lock.writeUnlock();
		}
	}
	
	private static class PrecompiledClass
	{
		
		private Class<?> classRef = null;
		
		private long modifiedDate = 0;
		
		public PrecompiledClass( Class<?> classRef, long date )
		{
			this.classRef = classRef;
			this.modifiedDate = date;
		}
		
		public long lastModified()
		{
			return modifiedDate;
		}
		
		public Class<?> getCompiledClass()
		{
			return classRef;
		}
		
	}
	
	@Override
	public Model getBindletModel()
	{
		try
		{
			BindletModel model = (BindletModel) ReflectionUtil.getAnnotation(this.getClass(), BindletModel.class);
			if (model == null) return Model.STATEFULL;
			return model.value();
		} catch (Exception e)
		{
			return Model.STATEFULL;
		}
	}
	
}
