package TaxService.CRUDs;

import TaxService.DAO.Payment;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class PaymentCRUD extends AbstractCRUD<Payment>
{
	public PaymentCRUD(SessionFactory factory)
	{
		super(factory);
	}

	public void create(Payment object)
	{
		connect();

		session.save(object);
		session.getTransaction().commit();
	}

	public boolean remove(Serializable id)
	{
		connect();

		Payment payment = session.get(Payment.class, id);

		if (payment != null)
		{
			session.getTransaction().commit();
			session.remove(payment);

			return true;
		}

		return false;
	}

	public Payment find(Serializable id)
	{
		connect();

		Payment payment = session.find(Payment.class, id);
		session.getTransaction().commit();

		return payment;
	}

	public List<Payment> findAll()
	{
		connect();

		TypedQuery<Payment> query = session.createQuery("SELECT a FROM Payment a", Payment.class);
		session.getTransaction().commit();

		return query.getResultList();
	}
}