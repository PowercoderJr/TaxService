package TaxService.CRUDs;

import TaxService.DAOs.AbstractRefDAO;
import TaxService.RandomHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractRefCRUD<T extends AbstractRefDAO> extends AbstractCRUD<T>
{
	public AbstractRefCRUD(Connection connection, Class<T> clazz)
	{
		super(connection, clazz);
	}

	public void fillFromSource() throws SQLException
	{
		String clazzName = clazz.getSimpleName().toLowerCase();
		fillFromSource("nomenclature/" + clazzName + (clazzName.charAt(clazzName.length() - 1) != 's' ? "s" : "es") + ".txt");
	}

	protected void fillFromSource(String srcPath) throws SQLException
	{
		try(InputStream src = RandomHelper.class.getClassLoader().getResourceAsStream(srcPath))
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(src, StandardCharsets.UTF_8));
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + clazz.getSimpleName() + " name VALUES ?");
			for (String item = reader.readLine(); item != null; item = reader.readLine())
				//create(clazz.getConstructor(String.class).newInstance(item));
				stmt.setObject(1, item);
		}
		catch (IOException /*| IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException */e)
		{
			e.printStackTrace();
		}

	}
}
