package corinna.persistence;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.thread.ObjectLocker;


public class PersistenceManager
{

	private static final Logger log = LoggerFactory.getLogger(PersistenceManager.class);

	/**
	 * Instance of the facade <code>EntityManager</code>.
	 */
	private Map<String, EntityManagerFacade> facades;

	private ObjectLocker facadesLock;

	/**
	 * Lock used to protect the access to the {@link #instance} field.
	 */
	private static Boolean instanceLock = false;

	/**
	 * Reference to the singleton instance of this class.
	 */
	private static PersistenceManager instance = null;

	private PersistenceManager()
	{
		facades = new HashMap<String, EntityManagerFacade>();
		facadesLock = new ObjectLocker();
	}

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return
	 */
	public static PersistenceManager getInstance()
	{
		synchronized (instanceLock)
		{
			if (instance == null) instance = new PersistenceManager();
			return instance;
		}
	}

	/**
	 * Returns the entity manager facade for the given persistence unit.
	 * 
	 * @param name
	 * @return
	 */
	public EntityManager getEntityManager( String persistenceUnit )
	{
		if (persistenceUnit == null || persistenceUnit.isEmpty()) return null;

		// check whether there is an entity manager for given persistence unit
		facadesLock.readLock();
		EntityManagerFacade em = facades.get(persistenceUnit);
		facadesLock.readUnlock();
		if (em != null) return em;
		// create a new entity manager facade for the given persistence unit
		facadesLock.writeLock();
		try
		{
			// check again due synchronization (exiting readLock, entering writeLock)
			em = facades.get(persistenceUnit);
			if (em == null)
			{
				// create the entity manager factory and the facade
				EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
				em = new EntityManagerFacade(emf);
				facades.put(persistenceUnit, em);
			}
		} finally
		{
			facadesLock.writeUnlock();
		}

		return em;
	}

	/**
	 * Find the <code>EntityManager</code> of the current thread associated with each registred
	 * persistence unit and close it.
	 * 
	 */
	public void releaseEntityManager()
	{
		// get the entity manager instance
		facadesLock.readLock();
		try
		{
			for (EntityManagerFacade em : facades.values())
				if (em != null) em.release();
		} finally
		{
			facadesLock.readUnlock();
		}
	}

	/**
	 * Find the <code>EntityManager</code> of the current thread associated with the given
	 * persistence unit and close it.
	 * 
	 * @param persistenceUnit
	 *            Name of the persistence unit
	 */
	public void releaseEntityManager( String persistenceUnit )
	{
		// get the entity manager instance
		facadesLock.readLock();
		try
		{
			EntityManagerFacade em = facades.get(persistenceUnit);
			if (em != null) em.release();
		} finally
		{
			facadesLock.readUnlock();
		}
	}

	/**
	 * Find all field in the give object that have the {@link BeanIbject} annotation and set its
	 * value with the reference of the respective service bean.
	 * 
	 * @param object
	 *            Object that will receive the injection.
	 */
	public void inject( Object object )
	{
		if (object == null || object.getClass() == Object.class) return;

		// iterate in object fields to find anyone that has the BeanInject attribute
		Field fields[] = object.getClass().getDeclaredFields();
		for (Field current : fields)
		{
			PersistenceContext att = current.getAnnotation(PersistenceContext.class);
			if (att == null) continue;
			EntityManager em = getEntityManager(att.unitName());
			if (em == null)
				log.error("Entity manager for '{}' not found when injecting on '{}'",
					att.unitName(), object.getClass().getName());

			try
			{
				synchronized (current)
				{
					boolean state = current.isAccessible();
					if (!state) current.setAccessible(true);
					boolean result = trySetField(object, current, em);
					if (!result) trySetField(object, current, null);
					if (!state) current.setAccessible(false);
				}
			} catch (Exception e)
			{
				log.error("Fail to inject entity manager for '{}' in '{}'", att.unitName(), object
					.getClass().getName());
			}
		}
	}

	/**
	 * Try set the value of the given class field.
	 * 
	 * @param object
	 *            Object whose field will set.
	 * @param field
	 *            Field will set.
	 * @param value
	 *            New value of the field.
	 * @return <code>true</code> if the value of the field was succefuly setted or
	 *         <code>false</code> otherwise.
	 */
	private boolean trySetField( Object object, Field field, Object value )
	{
		try
		{
			field.set(object, value);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

}
