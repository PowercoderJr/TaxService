package TaxService.CRUDs;

import TaxService.DAOs.Department;
import TaxService.DAOs.Education;
import TaxService.DAOs.Employee;
import TaxService.DAOs.Post;
import TaxService.RandomHelper;

import java.sql.*;
import java.time.LocalDate;

public class EmployeeCRUD extends AbstractRandomableCRUD<Employee>
{
	public EmployeeCRUD(Connection connection)
	{
		super(connection, Employee.class);
	}

	@Override
	protected Employee generateRandomBean() throws SQLException
	{
		RandomHelper.Gender gender = rnd.nextDouble() > 0.5 ? RandomHelper.Gender.MALE : RandomHelper.Gender.FEMALE;
		String surname = RandomHelper.getRandomSurname(gender);
		String name = RandomHelper.getRandomName(gender);
		String patronymic = RandomHelper.getRandomPatronymic(gender);

		DepartmentCRUD departmentCRUD = new DepartmentCRUD(connection);
		Department department = departmentCRUD.readRandom();

		Date birthdate = RandomHelper.getRandomDateBetween(LocalDate.of(1950, 1, 1), LocalDate.of(1990, 12, 31));

		PostCRUD postCRUD = new PostCRUD(connection);
		Post post = postCRUD.readRandom();

		int salary = 20 + rnd.nextInt(180) * 1000;

		EducationCRUD educationCRUD = new EducationCRUD(connection);
		Education education = educationCRUD.readRandom();

		return new Employee(surname, name, patronymic, department, birthdate, post, salary, education);
	}

	@Override
	public Employee readLazy(long id) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT id, surname, name, patronymic FROM employee WHERE id = " + id);
			if (rs.next())
			{
				Employee result = new Employee();
				result.id = rs.getLong(1);
				result.surname = rs.getString(2);
				result.name = rs.getString(3);
				result.patronymic = rs.getString(4);
				return result;
			}
			else
				return null;
		}
	}
}
