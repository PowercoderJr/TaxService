package TaxService;

import TaxService.CRUDs.*;
import TaxService.DAO.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class ServerMain
{
	public static void main( String[] args )
	{
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		AbstractCRUD<Department> departmentCRUD = new DepartmentCRUD(sessionFactory);
		AbstractCRUD<Employee> employeeCRUD = new EmployeeCRUD(sessionFactory);
		AbstractCRUD<Company> companyCRUD = new CompanyCRUD(sessionFactory);
		AbstractCRUD<Payment> paymentCRUD = new PaymentCRUD(sessionFactory);
		AbstractCRUD<Deptype> deptypeCRUD = new DeptypeCRUD(sessionFactory);
		AbstractCRUD<Education> educationCRUD = new EducationCRUD(sessionFactory);
		AbstractCRUD<Paytype> paytypeCRUD = new PaytypeCRUD(sessionFactory);
		AbstractCRUD<Owntype> owntypeCRUD = new OwntypeCRUD(sessionFactory);

		Deptype deptype = new Deptype("Областной");
		deptypeCRUD.create(deptype);
		Education education = new Education("Высшее");
		educationCRUD.create(education);
		Paytype paytype = new Paytype("Штраф");
		paytypeCRUD.create(paytype);
		Owntype owntype = new Owntype("ОАО");
		owntypeCRUD.create(owntype);
		Department department = new Department("Питерское", deptype, new BigDecimal(1986), "+380631232323", "Питер", "Кашева", "11a");
		departmentCRUD.create(department);
		Employee employee = new Employee("Демьянов", "Константин", "Владиславович", department, Date.valueOf(LocalDate.of(1974, 12, 13)), "Главный самый", 60000, education);
		employeeCRUD.create(employee);
		Company company = new Company("Крендельная", owntype, "+380994564645", new BigDecimal(2004), 20);
		companyCRUD.create(company);
		Payment payment = new Payment(paytype, Date.valueOf(LocalDate.of(2017, 5, 6)), new BigDecimal(1500.00), employee, department, company);
		paymentCRUD.create(payment);

		departmentCRUD.disconnect();
		employeeCRUD.disconnect();
		companyCRUD.disconnect();
		paymentCRUD.disconnect();
		deptypeCRUD.disconnect();
		educationCRUD.disconnect();
		paytypeCRUD.disconnect();
		owntypeCRUD.disconnect();
		sessionFactory.close();
		System.out.println( "Hello, I'm Server!\nBAC 3AAPEWTOBAHO!" );
	}
}
