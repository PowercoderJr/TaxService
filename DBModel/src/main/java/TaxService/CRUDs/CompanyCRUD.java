package TaxService.CRUDs;

import TaxService.DAO.Company;
import TaxService.DAO.Owntype;
import TaxService.RandomHelper;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
		List<Owntype> owntypes = owntypeCRUD.getAll();
		Owntype owntype = owntypes.get((int) (Math.random() * owntypes.size()));
		owntypeCRUD.disconnect();

		String phone = RandomHelper.getRandomPhone();

		BigDecimal startyear = new BigDecimal(1960 + (int)(Math.random() * 55));

		int statesize = 10 + (int)(Math.random() * 100);

		return new Company(name, owntype, phone, startyear, statesize);
	}
}
