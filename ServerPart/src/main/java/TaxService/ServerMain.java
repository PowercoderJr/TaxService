package TaxService;

import TaxService.CRUDs.*;
import TaxService.DAOs.*;
import org.reflections.Reflections;

import java.sql.*;
import java.util.Set;

public class ServerMain
{
	private static final boolean fromScratch = false;

	public static void main(String[] args)
	{
		System.out.println("From Scratch Mode is " + (fromScratch ? "ON" : "OFF"));
		if (fromScratch)
		{
			try (Connection superConnection = DriverManager.getConnection("jdbc:postgresql://localhost/TaxService", "postgres", "userpass"))
			{
				long timeZero = System.currentTimeMillis();
				dropNcreate(superConnection);
				System.out.println("Dropping'n'creating completed in " + (System.currentTimeMillis() - timeZero) + " ms");

				timeZero = System.currentTimeMillis();
				DepartmentCRUD departmentCRUD = new DepartmentCRUD(superConnection);
				departmentCRUD.insertRandomBeans(10);
				System.out.println("Departments generation completed in " + (System.currentTimeMillis() - timeZero) + " ms");

				timeZero = System.currentTimeMillis();
				EmployeeCRUD employeeCRUD = new EmployeeCRUD(superConnection);
				employeeCRUD.insertRandomBeans(1000);
				System.out.println("Employees generation completed in " + (System.currentTimeMillis() - timeZero) + " ms");

				timeZero = System.currentTimeMillis();
				CompanyCRUD companyCRUD = new CompanyCRUD(superConnection);
				companyCRUD.insertRandomBeans(1000);
				System.out.println("Companies generation completed in " + (System.currentTimeMillis() - timeZero) + " ms");

				timeZero = System.currentTimeMillis();
				PaymentCRUD paymentCRUD = new PaymentCRUD(superConnection);
				paymentCRUD.insertRandomBeans(1000);
				System.out.println("Payments generation completed in " + (System.currentTimeMillis() - timeZero) + " ms");
				
				departmentCRUD.delete(2);
				departmentCRUD.delete(22);
				departmentCRUD.delete(222);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			ServerAgent.getInstance().close();
		}
		else
		{
			ServerAgent.getInstance();
		}

		System.out.println("BAC 3AAPEWTOBAHO!");
	}

	private static void dropNcreate(Connection connection) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			//Пересоздание схемы
			stmt.executeUpdate("DROP SCHEMA IF EXISTS public CASCADE");
			stmt.executeUpdate("CREATE SCHEMA public");
			stmt.executeUpdate("GRANT ALL ON SCHEMA public TO postgres");
			stmt.executeUpdate("GRANT ALL ON SCHEMA public TO public"); //???

			//Создание таблиц-справочников
			Reflections reflections = new Reflections("TaxService.DAOs");
			Set<Class<? extends AbstractRefDAO>> refTables = reflections.getSubTypesOf(AbstractRefDAO.class);
			for (Class<? extends AbstractRefDAO> item : refTables)
			{
				stmt.executeUpdate("CREATE TABLE " + item.getSimpleName().toLowerCase() + "(id serial not null,name varchar(100) not null,primary key(id))");
				AbstractRefCRUD crud = (AbstractRefCRUD) ServerAgent.getInstance().getCrudForClass(item, connection);
				crud.fillFromSource();
			}
			//Создание таблиц
			String execMe;
			execMe  = "create table department(";
			execMe += "id serial primary key,";
			execMe += "deptype_id int8 not null references deptype(id) on delete cascade,";
			execMe += "name varchar(100) not null,";
			execMe += "startyear numeric(4,0) not null,";
			execMe += "phone varchar(13) not null,";
			execMe += "city varchar(30) not null,";
			execMe += "street varchar(30) not null,";
			execMe += "house varchar(6) not null)";
			stmt.executeUpdate(execMe);

			execMe  = "create table employee(";
			execMe += "id serial primary key,";
			execMe += "surname varchar(30) not null,";
			execMe += "name varchar(30) not null,";
			execMe += "patronymic varchar(30) not null,";
			execMe += "department_id int8 not null references department(id) on delete cascade,";
			execMe += "birthdate date not null,";
			execMe += "post_id int8 not null references post(id),";
			execMe += "salary int4 not null,";
			execMe += "education_id int8 not null references education(id) on delete cascade)";
			stmt.executeUpdate(execMe);

			execMe  = "create table company(";
			execMe += "id serial primary key,";
			execMe += "name varchar(100) not null,";
			execMe += "owntype_id serial not null references owntype(id) on delete cascade,";
			execMe += "phone varchar(13) not null,";
			execMe += "startyear numeric(4,0) not null,";
			execMe += "statesize int4 not null)";
			stmt.executeUpdate(execMe);

			execMe  = "create table payment(";
			execMe += "id serial primary key,";
			execMe += "paytype_id int8 not null references paytype(id) on delete cascade,";
			execMe += "date date not null,";
			execMe += "amount numeric(12,2) not null,";
			execMe += "employee_id int8 not null references employee(id) on delete cascade,";
			execMe += "department_id int8 not null references department(id) on delete cascade,";
			execMe += "company_id int8 not null references company(id) on delete cascade)";
			stmt.executeUpdate(execMe);
		}
	}
}
