package TaxService.CRUDs;

import TaxService.DAOs.AbstractDAO;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCRUD<T extends AbstractDAO>
{
	protected Connection connection;
	protected Class<T> clazz;

	//public AbstractCRUD(){}

	public AbstractCRUD(Connection connection, Class<T> clazz)
	{
		this.connection = connection;
		this.clazz = clazz;
	}

	public void create(T object) throws SQLException
	{
		//на будущее: Super.class.isAssignableFrom(Sub.class)
		List<Field> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(clazz.getFields()));
		fields.removeIf(item -> item.getName().equals("id") || item.getName().equals("serialVersionUID"));
		String colNames = fields.stream().map(item -> item.getName()).collect(Collectors.joining(", "));
		String values = fields.stream().map(item ->
		{
			String result = "";
			try
			{
				if (AbstractDAO.class.isAssignableFrom(item.getType()))
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
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName() + " WHERE id = " + id);
			List<T> result = reflectResultSet(rs);
			return result != null ? result.get(0) : null;
		}
	}

	public abstract T readLazy(long id) throws SQLException;

	public List<T> readHundred(int hundred) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName() + " OFFSET " + ((hundred - 1) * 100) + "LIMIT 100");
			return reflectResultSet(rs);
		}
	}

	public List<T> readAll() throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName());
			return reflectResultSet(rs);
		}
	}

	public T readRandom() throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + clazz.getSimpleName() + " OFFSET FLOOR(RANDOM()*(SELECT COUNT(*) FROM " +
					clazz.getSimpleName() + ")) LIMIT 1");
			List<T> result = reflectResultSet(rs);
			return result != null ? result.get(0) : null;
		}
	}

	public int count() throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + clazz.getSimpleName());
			rs.next();
			return rs.getInt(1);
		}
	}

	public ResultSet executeCustomQuery(String query) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			return stmt.executeQuery(query);
		}
	}

	//https://stackoverflow.com/questions/21956042/mapping-a-jdbc-resultset-to-an-object
	protected List<T> reflectResultSet(ResultSet rs) throws SQLException
	{
		List<T> list = new ArrayList<>();
		if (rs.next())
		{
			Field[] fields = clazz.getFields();
			try
			{
				do
				{
					T object = clazz.getConstructor().newInstance();

					for (Field field : fields)
					{
						String name = AbstractDAO.class.isAssignableFrom(field.getType()) ? field.getName() + "_id" : field.getName();
						String value = rs.getString(name);
						field.set(object, parseObject(field.getType(), value));
					}
					list.add(object);
				} while (rs.next());
			}
			catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	private Object parseObject(Class clazz, String value ) throws ClassNotFoundException, SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		//Примитивные
		if( Boolean.class == clazz || Boolean.TYPE == clazz) return Boolean.parseBoolean( value );
		if( Byte.class == clazz || Byte.TYPE == clazz) return Byte.parseByte( value );
		if( Short.class == clazz || Short.TYPE == clazz) return Short.parseShort( value );
		if( Integer.class == clazz || Integer.TYPE == clazz) return Integer.parseInt( value );
		if( Long.class == clazz || Long.TYPE == clazz) return Long.parseLong( value );
		if( Float.class == clazz || Float.TYPE == clazz) return Float.parseFloat( value );
		if( Double.class == clazz || Double.TYPE == clazz) return Double.parseDouble( value );

		//Мои DAO
		if (AbstractDAO.class.isAssignableFrom(clazz))
		{
			Class crudClass = Class.forName("TaxService.CRUDs." + clazz.getSimpleName() + "CRUD");
			AbstractCRUD instance = (AbstractCRUD) crudClass.getConstructor(Connection.class).newInstance(connection);
			return instance.readLazy(Long.parseLong(value));
		}

		//Другие
		if (String.class == clazz) return value;
		if (BigDecimal.class == clazz) return new BigDecimal(value);
		if (Date.class == clazz) return Date.valueOf(value);
		if (Time.class == clazz) return Time.valueOf(value);
		if (Timestamp.class == clazz) return Timestamp.valueOf(value);

		//Неиспользуемые
		/*if (Blob.class == clazz) return ;
		if (Clob.class == clazz) return ;
		if (byte[].class == clazz) return ;
		if (Array.class == clazz) return ;
		if (PGobject.class == clazz) return ;
		if (Character.class == clazz) return ;
		if (LocalDate.class == clazz) return ;
		if (LocalTime.class == clazz) return ;
		if (LocalDateTime.class == clazz) return ;
		if (OffsetDateTime.class == clazz) return ;
		if (Map.class == clazz) return ;*/
		return null;
	}
}
