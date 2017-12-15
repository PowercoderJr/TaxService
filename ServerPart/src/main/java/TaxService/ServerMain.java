package TaxService;

import TaxService.CRUDs.*;
import TaxService.DAOs.*;
import org.reflections.Reflections;

import java.sql.*;
import java.util.Set;

public class ServerMain
{
	public static void main(String[] args)
	{
		boolean fromScratch = true;

		System.out.println("From Scratch Mode is " + (fromScratch ? "ON" : "OFF"));
		if (fromScratch)
		{
			try (Connection superConnection = DriverManager.getConnection("jdbc:postgresql://localhost/TaxService", "postgres", "userpass"))
			{
				long timeZero = System.currentTimeMillis();
				dropNcreate(superConnection);
				System.out.println("Dropping'n'creating completed in " + (System.currentTimeMillis() - timeZero) / 1000 + " seconds");

				timeZero = System.currentTimeMillis();
				DepartmentCRUD departmentCRUD = new DepartmentCRUD(superConnection);
				departmentCRUD.insertRandomBeans(10000);
				System.out.println("Departments generation completed in " + (System.currentTimeMillis() - timeZero) / 1000 + " seconds");

				timeZero = System.currentTimeMillis();
				EmployeeCRUD employeeCRUD = new EmployeeCRUD(superConnection);
				employeeCRUD.insertRandomBeans(10000);
				System.out.println("Employees generation completed in " + (System.currentTimeMillis() - timeZero) / 1000 + " seconds");

				timeZero = System.currentTimeMillis();
				CompanyCRUD companyCRUD = new CompanyCRUD(superConnection);
				companyCRUD.insertRandomBeans(10000);
				System.out.println("Companies generation completed in " + (System.currentTimeMillis() - timeZero) / 1000 + " seconds");

				timeZero = System.currentTimeMillis();
				PaymentCRUD paymentCRUD = new PaymentCRUD(superConnection);
				paymentCRUD.insertRandomBeans(10000);
				System.out.println("Payments generation completed in " + (System.currentTimeMillis() - timeZero) / 1000 + " seconds");
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
			stmt.executeUpdate("create table department(id serial not null,deptype_id int8 not null references deptype(id),name varchar(100) not null,startyear numeric(4,0) not null,phone varchar(13) not null,city varchar(30) not null,street varchar(30) not null,house varchar(6) not null,primary key(id))");
			stmt.executeUpdate("create table employee(id serial not null,surname varchar(30) not null,name varchar(30) not null,patronymic varchar(30) not null,department_id int8 not null references department(id),birthdate date not null,post_id int8 not null references post(id),salary int4 not null,education_id int8 not null references education(id),primary key(id))");
			stmt.executeUpdate("create table company(id serial not null,name varchar(100) not null,owntype_id serial not null references owntype(id),phone varchar(13) not null,startyear numeric(4,0) not null,statesize int4 not null,primary key(id))");
			stmt.executeUpdate("create table payment(id serial not null,paytype_id int8 not null references paytype(id),date date not null,amount numeric(12,2) not null,employee_id int8 not null references employee(id),department_id int8 not null references department(id),company_id int8 not null references company(id),primary key(id))");
		}
	}
}
