package TaxService.CRUDs;

import TaxService.DAOs.*;
import TaxService.RandomHelper;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

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
		Department department = departmentCRUD.readRandom(true);

		Date birthdate = RandomHelper.getRandomDateBetween(LocalDate.of(1950, 1, 1), LocalDate.of(1990, 12, 31));

		PostCRUD postCRUD = new PostCRUD(connection);
		Post post = postCRUD.readRandom(true);

		int salary = 20 + rnd.nextInt(180) * 1000;

		EducationCRUD educationCRUD = new EducationCRUD(connection);
		Education education = educationCRUD.readRandom(true);

		return new Employee(surname, name, patronymic, department, birthdate, post, salary, education);
	}

	@Override
	public List<Employee> readPortion(int portion, boolean isLazy, String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilter();
		if (!extraFilter.isEmpty())
		{
			if (filter == null || filter.isEmpty())
				filter = " WHERE " + extraFilter;
			else
				filter += " AND " + extraFilter;
		}
		return super.readPortion(portion, isLazy, filter);
	}

	@Override
	public List<Employee> readAll(boolean isLazy, String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilter();
		if (!extraFilter.isEmpty())
		{
			if (filter == null || filter.isEmpty())
				filter = " WHERE " + extraFilter;
			else
				filter += " AND " + extraFilter;
		}
		return super.readAll(isLazy, filter);
	}

	@Override
	public int count(String filter) throws SQLException
	{
		String extraFilter = getLocalizationFilter();
		if (!extraFilter.isEmpty())
		{
			if (filter == null || filter.isEmpty())
				filter = " WHERE " + extraFilter;
			else
				filter += " AND " + extraFilter;
		}
		return super.count(filter);
	}

	private String getLocalizationFilter() throws SQLException
	{
		String localizationFilter = "";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT role FROM account WHERE account.login = CURRENT_USER");
		if (rs.next())
		{
			Account.Roles role = Enum.valueOf(Account.Roles.class, rs.getString(1));
			if (role == Account.Roles.JUSTUSER || role == Account.Roles.OPERATOR)
			{
				rs = stmt.executeQuery("SELECT department_id FROM employee WHERE employee.id = "
						+ "(SELECT employee_id FROM account WHERE login = CURRENT_USER)");
				rs.next();
				localizationFilter = "department_id = " + rs.getString(1);
			}
		}
		return localizationFilter;
	}
}
