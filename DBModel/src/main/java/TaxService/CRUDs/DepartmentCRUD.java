package TaxService.CRUDs;

import java.io.Serializable;
import java.util.List;
import javax.persistence.TypedQuery;
import org.hibernate.SessionFactory;
import TaxService.DAO.Department;

public class DepartmentCRUD extends AbstractCRUD<Department>
{
	public DepartmentCRUD(SessionFactory factory)
	{
		super(factory);
	}

	public void create(Department object)
	{
		connect();
		session.save(object);
		session.getTransaction().commit();
	}

	public boolean delete(Serializable id)
	{
		connect();
		Department department = session.get(Department.class, id);
		if (department != null)
		{
			session.delete(department);
			session.getTransaction().commit();
			return true;
		}
		return false;
	}

	public Department get(Serializable id)
	{
		connect();
		Department department = session.get(Department.class, id);
		session.getTransaction().commit();
		return department;
	}

	public List<Department> getAll()
	{
		connect();
		TypedQuery<Department> query = session.createQuery("SELECT a FROM Department a", Department.class);
		session.getTransaction().commit();
		return query.getResultList();
	}

	@Override
	protected Department generateRandomBean()
	{
		return null;
	}
}
