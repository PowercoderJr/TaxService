package TaxService;

import TaxService.CRUDs.*;
import TaxService.DAO.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Scanner;

public class ServerMain
{
	public static void main( String[] args )
	{
		try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory())
		{
			AbstractCRUD<StrangeThing> strangeThingCRUD = new StrangeThingCRUD(sessionFactory);
			AbstractRandomableCRUD<Department> departmentCRUD = new DepartmentCRUD(sessionFactory);
			AbstractRandomableCRUD<Employee> employeeCRUD = new EmployeeCRUD(sessionFactory);
			AbstractRandomableCRUD<Company> companyCRUD = new CompanyCRUD(sessionFactory);
			AbstractRandomableCRUD<Payment> paymentCRUD = new PaymentCRUD(sessionFactory);
			AbstractCRUD<Deptype> deptypeCRUD = new DeptypeCRUD(sessionFactory);
			AbstractCRUD<Education> educationCRUD = new EducationCRUD(sessionFactory);
			AbstractCRUD<Paytype> paytypeCRUD = new PaytypeCRUD(sessionFactory);
			AbstractCRUD<Owntype> owntypeCRUD = new OwntypeCRUD(sessionFactory);

			StrangeThing thing = new StrangeThing("admin", DigestUtils.sha256Hex(DigestUtils.sha256Hex("pass")), StrangeThing.Role.ADMIN);
			strangeThingCRUD.create(thing);
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

			departmentCRUD.insertRandomBeans(10);
			employeeCRUD.insertRandomBeans(10);
			companyCRUD.insertRandomBeans(10);
			paymentCRUD.insertRandomBeans(10);

			strangeThingCRUD.disconnect();
			departmentCRUD.disconnect();
			employeeCRUD.disconnect();
			companyCRUD.disconnect();
			paymentCRUD.disconnect();
			deptypeCRUD.disconnect();
			educationCRUD.disconnect();
			paytypeCRUD.disconnect();
			owntypeCRUD.disconnect();
		}

		/*Scanner in = new Scanner(System.in);
		System.out.println("Who's there?");
		boolean acceed = false;
		String login, fineLogin, pass, finePass;
		login = in.nextLine();
		pass = in.nextLine();
		try (BufferedReader f = new BufferedReader(new FileReader("src/main/java/TaxService/strangefile")))
		{
			fineLogin = f.readLine();
			finePass = f.readLine();
			if (login.compareTo(fineLogin) == 0 && DigestUtils.md5Hex(pass).compareTo(finePass) == 0)
				acceed = true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (acceed)
			System.out.println("Hello, I'm Server!\nBAC 3AAPEWTOBAHO!");
		else
			System.out.println("Get out!");*/
		System.out.println("Hello, I'm Server!\nBAC 3AAPEWTOBAHO!");
	}
}
