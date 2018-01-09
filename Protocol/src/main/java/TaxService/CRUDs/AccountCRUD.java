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
}
