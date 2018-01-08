package TaxService.CRUDs;

import TaxService.DAOs.*;
import TaxService.RandomHelper;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class PaymentCRUD extends AbstractRandomableCRUD<Payment>
{
	public PaymentCRUD(Connection connection)
	{
		super(connection, Payment.class);
	}

	@Override
	protected Payment generateRandomBean() throws SQLException
	{
		PaytypeCRUD paytypeCRUD = new PaytypeCRUD(connection);
		Paytype paytype = paytypeCRUD.readRandom(true);

		CompanyCRUD companyCRUD = new CompanyCRUD(connection);
		Company company = companyCRUD.readRandom(false);

		Date date = RandomHelper.getRandomDateBetween(LocalDate.of(company.getStartyear().intValue(), 1, 1), LocalDate.now());

		BigDecimal amount = BigDecimal.valueOf(rnd.nextDouble() * 100000);

		EmployeeCRUD employeeCRUD = new EmployeeCRUD(connection);
		Employee employee = employeeCRUD.readRandom(true);

		DepartmentCRUD departmentCRUD = new DepartmentCRUD(connection);
		Department department = departmentCRUD.readRandom(true);

		return new Payment(paytype, date, amount, employee, department, company);
	}

	@Override
	public List<Payment> readPortion(int portion, boolean isLazy, String filter) throws SQLException
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
	public List<Payment> readAll(boolean isLazy, String filter) throws SQLException
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
