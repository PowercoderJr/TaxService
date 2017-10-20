package TaxService.CRUDs;

import TaxService.DAO.Owntype;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class OwntypeCRUD extends AbstractCRUD<Owntype>
{
	public OwntypeCRUD(SessionFactory factory)
	{
		super(factory);
	}

	public void create(Owntype object)
	{
		connect();
		session.save(object);
		session.getTransaction().commit();
	}

	public boolean delete(Serializable id)
	{
		connect();
		Owntype owntype = session.get(Owntype.class, id);
		if (owntype != null)
		{
			session.delete(owntype);
			session.getTransaction().commit();
			return true;
		}
		return false;
	}

	public Owntype get(Serializable id)
	{
		connect();
		Owntype owntype = session.get(Owntype.class, id);
		session.getTransaction().commit();
		return owntype;
	}

	public List<Owntype> getAll()
	{
		connect();
		TypedQuery<Owntype> query = session.createQuery("SELECT a FROM Owntype a", Owntype.class);
		session.getTransaction().commit();
		return query.getResultList();
	}
}