package corinna.http.bindlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.bindlet.IComponentInformation;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpBindlet;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.io.BindletOutputStream;


public class ServerPagesBindlet extends HttpBindlet
{

	private static final long serialVersionUID = 773622322084886911L;
	
	private static final String CONFIG_DOCUMENT_ROOT = "documentRoot";
	
	private static final String HTTP_FIELD_DATE = "Date";
	
	private static final String HTTP_FIELD_KEEP_ALIVE = "Keep-Alive";

	private static final String HTTP_FIELD_CONNECTION = "Connection";
	
	private static final String HTTP_FIELD_SERVER = "Server";
	
	/**
	 * Tempo máximo, em segundos, na qual um cliente deve manter a conexão aberta sem atividade.
	 * Esse valor é utilizado no campo de cabeçalho HTTP {@link HTTP_FIELD_KEEP_ALIVE}.
	 */
	private static final int HTTP_KEEP_ALIVE_TIMEOUT = 10;

	/**
	 * Número máximo de requisições que um cliente pode enviar através de uma mesma conexão.
	 * Esse valor é utilizado no campo de cabeçalho HTTP {@link HTTP_FIELD_KEEP_ALIVE}.
	 */
	private static final int HTTP_KEEP_ALIVE_MAX = 10;

	public ServerPagesBindlet() throws BindletException
	{
		super();
	}

	public String getDocumentRoot()
	{
		return getInitParameter(CONFIG_DOCUMENT_ROOT);		
	}
	
	@Override
	public boolean isRestricted()
	{
		return false;
	}

	@Override
	public IComponentInformation getBindletInfo()
	{
		return null;
	}

	@Override
	public void doGet( IHttpBindletRequest request, IHttpBindletResponse response ) throws BindletException, IOException
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
		
		//serverLog.info("Start download of file '" + file.getAbsolutePath() + "'");
		
		BindletOutputStream out = null;
		InputStream in = null;
		
		try
		{
			in = new FileInputStream(file);
			int outputLength = (int)file.length();

			// define a data na qual o recurso foi fornecido e a data do recurso em si
			response.setDateHeader( HTTP_FIELD_DATE, System.currentTimeMillis() );
			//response.setDateHeader( HTTP_FIELD_LAST_MODIFIED, file.lastModified() );
			// define o tipo de conteúdo
			response.setContentType( "text/html" );
			// define as configurações relativas ao uso de conexões com o servidor
			response.setHeader(HTTP_FIELD_CONNECTION, "keep-alive");
			response.setHeader(HTTP_FIELD_KEEP_ALIVE, "timeout=" + HTTP_KEEP_ALIVE_TIMEOUT + 
				", max=" + HTTP_KEEP_ALIVE_MAX);
			// define o nome do servidor (inibe informações do AS)
			response.setHeader(HTTP_FIELD_SERVER, "CPqD VaaS");

			// diretiva HTTP para informar o tamanho do conteúdo
			response.setContentLength( outputLength );
			
			// envia os dados do arquivo (completa ou parcialmente)
			out = response.getOutputStream();
			sendData( out, in, 0, outputLength );
		}
		catch (Exception e)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		finally
		{
			if (in != null) in.close();
			in = null;
			if (out != null) out.close();
			out = null;
		}
	}
	
	private long sendData( BindletOutputStream output, InputStream input, long start, long length ) 
	throws IOException
{
	boolean isLocalBuffer = false;
	long required, block, counter = 0;
	int readed = 0;
	byte buffer[] = new byte[1024];
	
	if (start < 0 || length < 0)
		throw new IllegalArgumentException("The start offset and length must be a positive value");
	if (start >= length)
		throw new IllegalArgumentException("The start offset must be less than length");
	
	// solicita um buffer para a cópia
	block = buffer.length;
	counter = length;
	
	try
	{
		// ignora os 'start' bytes iniciais do fluxo
		input.skip(start);
		
		while (readed < 0 || counter > 0)
		{
			required = (counter > block) ? block : counter;

			// efetua a leitura dos dados
			readed = input.read(buffer, 0, (int)required);
			if (readed < 0) break;
			counter -= readed;
			
			// envia um bloco de dados
		    output.write(buffer, 0, readed);
		   // serverLog.info("DOWNLOAD: Written %d bytes to client" + readed);
		    
		}
	} finally
	{
	}
	//serverLog.info("DOWNLOAD: Completed!");
	return counter;
}
	
	/**
	 * Procura por um arquivo no diretório e retorna sua instância <code>File</code> associada.
	 *  
	 * @param fileName Nome do arquivo.
	 * @return Instância <code>File</code> associada ao arquivo encontrado. Caso o arquivo não 
	 *     exista ou seja um diretório, retorna <code>null</code>.
	 */
	private File findFile( String fileName )
	{
		File file = new File( getDocumentRoot() + File.separatorChar + fileName );
		if ( !file.exists() || file.isDirectory() ) return null;
		return file;
	}
	
}
