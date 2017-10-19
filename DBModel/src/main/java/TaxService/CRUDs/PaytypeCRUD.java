package TaxService.CRUDs;

import TaxService.DAO.Paytype;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class PaytypeCRUD extends AbstractCRUD<Paytype>
{
	public PaytypeCRUD(SessionFactory factory)
	{
		super(factory);
	}

	public void create(Paytype object)
	{
		connect();
		session.save(object);
		session.getTransaction().commit();
	}

	public boolean delete(Serializable id)
	{
		connect();
		Paytype paytype = session.get(Paytype.class, id);
		if (paytype != null)
		{
			session.delete(paytype);
			session.getTransaction().commit();
			return true;
		}
		return false;
	}

	public Paytype get(Serializable id)
	{
		connect();
		Paytype paytype = session.get(Paytype.class, id);
		session.getTransaction().commit();
		return paytype;
	}

	public List<Paytype> getAll()
	{
		connect();
		TypedQuery<Paytype> query = session.createQuery("SELECT a FROM Paytype a", Paytype.class);
		session.getTransaction().commit();
		return query.getResultList();
	}

	@Override
	protected Paytype generateRandomBean()
	{
		return null;
	}
}