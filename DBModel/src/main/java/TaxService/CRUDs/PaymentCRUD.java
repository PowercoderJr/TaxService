package TaxService.CRUDs;

import TaxService.DAO.*;
import TaxService.RandomHelper;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
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

	public boolean delete(Serializable id)
	{
		connect();
		Payment payment = session.get(Payment.class, id);
		if (payment != null)
		{
			session.delete(payment);
			session.getTransaction().commit();
			return true;
		}
		return false;
	}

	public Payment get(Serializable id)
	{
		connect();
		Payment payment = session.get(Payment.class, id);
		session.getTransaction().commit();
		return payment;
	}

	public List<Payment> getAll()
	{
		connect();
		TypedQuery<Payment> query = session.createQuery("SELECT a FROM Payment a", Payment.class);
		session.getTransaction().commit();
		return query.getResultList();
	}

	@Override
	protected Payment generateRandomBean()
	{
		PaytypeCRUD crud1 = new PaytypeCRUD(factory);
		List<Paytype> paytypes = crud1.getAll();
		Paytype paytype = paytypes.get((int) (Math.random() * paytypes.size()));
		crud1.disconnect();

		Date date = RandomHelper.getRandomDateBetween(LocalDate.of(2000, 1, 1), LocalDate.now());

		BigDecimal amount = BigDecimal.valueOf(Math.random() * 100000);

		EmployeeCRUD crud2 = new EmployeeCRUD(factory);
		List<Employee> employees = crud2.getAll();
		Employee employee = employees.get((int) (Math.random() * employees.size()));
		crud2.disconnect();

		DepartmentCRUD crud3 = new DepartmentCRUD(factory);
		List<Department> departments = crud3.getAll();
		Department department = departments.get((int) (Math.random() * departments.size()));
		crud3.disconnect();

		CompanyCRUD crud4 = new CompanyCRUD(factory);
		List<Company> companies = crud4.getAll();
		Company company = companies.get((int) (Math.random() * companies.size()));
		crud4.disconnect();

		return new Payment(paytype, date, amount, employee, department, company);
	}
}