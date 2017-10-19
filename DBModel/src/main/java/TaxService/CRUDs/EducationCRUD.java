package TaxService.CRUDs;

import TaxService.DAO.Education;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class EducationCRUD extends AbstractCRUD<Education>
{
	public EducationCRUD(SessionFactory factory)
	{
		super(factory);
	}

	public void create(Education object)
	{
		connect();
		session.save(object);
		session.getTransaction().commit();
	}

	public boolean delete(Serializable id)
	{
		connect();
		Education education = session.get(Education.class, id);
		if (education != null)
		{
			session.delete(education);
			session.getTransaction().commit();
			return true;
		}
		return false;
	}

	public Education get(Serializable id)
	{
		connect();
		Education education = session.get(Education.class, id);
		session.getTransaction().commit();
		return education;
	}

	public List<Education> getAll()
	{
		connect();
		TypedQuery<Education> query = session.createQuery("SELECT a FROM Education a", Education.class);
		session.getTransaction().commit();
		return query.getResultList();
	}

	@Override
	protected Education generateRandomBean()
	{
		return null;
	}
}