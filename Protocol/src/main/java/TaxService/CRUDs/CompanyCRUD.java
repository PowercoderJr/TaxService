package TaxService.CRUDs;

import TaxService.DAOs.Company;
import TaxService.DAOs.Owntype;
import TaxService.RandomHelper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class CompanyCRUD extends AbstractRandomableCRUD<Company>
{
	public CompanyCRUD(Connection connection)
	{
		super(connection, Company.class);
	}

	@Override
	protected Company generateRandomBean() throws SQLException
	{
		String name = RandomHelper.getRandomCompany();

		OwntypeCRUD owntypeCRUD = new OwntypeCRUD(connection);
		Owntype owntype = owntypeCRUD.getRandom();

		String phone = RandomHelper.getRandomPhone();

		BigDecimal startyear = new BigDecimal(1960 + (int)(Math.random() * 55));

		int statesize = 10 + (int)(Math.random() * 100);

		return new Company(name, owntype, phone, startyear, statesize);
	}
}
