package TaxService.CRUDs;

import org.hibernate.SessionFactory;

public abstract class AbstractRandomableCRUD<T> extends AbstractCRUD<T>
{
	public AbstractRandomableCRUD(SessionFactory factory, Class<T> clazz)
	{
		super(factory, clazz);
	}

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
