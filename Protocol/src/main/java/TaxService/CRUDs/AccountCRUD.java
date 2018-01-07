package TaxService.CRUDs;

import TaxService.DAOs.Account;
import TaxService.DAOs.City;

import java.sql.Connection;

public class AccountCRUD extends AbstractCRUD<Account>
{
	public AccountCRUD(Connection connection)
	{
		super(connection, Account.class);
	}
}
