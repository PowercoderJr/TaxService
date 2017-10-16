package TaxService;

import TaxService.CRUDs.*;
import TaxService.DAO.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Date;
import java.util.List;

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

		/*Department newdep = new Department("Kekskoe", (short)12, "+380551231212", "Harcizsk", "Lolosheva", "11");
		departmentCRUD.create(newdep);
		List<Department> list = departmentCRUD.findAll();
		System.out.println("Такие дела:");
		for (Department dep : list)
			System.out.println(dep);

		Employee newemp = new Employee("Pipiskovich", "Ololosh", "Ivanovich", new Date(1990, 5, 10), "Super-bomj", 100);
		employeeCRUD.create(newemp);*/

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
