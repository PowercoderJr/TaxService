package TaxService.CRUDs;

import TaxService.DAOs.Company;
import TaxService.DAOs.Owntype;
import TaxService.RandomHelper;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;

public class CompanyCRUD extends AbstractRandomableCRUD<Company>
{
	public CompanyCRUD(SessionFactory factory)
	{
		super(factory, Company.class);
	}

	@Override
	protected Company generateRandomBean()
	{
		String name = RandomHelper.getRandomCompany();

		OwntypeCRUD owntypeCRUD = new OwntypeCRUD(factory);
		Owntype owntype = owntypeCRUD.getRandom();
		owntypeCRUD.disconnect();

		String phone = RandomHelper.getRandomPhone();

		BigDecimal startyear = new BigDecimal(1960 + (int)(Math.random() * 55));

		int statesize = 10 + (int)(Math.random() * 100);

		return new Company(name, owntype, phone, startyear, statesize);
	}
}
