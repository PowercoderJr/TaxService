package TaxService.CRUDs;

import TaxService.DAOs.Company;
import TaxService.DAOs.Owntype;
import TaxService.RandomHelper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
		Owntype owntype = owntypeCRUD.readRandom(true);

		String phone = RandomHelper.getRandomPhone();

		BigDecimal startyear = new BigDecimal(1960 + rnd.nextInt(55));

		int statesize = 10 + rnd.nextInt(100);

		return new Company(name, owntype, phone, startyear, statesize);
	}
}
