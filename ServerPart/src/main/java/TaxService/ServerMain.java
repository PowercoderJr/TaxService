package TaxService;

import TaxService.DAO.Department;
import TaxService.Services.DepartmentService;
import TaxService.Services.Service;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ServerMain
{
	public static void main( String[] args )
	{
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Service<Department> departmentService = new DepartmentService(sessionFactory);

//		Department dep = new Department("Kekskoe", (short)12, "+380551231212", "Enakievo", "Lolosheva", "11");
//		departmentService.create(dep);
//		List<Department> list = departmentService.findAll();
//		System.out.println("Такие дела:");
//		for (Department dep : list)
//			System.out.println(dep);

		departmentService.disconnect();

		sessionFactory.close();
		System.out.println( "Hello, I'm Server!\nBAC 3AAPEWTOBAHO!" );
	}
}