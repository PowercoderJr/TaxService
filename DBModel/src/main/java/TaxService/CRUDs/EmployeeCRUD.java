package TaxService.CRUDs;

import TaxService.DAO.Department;
import TaxService.DAO.Education;
import TaxService.DAO.Employee;
import TaxService.RandomHelper;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class EmployeeCRUD extends AbstractRandomableCRUD<Employee>
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
		RandomHelper.Gender gender = Math.random() > 0.5 ? RandomHelper.Gender.MALE : RandomHelper.Gender.FEMALE;
		String surname = RandomHelper.getRandomSurname(gender);
		String name = RandomHelper.getRandomName(gender);
		String patronymic = RandomHelper.getRandomPatronymic(gender);

		DepartmentCRUD departmentCRUD = new DepartmentCRUD(factory);
		List<Department> departments = departmentCRUD.getAll();
		Department department = departments.get((int) (Math.random() * departments.size()));
		departmentCRUD.disconnect();

		Date birthdate = RandomHelper.getRandomDateBetween(LocalDate.of(1950, 1, 1), LocalDate.of(1990, 12, 31));

		String post = RandomHelper.getRandomPost();

		int salary = (int)(20 + Math.random() * 180) * 1000;

		EducationCRUD educationCRUD = new EducationCRUD(factory);
		List<Education> educations = educationCRUD.getAll();
		Education education = educations.get((int) (Math.random() * educations.size()));
		educationCRUD.disconnect();

		return new Employee(surname, name, patronymic, department, birthdate, post, salary, education);
	}
}