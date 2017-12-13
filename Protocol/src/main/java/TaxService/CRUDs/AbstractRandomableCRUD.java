package TaxService.CRUDs;

import TaxService.DAOs.AbstractDAO;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractRandomableCRUD<T extends AbstractDAO> extends AbstractCRUD<T>
{
	public AbstractRandomableCRUD(Connection connection, Class<T> clazz)
	{
		super(connection, clazz);
	}

	protected abstract T generateRandomBean();

	//docs.jboss.org/hibernate/orm/4.3/manual/en-US/html/ch15.html
	//sessionFactory.getConfiguration().getProperty("hibernate.jdbc.batch_size");
	public void insertRandomBeans(int n) throws SQLException
	{
		/*for (int i = 0, j = 0; i < n; i += j)
		{
			for (j = 0; j < 640 && j < n - i; ++j) //j < batch size
				session.save(generateRandomBean());
			session.flush();
			session.clear();
		}*/
		//TODO: разобраться с id, см. todo в AbstractCRUD.java
		List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
		fields.remove(0);
		String colNames = fields.stream().map(item -> item.getName()).collect(Collectors.joining(", "));
		String qmarks = String.join(", ", Collections.nCopies(fields.size(), "?"));
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + clazz.getSimpleName() + " " + colNames + " VALUES " + qmarks);
		try
		{
			T randomable;
			for (int i = 0; i < n; ++i)
			{
				randomable = generateRandomBean();
				for (int j = 0; j < fields.size(); ++j)
					if (AbstractDAO.class.isAssignableFrom(fields.get(j).getClass()))
						stmt.setLong(j, ((AbstractDAO)fields.get(j).get(randomable)).getId());
					else
						stmt.setObject(j, fields.get(j).get(randomable));
			}
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
}
