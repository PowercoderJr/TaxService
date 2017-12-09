package TaxService.CRUDs;

import TaxService.DAOs.Department;
import TaxService.DAOs.Education;
import TaxService.DAOs.Employee;
import TaxService.RandomHelper;
import org.hibernate.SessionFactory;

import java.sql.Date;
import java.time.LocalDate;

public class EmployeeCRUD extends AbstractRandomableCRUD<Employee>
{
	public EmployeeCRUD(SessionFactory factory)
	{
		super(factory, Employee.class);
	}

	@Override
	protected Employee generateRandomBean()
	{
		RandomHelper.Gender gender = Math.random() > 0.5 ? RandomHelper.Gender.MALE : RandomHelper.Gender.FEMALE;
		String surname = RandomHelper.getRandomSurname(gender);
		String name = RandomHelper.getRandomName(gender);
		String patronymic = RandomHelper.getRandomPatronymic(gender);

		DepartmentCRUD departmentCRUD = new DepartmentCRUD(factory);
		Department department = departmentCRUD.getRandom();
		departmentCRUD.disconnect();

		Date birthdate = RandomHelper.getRandomDateBetween(LocalDate.of(1950, 1, 1), LocalDate.of(1990, 12, 31));

		String post = RandomHelper.getRandomPost();

		int salary = (int)(20 + Math.random() * 180) * 1000;

		EducationCRUD educationCRUD = new EducationCRUD(factory);
		Education education = educationCRUD.getRandom();
		educationCRUD.disconnect();

		return new Employee(surname, name, patronymic, department, birthdate, post, salary, education);
	}
}
