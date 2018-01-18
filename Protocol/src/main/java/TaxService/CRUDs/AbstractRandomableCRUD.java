package TaxService.CRUDs;

import TaxService.DAOs.AbstractDAO;
import TaxService.Utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractRandomableCRUD<T extends AbstractDAO> extends AbstractCRUD<T>
{
	protected static Random rnd = new Random();

	public AbstractRandomableCRUD(Connection connection, Connection superConnection, Class<T> clazz)
	{
		super(connection, superConnection, clazz);
	}

	protected abstract T generateRandomBean() throws SQLException;

	public void insertRandomBeans(int n) throws SQLException
	{
		List<Field> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(clazz.getFields()));
		fields.removeIf(item -> item.getName().equals("id") || item.getName()
				.equals("serialVersionUID") || item.getName().equals("readEvenIfLazy"));
		String colNames = Utils.fieldNamesToString(fields.stream());
		String qmarks = String.join(", ", Collections.nCopies(fields.size(), "?"));

		try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + clazz.getSimpleName() + " (" + colNames + ") VALUES (" + qmarks + ")"))
		{
			T randomable;
			for (int i = 0; i < n; ++i)
			{
				randomable = generateRandomBean();
				for (int j = 0; j < fields.size(); ++j)
				{
					if (AbstractDAO.class.isAssignableFrom(fields.get(j).getType()))
						stmt.setLong(j + 1, ((AbstractDAO) fields.get(j).get(randomable)).getId());
					else
						stmt.setObject(j + 1, fields.get(j).get(randomable));
				}
				stmt.executeUpdate();
			}
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
}
