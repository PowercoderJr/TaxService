package TaxService.CRUDs;

import TaxService.DAOs.Account;
import TaxService.DAOs.City;
import TaxService.PhraseBook;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class AccountCRUD extends AbstractCRUD<Account>
{
	public AccountCRUD(Connection connection)
	{
		super(connection, Account.class);
	}

	@Override
	public int create(Account object) throws SQLException
	{
		String[] logpass = object.getLogin().split("\\" + PhraseBook.SEPARATOR, 2);
		object.setLogin(logpass[0]);
		String password = logpass[1].replace("\\", "\\\\").replace("\'", "\\\'");
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("CREATE USER " + object.getLogin() + " WITH PASSWORD '" + password + "' IN ROLE " + String.valueOf(object.getRole()));
		if (object.getRole() == Account.Roles.ADMIN)
			stmt.executeUpdate("ALTER USER " + object.getLogin() + " WITH CREATEROLE;");

		return super.create(object);
	}

	@Override
	public int update(String login, String blockedStr) throws SQLException
	{
		Statement stmt = connection.createStatement();
		boolean blocked = Boolean.valueOf(blockedStr);
		if (blocked)
			stmt.executeUpdate("ALTER USER " + login + " WITH NOLOGIN");
		else
			stmt.executeUpdate("ALTER USER " + login + " WITH LOGIN");
		return super.update("WHERE (login) = " + "('" + login + "')", "(blocked) = " + "(" + blockedStr + ")");
	}

	@Override
	public int delete(long id) throws SQLException
	{
		return super.delete(id);
	}

	@Override
	public int delete(String filter) throws SQLException
	{
		return super.delete(filter);
	}
}
