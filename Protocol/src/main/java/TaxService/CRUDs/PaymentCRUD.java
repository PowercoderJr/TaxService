package TaxService.CRUDs;

import TaxService.DAOs.*;
import TaxService.RandomHelper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;

public class PaymentCRUD extends AbstractRandomableCRUD<Payment>
{
	public PaymentCRUD(Connection connection)
	{
		super(connection, Payment.class);
	}

	@Override
	protected Payment generateRandomBean()
	{
		PaytypeCRUD paytypeCRUD = new PaytypeCRUD(factory);
		Paytype paytype = paytypeCRUD.getRandom();
		paytypeCRUD.disconnect();

		CompanyCRUD companyCRUD = new CompanyCRUD(factory);
		Company company = companyCRUD.getRandom();
		companyCRUD.disconnect();

		Date date = RandomHelper.getRandomDateBetween(LocalDate.of(company.getStartyear().intValue(), 1, 1), LocalDate.now());

		BigDecimal amount = BigDecimal.valueOf(Math.random() * 100000);

		EmployeeCRUD employeeCRUD = new EmployeeCRUD(factory);
		Employee employee = employeeCRUD.getRandom();
		employeeCRUD.disconnect();

		DepartmentCRUD departmentCRUD = new DepartmentCRUD(factory);
		Department department = departmentCRUD.getRandom();
		departmentCRUD.disconnect();

		return new Payment(paytype, date, amount, employee, department, company);
	}
}
