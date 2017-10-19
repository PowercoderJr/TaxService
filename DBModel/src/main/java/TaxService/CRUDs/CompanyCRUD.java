package TaxService.CRUDs;

import TaxService.DAO.Company;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class CompanyCRUD extends AbstractCRUD<Company>
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
		return null;
	}
}