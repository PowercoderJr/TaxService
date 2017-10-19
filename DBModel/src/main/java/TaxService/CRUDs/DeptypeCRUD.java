package TaxService.CRUDs;

import TaxService.DAO.Deptype;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class DeptypeCRUD extends AbstractCRUD<Deptype>
{
	public DeptypeCRUD(SessionFactory factory)
	{
		super(factory);
	}

	public void create(Deptype object)
	{
		connect();
		session.save(object);
		session.getTransaction().commit();
	}

	public boolean delete(Serializable id)
	{
		connect();
		Deptype deptype = session.get(Deptype.class, id);
		if (deptype != null)
		{
			session.delete(deptype);
			session.getTransaction().commit();
			return true;
		}
		return false;
	}

	public Deptype get(Serializable id)
	{
		connect();
		Deptype deptype = session.get(Deptype.class, id);
		session.getTransaction().commit();
		return deptype;
	}

	public List<Deptype> getAll()
	{
		connect();
		TypedQuery<Deptype> query = session.createQuery("SELECT a FROM Deptype a", Deptype.class);
		session.getTransaction().commit();
		return query.getResultList();
	}

	@Override
	protected Deptype generateRandomBean()
	{
		return null;
	}
}