package corinna.persistence;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;


public class EntityManagerFacade implements EntityManager
{

	EntityManagerFactory emf;
	
	ThreadLocal<EntityManager> em;
	
	public EntityManagerFacade( EntityManagerFactory factory )
	{
		if (factory == null)
			throw new IllegalArgumentException("Entity manager factory can not be null");
		em = new ThreadLocal<EntityManager>();
		emf = factory;
	}

	/**
	 * Returns the entity manager for the current thread.
	 * 
	 * @return
	 */
	protected EntityManager getEntityManager()
	{
		// create a new entity manager if necessary
		EntityManager em = this.em.get();
		if (em == null) this.em.set( em = emf.createEntityManager() );

		return em;
	}
	
	@Override
	public void clear()
	{
		getEntityManager().clear();
	}

	@Override
	public void close()
	{
		getEntityManager().close();
	}

	@Override
	public boolean contains( Object object )
	{
		return getEntityManager().contains(object);
	}

	@Override
	public Query createNamedQuery( String name )
	{
		return getEntityManager().createNamedQuery(name);
	}

	@Override
	public <T> TypedQuery<T> createNamedQuery( String name, Class<T> type )
	{
		return getEntityManager().createNamedQuery(name, type);
	}

	@Override
	public Query createNativeQuery( String arg0 )
	{
		return getEntityManager().createNamedQuery(arg0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Query createNativeQuery( String arg0, Class arg1 )
	{
		return getEntityManager().createNativeQuery(arg0, arg1);
	}

	@Override
	public Query createNativeQuery( String arg0, String arg1 )
	{
		return getEntityManager().createNativeQuery(arg0, arg1);
	}

	@Override
	public Query createQuery( String arg0 )
	{
		return getEntityManager().createNamedQuery(arg0);
	}

	@Override
	public <T> TypedQuery<T> createQuery( CriteriaQuery<T> arg0 )
	{
		return getEntityManager().createQuery(arg0);
	}

	@Override
	public <T> TypedQuery<T> createQuery( String arg0, Class<T> arg1 )
	{
		return getEntityManager().createNamedQuery(arg0, arg1);
	}

	@Override
	public void detach( Object arg0 )
	{
		getEntityManager().detach(arg0);
	}

	@Override
	public <T> T find( Class<T> arg0, Object arg1 )
	{
		return getEntityManager().find(arg0, arg1);
	}

	@Override
	public <T> T find( Class<T> arg0, Object arg1, Map<String, Object> arg2 )
	{
		return getEntityManager().find(arg0, arg1, arg2);
	}

	@Override
	public <T> T find( Class<T> arg0, Object arg1, LockModeType arg2 )
	{
		return getEntityManager().find(arg0, arg1, arg2);
	}

	@Override
	public <T> T find( Class<T> arg0, Object arg1, LockModeType arg2, Map<String, Object> arg3 )
	{
		return getEntityManager().find(arg0, arg1, arg2, arg3);
	}

	@Override
	public void flush()
	{
		getEntityManager().flush();
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder()
	{
		return getEntityManager().getCriteriaBuilder();
	}

	@Override
	public Object getDelegate()
	{
		return getEntityManager().getDelegate();
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory()
	{
		return getEntityManager().getEntityManagerFactory();
	}

	@Override
	public FlushModeType getFlushMode()
	{
		return getEntityManager().getFlushMode();
	}

	@Override
	public LockModeType getLockMode( Object arg0 )
	{
		return getEntityManager().getLockMode(arg0);
	}

	@Override
	public Metamodel getMetamodel()
	{
		return getEntityManager().getMetamodel();
	}

	@Override
	public Map<String, Object> getProperties()
	{
		return getEntityManager().getProperties();
	}

	@Override
	public <T> T getReference( Class<T> arg0, Object arg1 )
	{
		return getEntityManager().getReference(arg0, arg1);
	}

	@Override
	public EntityTransaction getTransaction()
	{
		return getEntityManager().getTransaction();
	}

	@Override
	public boolean isOpen()
	{
		return getEntityManager().isOpen();
	}

	@Override
	public void joinTransaction()
	{
		getEntityManager().joinTransaction();
	}

	@Override
	public void lock( Object arg0, LockModeType arg1 )
	{
		getEntityManager().lock(arg0, arg1);
	}

	@Override
	public void lock( Object arg0, LockModeType arg1, Map<String, Object> arg2 )
	{
		getEntityManager().lock(arg0, arg1, arg2);
	}

	@Override
	public <T> T merge( T arg0 )
	{
		return getEntityManager().merge(arg0);
	}

	@Override
	public void persist( Object arg0 )
	{
		getEntityManager().persist(arg0);
	}

	@Override
	public void refresh( Object arg0 )
	{
		getEntityManager().refresh(arg0);
	}

	@Override
	public void refresh( Object arg0, Map<String, Object> arg1 )
	{
		getEntityManager().refresh(arg0, arg1);
	}

	@Override
	public void refresh( Object arg0, LockModeType arg1 )
	{
		getEntityManager().refresh(arg0, arg1);
	}

	@Override
	public void refresh( Object arg0, LockModeType arg1, Map<String, Object> arg2 )
	{
		getEntityManager().refresh(arg0, arg1, arg2);
	}

	@Override
	public void remove( Object arg0 )
	{
		getEntityManager().remove(arg0);
	}

	@Override
	public void setFlushMode( FlushModeType arg0 )
	{
		getEntityManager().setFlushMode(arg0);
	}

	@Override
	public void setProperty( String arg0, Object arg1 )
	{
		getEntityManager().setProperty(arg0, arg1);
	}

	@Override
	public <T> T unwrap( Class<T> arg0 )
	{
		return getEntityManager().unwrap(arg0);
	}

	/**
	 * Find the <code>EntityManager</code> of the current thread and close it.
	 */
	public void release()
	{
		EntityManager em = this.em.get();
		this.em.set(null);
		em.close();
	}

}
