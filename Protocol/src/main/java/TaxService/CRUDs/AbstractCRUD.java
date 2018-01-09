package TaxService.CRUDs;

import TaxService.DAOs.*;
import TaxService.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCRUD<T extends AbstractDAO>
{
	public static final int PORTION_SIZE = 100;

	static
	{
		Department.init();
		Employee.init();
		Company.init();
		Payment.init();
		Deptype.init();
		City.init();
		Post.init();
		Education.init();
		Owntype.init();
		Paytype.init();
	}

	protected Connection connection;
	protected Class<T> clazz;

	public AbstractCRUD(Connection connection, Class<T> clazz)
	{
		this.connection = connection;
		this.clazz = clazz;
	}

	public int create(T object) throws SQLException
	{
		List<Field> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(clazz.getFields()));
		fields.removeIf(item -> item.getName().equals("id") || item.getName()
				.equals("serialVersionUID") || item.getName().equals("readEvenIfLazy"));
		String colNames = Utils.fieldNamesToString(fields.stream());
		String values = Utils.fieldValuesToString(fields.stream(), object);

		try (Statement stmt = connection.createStatement())
		{
			return stmt.executeUpdate("INSERT INTO " + clazz.getSimpleName() + " (" + colNames + ") VALUES (" +
					values + ")");
		}
	}

	public T read(long id, boolean isLazy, @Nullable String filter) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			if (filter == null) filter = "";
			filter = conjunctFilters("id = " + id, filter);
			String fields;
			if (isLazy)
				fields = Utils.fieldNamesToString(Arrays.stream(AbstractDAO.getReadEvenIfLazy(clazz)));
			else
				fields = "*";
			filter = adjustFilter(filter);
			ResultSet rs = stmt.executeQuery("SELECT " + fields + " FROM " + clazz.getSimpleName() + filter);
			List<T> result = reflectResultSet(rs, isLazy);
			return result.isEmpty() ? null : result.get(0);
		}
	}

	public List<T> readPortion(int portion, boolean isLazy, @Nullable String filter) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			if (filter == null) filter = "";
			String fields;
			if (isLazy)
				fields = Utils.fieldNamesToString(Arrays.stream(AbstractDAO.getReadEvenIfLazy(clazz)));
			else
				fields = "*";
			filter = adjustFilter(filter);
			ResultSet rs = stmt.executeQuery("SELECT " + fields + " FROM " + clazz.getSimpleName() +
					filter + " ORDER BY 1 ASC OFFSET " + ((portion - 1) * AbstractCRUD.PORTION_SIZE) +
					" LIMIT " + AbstractCRUD.PORTION_SIZE);
			return reflectResultSet(rs, isLazy);
		}
	}

	public List<T> readAll(boolean isLazy, @Nullable String filter) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			if (filter == null) filter = "";
			String fields;
			if (isLazy)
				fields = Utils.fieldNamesToString(Arrays.stream(AbstractDAO.getReadEvenIfLazy(clazz)));
			else
				fields = "*";
			filter = adjustFilter(filter);
			ResultSet rs = stmt.executeQuery("SELECT " + fields + " FROM " + clazz.getSimpleName() + filter + " ORDER BY 1 ASC");
			return reflectResultSet(rs, isLazy);
		}
	}

	public T readRandom(boolean isLazy, @Nullable String filter) throws SQLException
	{
		if (filter == null) filter = "";
		filter = conjunctFilters("", filter);
		try (Statement stmt = connection.createStatement())
		{
			String fields;
			if (isLazy)
				fields = Utils.fieldNamesToString(Arrays.stream(AbstractDAO.getReadEvenIfLazy(clazz)));
			else
				fields = "*";
			filter = adjustFilter(filter);
			ResultSet rs = stmt.executeQuery("SELECT " + fields + " FROM " + clazz.getSimpleName() +
					filter + " OFFSET FLOOR(RANDOM()*(SELECT COUNT(*) FROM " + clazz.getSimpleName() + filter + ")) LIMIT 1");
			List<T> result = reflectResultSet(rs, isLazy);
			return result != null ? result.get(0) : null;
		}
	}

	public int update(@Nonnull String filter, String newValues) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			filter = adjustFilter(filter);
			return stmt.executeUpdate("UPDATE " + clazz.getSimpleName() + " SET " + newValues + filter);
		}
	}

	public int delete(long id, @Nullable String filter) throws SQLException
	{
		if (filter == null) filter = "";
		filter = conjunctFilters("id = " + id, filter);
		try (Statement stmt = connection.createStatement())
		{
			filter = adjustFilter(filter);
			return stmt.executeUpdate("DELETE FROM " + clazz.getSimpleName() + filter);
		}
	}

	public int delete(@Nullable String filter) throws SQLException
	{
		if (filter == null) filter = "";
		try (Statement stmt = connection.createStatement())
		{
			filter = adjustFilter(filter);
			return stmt.executeUpdate("DELETE FROM " + clazz.getSimpleName() + filter);
		}
	}

	public int count(@Nullable String filter) throws SQLException
	{
		if (filter == null) filter = "";
		try (Statement stmt = connection.createStatement())
		{
			filter = adjustFilter(filter);
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + clazz.getSimpleName() + filter);
			rs.next();
			return rs.getInt(1);
		}
	}

	/*public ResultSet executeCustomQuery(String query) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			return stmt.executeQuery(query);
		}
	}*/

	//https://stackoverflow.com/questions/21956042/mapping-a-jdbc-resultset-to-an-object
	protected List<T> reflectResultSet(ResultSet rs, boolean isLazy) throws SQLException
	{
		List<T> list = new ArrayList<>();
		if (rs.next())
		{
			Field[] fields = isLazy ? AbstractDAO.getReadEvenIfLazy(clazz) : clazz.getFields();
			try
			{
				do
				{
					T object = clazz.getConstructor().newInstance();

					for (Field field : fields)
					{
						String name = AbstractDAO.class.isAssignableFrom(field.getType()) ? field.getName() + "_id" : field
								.getName();
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

	private Object parseObject(Class clazz, String value) throws ClassNotFoundException, SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		//Примитивные
		if (Boolean.class == clazz || Boolean.TYPE == clazz)
			return value.equalsIgnoreCase("t");
		if (Byte.class == clazz || Byte.TYPE == clazz)
			return Byte.parseByte(value);
		if (Short.class == clazz || Short.TYPE == clazz)
			return Short.parseShort(value);
		if (Integer.class == clazz || Integer.TYPE == clazz)
			return Integer.parseInt(value);
		if (Long.class == clazz || Long.TYPE == clazz)
			return Long.parseLong(value);
		if (Float.class == clazz || Float.TYPE == clazz)
			return Float.parseFloat(value);
		if (Double.class == clazz || Double.TYPE == clazz)
			return Double.parseDouble(value);

		//Мои DAO
		if (AbstractDAO.class.isAssignableFrom(clazz))
		{
			Class crudClass = Class.forName("TaxService.CRUDs." + clazz.getSimpleName() + "CRUD");
			AbstractCRUD instance = (AbstractCRUD) crudClass.getConstructor(Connection.class).newInstance(connection);
			return instance.read(Long.parseLong(value), true, null);
		}

		//Другие
		if (String.class == clazz)
			return value;
		if (BigDecimal.class == clazz)
			return new BigDecimal(value);
		if (Date.class == clazz)
			return Date.valueOf(value);
		if (Time.class == clazz)
			return Time.valueOf(value);
		if (Timestamp.class == clazz)
			return Timestamp.valueOf(value);
		if (Account.Roles.class == clazz)
			return Enum.valueOf(clazz, value);

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

	protected String conjunctFilters(@Nullable String filter1, @Nullable String filter2)
	{
		if (filter1 == null) filter1 = "";
		if (filter2 == null) filter2 = "";
		if (!filter2.isEmpty())
		{
			if (filter1.isEmpty())
				filter1 = filter2;
			else
				filter1 += " AND " + filter2;
		}
		return filter1;
	}

	private String adjustFilter(@Nullable String filter)
	{
		if (filter == null)
			filter = "";
		if (!filter.isEmpty())
			filter = " WHERE " + filter;
		return filter;
	}
}
