package javax.bindlet;


/**
 * Define os métodos necessários para identificar um componente.
 *  
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public interface IComponentInformation 
{

	
	/**
	 * Retorna o nome da funcionalidade.
	 * 
	 * @return
	 */
	String getComponentName();
	
	
	/**
	 * Retorna a versão da funcionalidade.
	 * @return
	 */
	String getComponentVersion();
	
	
	/**
	 * Retorna o nome do implementador da funcionalidade.
	 * 
	 * @return
	 */
	String getComponentImplementor();
	
	
}