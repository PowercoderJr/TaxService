package TaxService.CRUDs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.TypedQuery;

import TaxService.DAO.Deptype;
import TaxService.RandomHelper;
import org.hibernate.SessionFactory;
import TaxService.DAO.Department;

public class DepartmentCRUD extends AbstractRandomableCRUD<Department>
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
		String name = "Уникальное название";

		DeptypeCRUD deptypeCRUD = new DeptypeCRUD(factory);
		List<Deptype> deptypes = deptypeCRUD.getAll();
		Deptype deptype = deptypes.get((int) (Math.random() * deptypes.size()));
		deptypeCRUD.disconnect();

		BigDecimal startyear = new BigDecimal(1960 + (int)(Math.random() * 55));

		String phone = RandomHelper.getRandomPhone();

		String city = RandomHelper.getRandomCity();

		String street = RandomHelper.getRandomStreet();

		String house = RandomHelper.getRandomHouse();

		return new Department(name, deptype, startyear, phone, city, street, house);
	}
}
