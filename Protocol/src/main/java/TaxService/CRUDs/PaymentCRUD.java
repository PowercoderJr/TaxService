package TaxService.CRUDs;

import TaxService.DAOs.*;
import TaxService.RandomHelper;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class PaymentCRUD extends AbstractRandomableCRUD<Payment>
{
	public PaymentCRUD(Connection connection, Connection superConnection)
	{
		super(connection, superConnection, Payment.class);
	}

	@Override
	protected Payment generateRandomBean() throws SQLException
	{
		PaytypeCRUD paytypeCRUD = new PaytypeCRUD(connection, superConnection);
		Paytype paytype = paytypeCRUD.readRandom(true, null);

		CompanyCRUD companyCRUD = new CompanyCRUD(connection, superConnection);
		Company company = companyCRUD.readRandom(false, null);

		Date date = RandomHelper.getRandomDateBetween(LocalDate.of(company.getStartyear().intValue(), 1, 1), LocalDate.now());

		BigDecimal amount = BigDecimal.valueOf(rnd.nextDouble() * 100000);

		EmployeeCRUD employeeCRUD = new EmployeeCRUD(connection, superConnection);
		Employee employee = employeeCRUD.readRandom(true, null);

		DepartmentCRUD departmentCRUD = new DepartmentCRUD(connection, superConnection);
		Department department = departmentCRUD.readRandom(true, null);

		return new Payment(paytype, date, amount, employee, department, company);
	}
}
