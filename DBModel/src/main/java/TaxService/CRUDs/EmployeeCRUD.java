package TaxService.CRUDs;

import TaxService.DAO.Employee;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class EmployeeCRUD extends AbstractCRUD<Employee>
{
	public EmployeeCRUD(SessionFactory factory)
	{
		super(factory);
	}

	public void create(Employee object)
	{
		connect();
		session.save(object);
		session.getTransaction().commit();
	}

	public boolean delete(Serializable id)
	{
		connect();
		Employee employee = session.get(Employee.class, id);
		if (employee != null)
		{
			session.delete(employee);
			session.getTransaction().commit();
			return true;
		}
		return false;
	}

	public Employee get(Serializable id)
	{
		connect();
		Employee employee = session.get(Employee.class, id);
		session.getTransaction().commit();
		return employee;
	}

	public List<Employee> getAll()
	{
		connect();
		TypedQuery<Employee> query = session.createQuery("SELECT a FROM Employee a", Employee.class);
		session.getTransaction().commit();
		return query.getResultList();
	}

	@Override
	protected Employee generateRandomBean()
	{
		return null;
	}
}