package TaxService.CRUDs;

import TaxService.DAO.*;
import TaxService.RandomHelper;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class PaymentCRUD extends AbstractRandomableCRUD<Payment>
{
	public PaymentCRUD(SessionFactory factory)
	{
		super(factory, Payment.class);
	}

	@Override
	protected Payment generateRandomBean()
	{
		PaytypeCRUD paytypeCRUD = new PaytypeCRUD(factory);
		List<Paytype> paytypes = paytypeCRUD.getAll();
		Paytype paytype = paytypes.get((int) (Math.random() * paytypes.size()));
		paytypeCRUD.disconnect();

		Date date = RandomHelper.getRandomDateBetween(LocalDate.of(2000, 1, 1), LocalDate.now());

		BigDecimal amount = BigDecimal.valueOf(Math.random() * 100000);

		EmployeeCRUD employeeCRUD = new EmployeeCRUD(factory);
		List<Employee> employees = employeeCRUD.getAll();
		Employee employee = employees.get((int) (Math.random() * employees.size()));
		employeeCRUD.disconnect();

		DepartmentCRUD departmentCRUD = new DepartmentCRUD(factory);
		List<Department> departments = departmentCRUD.getAll();
		Department department = departments.get((int) (Math.random() * departments.size()));
		departmentCRUD.disconnect();

		CompanyCRUD companyCRUD = new CompanyCRUD(factory);
		List<Company> companies = companyCRUD.getAll();
		Company company = companies.get((int) (Math.random() * companies.size()));
		companyCRUD.disconnect();

		return new Payment(paytype, date, amount, employee, department, company);
	}
}
