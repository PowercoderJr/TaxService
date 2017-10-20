package TaxService.CRUDs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractCRUD<T>
{
	protected SessionFactory factory;
	protected Session session;

	public AbstractCRUD(SessionFactory factory)
	{
		this.factory = factory;
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

	public abstract void create(T object);

	public abstract boolean delete(Serializable id);

	public abstract T get(Serializable id);

	public abstract List<T> getAll();
}
