package TaxService.CRUDs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public abstract class AbstractCRUD<T>
{
	protected SessionFactory factory;
	protected Session session;
	protected Class<T> clazz;

	public AbstractCRUD(SessionFactory factory, Class<T> clazz)
	{
		this.factory = factory;
		this.clazz = clazz;
	}

	public void connect()
	{
		if (session == null || !session.isOpen())
			session = factory.openSession();

		session.beginTransaction();
	}

	public void disconnect()
	{
		if (session != null && session.isOpen())
			session.close();
	}

	public void create(T object)
	{
		connect();
		session.save(object);
		session.getTransaction().commit();
	};

	public boolean delete(Serializable id)
	{
		connect();
		T object = session.get(clazz, id);
		if (object != null)
		{
			session.delete(object);
			session.getTransaction().commit();
			return true;
		}
		return false;
	};

	public void delete(T object)
	{
		connect();
		session.delete(object);
		session.getTransaction().commit();
	};

	public T get(Serializable id)
	{
		connect();
		T object = session.get(clazz, id);
		session.getTransaction().commit();
		return object;
	};

	public List<T> getAll()
	{
		connect();
		TypedQuery<T> query = session.createQuery("SELECT a FROM " + clazz.getSimpleName() + " a", clazz);
		session.getTransaction().commit();
		return query.getResultList();
	};
}
