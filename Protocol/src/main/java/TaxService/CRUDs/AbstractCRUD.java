package TaxService.CRUDs;

import TaxService.DAOs.AbstractDAO;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//TODO: рефлексия - сделать id приватными или удалять их из массива полей, когда они не нужны
public abstract class AbstractCRUD<T extends AbstractDAO>
{
	//protected SessionFactory factory;
	protected Connection connection;
	//protected Session session;
	protected Class<T> clazz;

	//public AbstractCRUD(){}

	public AbstractCRUD(Connection connection, Class<T> clazz)
	{
		this.connection = connection;
		this.clazz = clazz;
	}

	/*public void connect()
	{
		if (session == null || !session.isOpen())
			session = factory.openSession();

		session.beginTransaction();
	}

	public void disconnect()
	{
		if (session != null && session.isOpen())
			session.close();
	}*/

	public void create(T object) throws SQLException
	{
		//на будущее: Super.class.isAssignableFrom(Sub.class)
		Field[] fields = clazz.getDeclaredFields();
		String colNames = Arrays.stream(fields).map(item -> item.getName()).collect(Collectors.joining(", "));
		String values = Arrays.stream(fields).map(item ->
		{
			String result = "";
			try
			{
				result = item.get(object).toString();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return result;
		}).collect(Collectors.joining(", "));

		Statement stmt = connection.createStatement();
		stmt.executeQuery("INSERT INTO " + clazz.getSimpleName() + " " + colNames + " VALUES " + values);
	}

	public void delete(long id) throws SQLException
	{
		Statement stmt = connection.createStatement();
		stmt.executeQuery("DELETE FROM " + clazz.getSimpleName() + " WHERE id = " + id);
	}

	public T read(long id) throws SQLException
	{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT 1 FROM " + clazz.getSimpleName() + " WHERE id = " + id);
		return rs.next() ? reflectResultSet(rs).get(0) : null;
	}

	public List<T> readHundred(int hundred) throws SQLException
	{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName() + " OFFSET " + (hundred * 100) + "LIMIT 100");
		return rs.next() ? reflectResultSet(rs) : null;
	}

	public List<T> readAll() throws SQLException
	{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName());
		return rs.next() ? reflectResultSet(rs) : null;
	}

	public T getRandom() throws SQLException
	{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName() + " OFFSET FLOOR(RANDOM()*(SELECT COUNT(*) FROM " + clazz.getSimpleName() + ")) LIMIT 1");
		return rs.next() ? reflectResultSet(rs).get(0) : null;
	}

	//https://stackoverflow.com/questions/21956042/mapping-a-jdbc-resultset-to-an-object
	private List<T> reflectResultSet(ResultSet rs) throws SQLException
	{
		//TODO: хорошо ли бефорфёрстить здесь??
		rs.beforeFirst();

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields)
			field.setAccessible(true);

		List<T> list = new ArrayList<>();
		try
		{
			while (rs.next())
			{
				T object = clazz.getConstructor().newInstance();

				for (Field field : fields)
				{
					String name = field.getName();
					String value = rs.getString(name);
					field.set(object, field.getType().getConstructor(String.class).newInstance(value));
				}
				list.add(object);
			}
		}
		catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e)
		{
			e.printStackTrace();
		}

		return list;
	}
}
