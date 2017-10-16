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

	public boolean remove(Serializable id)
	{
		connect();

		Education education = session.get(Education.class, id);

		if (education != null)
		{
			session.getTransaction().commit();
			session.remove(education);

			return true;
		}

		return false;
	}

	public Education find(Serializable id)
	{
		connect();

		Education education = session.find(Education.class, id);
		session.getTransaction().commit();

		return education;
	}

	public List<Education> findAll()
	{
		connect();

		TypedQuery<Education> query = session.createQuery("SELECT a FROM Education a", Education.class);
		session.getTransaction().commit();

		return query.getResultList();
	}
}