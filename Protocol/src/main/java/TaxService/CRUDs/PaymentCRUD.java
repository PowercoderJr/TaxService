package TaxService.CRUDs;

import TaxService.DAOs.*;
import TaxService.RandomHelper;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

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
		Paytype paytype = paytypeCRUD.readRandom();

		CompanyCRUD companyCRUD = new CompanyCRUD(connection);
		Company company = companyCRUD.readRandom();

		Date date = RandomHelper.getRandomDateBetween(LocalDate.of(company.getStartyear().intValue(), 1, 1), LocalDate.now());

		BigDecimal amount = BigDecimal.valueOf(rnd.nextDouble() * 100000);

		EmployeeCRUD employeeCRUD = new EmployeeCRUD(connection);
		Employee employee = employeeCRUD.readRandom();

		DepartmentCRUD departmentCRUD = new DepartmentCRUD(connection);
		Department department = departmentCRUD.readRandom();

		return new Payment(paytype, date, amount, employee, department, company);
	}

	@Override
	public Payment readLazy(long id) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT id FROM company WHERE id = " + id);
			if (rs.next())
			{
				Payment result = new Payment();
				result.id = id;
				return result;
			}
			else
				return null;
		}
	}
}
