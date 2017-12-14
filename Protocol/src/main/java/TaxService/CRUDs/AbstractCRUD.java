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
		List<Field> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(clazz.getFields()));
		fields.removeIf(item -> item.getName().equals("id"));
		String colNames = fields.stream().map(item -> item.getName()).collect(Collectors.joining(", "));
		String values = fields.stream().map(item ->
		{
			String result = "";
			try
			{
				if (AbstractDAO.class.isAssignableFrom(item.getClass()))
					result = String.valueOf(((AbstractDAO)item.get((object).getId())).getId());
				else
					result = String.valueOf(item.get(object));
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return "'" + result + "'";
		}).collect(Collectors.joining(", "));

		try (Statement stmt = connection.createStatement())
		{
			stmt.executeUpdate("INSERT INTO " + clazz.getSimpleName() + " (" + colNames + ") VALUES (" + values + ")");
		}
	}

	public void delete(long id) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			stmt.executeUpdate("DELETE FROM " + clazz.getSimpleName() + " WHERE id = " + id);
		}
	}

	public T read(long id) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT 1 FROM " + clazz.getSimpleName() + " WHERE id = " + id);
			return rs.next() ? reflectResultSet(rs).get(0) : null;
		}
	}

	public List<T> readHundred(int hundred) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName() + " OFFSET " + (hundred * 100) + "LIMIT 100");
			return rs.next() ? reflectResultSet(rs) : null;
		}
	}

	public List<T> readAll() throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName());
			return rs.next() ? reflectResultSet(rs) : null;
		}
	}

	public T getRandom() throws SQLException
	{
		try (Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
		{
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName() + " OFFSET FLOOR(RANDOM()*(SELECT COUNT(*) FROM " +
					clazz.getSimpleName() + ")) LIMIT 1");
			return rs.next() ? reflectResultSet(rs).get(0) : null;
		}
	}

	//https://stackoverflow.com/questions/21956042/mapping-a-jdbc-resultset-to-an-object
	//TODO: правильно отражать примитивные типы - https://stackoverflow.com/questions/13943550/how-to-convert-from-string-to-a-primitive-type-or-standard-java-wrapper-types
	private List<T> reflectResultSet(ResultSet rs) throws SQLException
	{
		//TODO: хорошо ли бефорфёрстить здесь??
		rs.beforeFirst();

		Field[] fields = clazz.getFields();

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
					if (AbstractDAO.class.isAssignableFrom(field.getClass()))
					{
						Class crudClass = Class.forName(field.getClass().getSimpleName() + "CRUD");
						AbstractCRUD instance = (AbstractCRUD) crudClass.getConstructor(Connection.class).newInstance(connection);
						AbstractDAO child = instance.read(Long.parseLong(value));
						field.set(object, child);
					}
					else
					{
						field.set(object, field.getType().getConstructor(String.class).newInstance(value)); //TODO ?
					}
				}
				list.add(object);
			}
		}
		catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return list;
	}
}
