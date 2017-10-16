package TaxService.Services;

import java.io.Serializable;
import java.util.List;
import javax.persistence.TypedQuery;
import org.hibernate.SessionFactory;
import TaxService.DAO.Department;

public class DepartmentService extends Service<Department>
{
	public DepartmentService(SessionFactory factory)
	{
		super(factory);
	}

	public void create(Department object)
	{
		connect();

		session.save(object);
		session.getTransaction().commit();
	}

	public boolean remove(Serializable id)
	{
		connect();

		Department department = session.get(Department.class, id);

		if (department != null)
		{
			session.getTransaction().commit();
			session.remove(department);

			return true;
		}

		return false;
	}

	public Department find(Serializable id)
	{
		connect();

		Department department = session.find(Department.class, id);
		session.getTransaction().commit();

		return department;
	}

	public List<Department> findAll()
	{
		connect();

		TypedQuery<Department> query = session.createQuery("SELECT a FROM department a", Department.class);
		session.getTransaction().commit();

		return query.getResultList();
	}
}