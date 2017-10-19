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

	protected abstract T generateRandomBean();

	//docs.jboss.org/hibernate/orm/4.3/manual/en-US/html/ch15.html
	//sessionFactory.getConfiguration().getProperty("hibernate.jdbc.batch_size");
	public void insertRandomBeans(int n)
	{
		connect();
		for (int i = 0, j = 0; i < n; i += j)
		{
			for (j = 0; j < 20 && j < n - i; ++j) //j < batch size
				session.save(generateRandomBean());
			session.flush();
			session.clear();
		}
		session.getTransaction().commit();
	}
}
