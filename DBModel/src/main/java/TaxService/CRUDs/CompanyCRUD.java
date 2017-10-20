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
		super(factory);
	}

	public void create(Company object)
	{
		connect();
		session.save(object);
		session.getTransaction().commit();
	}

	public boolean delete(Serializable id)
	{
		connect();
		Company company = session.get(Company.class, id);
		if (company != null)
		{
			session.delete(company);
			session.getTransaction().commit();
			return true;
		}
		return false;
	}

	public Company get(Serializable id)
	{
		connect();
		Company company = session.get(Company.class, id);
		session.getTransaction().commit();
		return company;
	}

	public List<Company> getAll()
	{
		connect();
		TypedQuery<Company> query = session.createQuery("SELECT a FROM Company a", Company.class);
		session.getTransaction().commit();
		return query.getResultList();
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