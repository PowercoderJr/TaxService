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
	public AbstractRefCRUD(Connection connection, Connection superConnection, Class<T> clazz)
	{
		super(connection, superConnection, clazz);
	}

	public void fillFromSource() throws SQLException
	{
		StringBuilder srcPath = new StringBuilder("nomenclature/" + clazz.getSimpleName().toLowerCase());
		switch (srcPath.charAt(srcPath.length() - 1))
		{
			case 's':
				srcPath.append("e");
				break;
			case 'y':
				srcPath.deleteCharAt(srcPath.length() - 1);
				srcPath.append("ie");
				break;
		}
		srcPath.append("s.txt");

		fillFromSource(srcPath.toString());
	}

	protected void fillFromSource(String srcPath) throws SQLException
	{
		try(InputStream src = RandomHelper.class.getClassLoader().getResourceAsStream(srcPath);
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + clazz.getSimpleName() + " (name) VALUES (?)"))
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(src, StandardCharsets.UTF_8));
			for (String item = reader.readLine(); item != null; item = reader.readLine())
			{
				//create(clazz.getConstructor(String.class).newInstance(item));
				stmt.setObject(1, item);
				stmt.executeUpdate();
			}
		}
		catch (IOException /*| IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException */e)
		{
			e.printStackTrace();
		}
	}
}
