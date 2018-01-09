package TaxService.CRUDs;

import TaxService.DAOs.AbstractDAO;
import TaxService.DAOs.Account;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class AbstractRandomableLocalizableCRUD<T extends AbstractDAO> extends AbstractRandomableCRUD<T>
{
	public AbstractRandomableLocalizableCRUD(Connection connection, Class<T> clazz)
	{
		super(connection, clazz);
	}

	@Override
	public T read(long id, boolean isLazy, @Nullable String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilterIfNotPrivileged();
		filter = conjunctFilters(filter, extraFilter);
		return super.read(id, isLazy, filter);
	}

	@Override
	public List<T> readPortion(int portion, boolean isLazy, @Nullable String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilterIfNotPrivileged();
		filter = conjunctFilters(filter, extraFilter);
		return super.readPortion(portion, isLazy, filter);
	}

	@Override
	public List<T> readAll(boolean isLazy, @Nullable String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilterIfNotPrivileged();
		filter = conjunctFilters(filter, extraFilter);
		return super.readAll(isLazy, filter);
	}

	@Override
	public T readRandom(boolean isLazy, @Nullable String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilterIfNotPrivileged();
		filter = conjunctFilters(filter, extraFilter);
		return super.readRandom(isLazy, filter);
	}

	@Override
	public int update(@Nonnull String filter, String newValues) throws SQLException
	{
		String extraFilter = getLocalizationFilterIfNotPrivileged();
		filter = conjunctFilters(filter, extraFilter);
		return super.update(filter, newValues);
	}

	@Override
	public int delete(long id, @Nullable String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilterIfNotPrivileged();
		filter = conjunctFilters(filter, extraFilter);
		return super.delete(id, filter);
	}

	@Override
	public int delete(@Nullable String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilterIfNotPrivileged();
		filter = conjunctFilters(filter, extraFilter);
		return super.delete(filter);
	}

	@Override
	public int count(@Nullable String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilterIfNotPrivileged();
		filter = conjunctFilters(filter, extraFilter);
		return super.count(filter);
	}

	@Deprecated
	protected String getLocalizationFilterIfNotPrivileged() throws SQLException
	{
		String localizationFilter = "";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT role FROM account WHERE account.login = CURRENT_USER");
		if (rs.next())
		{
			Account.Roles role = Enum.valueOf(Account.Roles.class, rs.getString(1));
			if (role == Account.Roles.JUSTUSER || role == Account.Roles.OPERATOR)
			{
				rs = stmt.executeQuery("SELECT department_id FROM employee WHERE employee.id = "
						+ "(SELECT employee_id FROM account WHERE login = CURRENT_USER)");
				rs.next();
				localizationFilter = "department_id = " + rs.getString(1);
			}
		}
		return localizationFilter;
	}
}
