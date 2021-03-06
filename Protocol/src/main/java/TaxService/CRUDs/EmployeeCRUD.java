package TaxService.CRUDs;

import TaxService.DAOs.*;
import TaxService.RandomHelper;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class EmployeeCRUD extends AbstractRandomableCRUD<Employee>
{
	public EmployeeCRUD(Connection connection, Connection superConnection)
	{
		super(connection, superConnection, Employee.class);
	}

	@Override
	protected Employee generateRandomBean() throws SQLException
	{
		RandomHelper.Gender gender = rnd.nextDouble() > 0.5 ? RandomHelper.Gender.MALE : RandomHelper.Gender.FEMALE;
		String surname = RandomHelper.getRandomSurname(gender);
		String name = RandomHelper.getRandomName(gender);
		String patronymic = RandomHelper.getRandomPatronymic(gender);

		DepartmentCRUD departmentCRUD = new DepartmentCRUD(connection, superConnection);
		Department department = departmentCRUD.readRandom(true, null);

		Date birthdate = RandomHelper.getRandomDateBetween(LocalDate.of(1950, 1, 1), LocalDate.of(2005, 12, 31));

		PostCRUD postCRUD = new PostCRUD(connection, superConnection);
		Post post = postCRUD.readRandom(true, null);

		int salary = 20 + rnd.nextInt(180) * 1000;

		EducationCRUD educationCRUD = new EducationCRUD(connection, superConnection);
		Education education = educationCRUD.readRandom(true, null);

		return new Employee(surname, name, patronymic, department, birthdate, post, salary, education);
	}
}
