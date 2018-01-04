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
				departmentCRUD.insertRandomBeans(1000);
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

				setupTriggers(superConnection);
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
				stmt.executeUpdate("CREATE TABLE " + item.getSimpleName().toLowerCase() + "(id serial not null,name varchar(100) not null unique,primary key(id))");
				AbstractRefCRUD crud = (AbstractRefCRUD) ServerAgent.getInstance().getCrudForClass(item, connection);
				crud.fillFromSource();
			}
			//Создание таблиц
			String execMe;
			execMe  = "create table department("
					+ "id serial primary key,"
					+ "deptype_id int8 not null references deptype(id) on delete cascade,"
					+ "name varchar(100) not null,"
					+ "startyear numeric(4,0) not null,"
					+ "phone varchar(17) not null unique,"
					+ "city_id int8 not null references city(id) on delete cascade,"
					+ "street varchar(30) not null,"
					+ "house varchar(6) not null)";
			stmt.executeUpdate(execMe);

			execMe  = "create table employee("
					+ "id serial primary key,"
					+ "surname varchar(30) not null,"
					+ "name varchar(30) not null,"
					+ "patronymic varchar(30) not null,"
					+ "department_id int8 not null references department(id) on delete cascade,"
					+ "birthdate date not null,"
					+ "post_id int8 not null references post(id),"
					+ "salary int4 not null,"
					+ "education_id int8 not null references education(id) on delete cascade)";
			stmt.executeUpdate(execMe);

			execMe  = "create table company("
					+ "id serial primary key,"
					+ "name varchar(100) not null,"
					+ "owntype_id serial not null references owntype(id) on delete cascade,"
					+ "phone varchar(17) not null,"
					+ "startyear numeric(4,0) not null,"
					+ "statesize int4 not null)";
			stmt.executeUpdate(execMe);

			execMe  = "create table payment("
					+ "id serial primary key,"
					+ "paytype_id int8 not null references paytype(id) on delete cascade,"
					+ "date date not null,"
					+ "amount numeric(12,2) not null,"
					+ "employee_id int8 not null references employee(id) on delete cascade,"
					+ "department_id int8 not null references department(id) on delete cascade,"
					+ "company_id int8 not null references company(id) on delete cascade)";
			stmt.executeUpdate(execMe);

			//Назначение индексов
			execMe  = "create index emp_fullname_idx on employee (surname, name, patronymic)";
			stmt.executeUpdate(execMe);

			//Создание представлений и хранимых процедур
			execMe  = "CREATE FUNCTION query_1_1 (x int)\n"
					+ "RETURNS TABLE(a int, b text, c numeric, d date, e int, f text, g text) AS $$\n"
					+ "\tselect payment.id, paytype.name, payment.amount, payment.date, company.id, company.name, company.phone\n"
					+ "\t\tfrom payment\n"
					+ "\t\tinner join paytype on payment.paytype_id = paytype.id\n"
					+ "\t\tinner join employee on payment.employee_id = employee.id\n"
					+ "\t\tinner join company on payment.company_id = company.id\n"
					+ "\t\twhere employee.id = $1\n"
					+ "\t\torder by 1"
					+ "$$ LANGUAGE SQL;";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE FUNCTION query_1_2 (x text)\n"
					+ "RETURNS TABLE(a int, b text, c numeric, d date, e int, f text, g text, h text, i int, j text, k text) AS $$\n"
					+ "\tselect payment.id, paytype.name, payment.amount, payment.date, employee.id, employee.surname, employee.name, employee.patronymic, company.id, company.name, company.phone\n"
					+ "\t\tfrom payment\n"
					+ "\t\tinner join paytype on payment.paytype_id = paytype.id\n"
					+ "\t\tinner join employee on payment.employee_id = employee.id\n"
					+ "\t\tinner join company on payment.company_id = company.id\n"
					+ "\t\twhere payment.date >= $1::date\n"
					+ "\t\torder by payment.date"
					+ "$$ LANGUAGE SQL;";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE FUNCTION query_1_3 (x1 int, x2 text)\n"
					+ "RETURNS TABLE(a int, b text, c numeric, d date, e int, f text, g text) AS $$\n"
					+ "\tselect payment.id, paytype.name, payment.amount, payment.date, company.id, company.name, company.phone\n"
					+ "\t\tfrom payment\n"
					+ "\t\tinner join paytype on payment.paytype_id = paytype.id\n"
					+ "\t\tinner join employee on payment.employee_id = employee.id\n"
					+ "\t\tinner join company on payment.company_id = company.id\n"
					+ "\t\twhere employee.id = $1 AND payment.date >= $2::date\n"
					+ "\t\torder by payment.date"
					+ "$$ LANGUAGE SQL;";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE VIEW query_2_1 AS\n"
					+ "\tselect department.id as department_id, department.name as department_name, deptype.name as deptype_name,"
					+ " city.name as city_name, employee.surname as employee_surname, employee.name as employee_name,"
					+ " employee.patronymic as employee_patronymic, post.name as post_name, employee.salary as employee_salary \n"
					+ "\t\tfrom department\n"
					+ "\t\tinner join deptype on department.deptype_id = deptype.id\n"
					+ "\t\tinner join city on department.city_id = city.id\n"
					+ "\t\tinner join employee on department.id = employee.department_id\n"
					+ "\t\tinner join post on employee.post_id = post.id\n"
					+ "\t\torder by 1, 5";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE VIEW query_3 AS\n"
					+ "\tselect company.id, company.name, max(payment.date)\n"
					+ "\t\tfrom company\n"
					+ "\t\tleft join payment on company.id = payment.company_id\n"
					+ "\t\tgroup by company.id\n"
					+ "\t\torder by 2";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE VIEW query_6 AS\n"
					+ "\tselect avg(statesize)\n"
					+ "\t\tfrom company";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE VIEW query_7 AS\n"
					+ "\tselect education.name as education_name, post.name as post_name, count(*)\n"
					+ "\t\tfrom employee\n"
					+ "\t\tinner join post on employee.post_id = post.id\n"
					+ "\t\tinner join education on employee.education_id = education.id\n"
					+ "\t\tgroup by cube((education.id, education.name), (post.id, post.name))\n"
					+ "\t\torder by education.id, post.id";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE FUNCTION query_8_1 (x1 int)\n"
					+ "RETURNS TABLE(a numeric) AS $$\n"
					+ "\tselect avg(employee.salary)\n"
					+ "\t\tfrom employee\n"
					+ "\t\tinner join post on employee.post_id = post.id\n"
					+ "\t\twhere post.id = $1"
					+ "$$ LANGUAGE SQL";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE FUNCTION query_8_2 (x1 text)\n"
					+ "RETURNS TABLE(a int8) AS $$\n"
					+ "\tselect count(*)\n"
					+ "\t\tfrom department\n"
					+ "\t\twhere phone like '+38(' || $1 || ')%'"
					+ "$$ LANGUAGE SQL";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE FUNCTION query_9 (x1 numeric)\n"
					+ "RETURNS TABLE(a int, b text, c numeric) AS $$\n"
					+ "\tselect company.id, company.name, sum(payment.amount)\n"
					+ "\t\tfrom payment\n"
					+ "\t\tinner join company on payment.company_id = company.id\n"
					+ "\t\tgroup by company.id\n"
					+ "\t\thaving sum(payment.amount) < $1\n"
					+ "\t\torder by 3 desc"
					+ "$$ LANGUAGE SQL";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE FUNCTION query_10 (x1 int, x2 numeric)\n"
					+ "RETURNS TABLE(a int, b text, c numeric) AS $$\n"
					+ "\tselect company.id, company.name, sum(payment.amount)\n"
					+ "\t\tfrom payment\n"
					+ "\t\tinner join company on payment.company_id = company.id\n"
					+ "\t\twhere company.owntype_id = $1\n"
					+ "\t\tgroup by company.id\n"
					+ "\t\thaving sum(payment.amount) < $2\n"
					+ "\t\torder by 3 desc"
					+ "$$ LANGUAGE SQL";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE FUNCTION query_12 (x1 int)\n"
					+ "RETURNS TABLE(a text, b text) AS $$\n"
					+ "\tselect name, phone\n"
					+ "\t\tfrom department\n"
					+ "\t\twhere startyear = $1\n"
					+ "\tunion\n"
					+ "\tselect name, phone\n"
					+ "\t\tfrom company\n"
					+ "\t\twhere startyear = $1\n"
					+ "\t\torder by 1"
					+ "$$ LANGUAGE SQL";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE FUNCTION query_13_1 (x1 text)\n"
					+ "RETURNS TABLE(a int, b text, c int8, d numeric) AS $$\n"
					+ "\tselect company.id, company.name, count(*), sum(payment.amount)\n"
					+ "\t\tfrom company\n"
					+ "\t\tinner join payment on company.id = payment.company_id\n"
					+ "\t\twhere company.id in \n"
					+ "\t\t(\n"
					+ "\t\t\tselect company.id\n"
					+ "\t\t\t\tfrom company\n"
					+ "\t\t\t\tinner join payment on company.id = payment.company_id\n"
					+ "\t\t\t\twhere payment.date >= $1::date\n"
					+ "\t\t)\n"
					+ "\t\tgroup by company.id\n"
					+ "\t\torder by 3 desc, 4 desc"
					+ "$$ LANGUAGE SQL";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE FUNCTION query_13_2 (x1 text)\n"
					+ "RETURNS TABLE(a int, b text) AS $$\n"
					+ "\tselect company.id, company.name\n"
					+ "\t\tfrom company\n"
					+ "\t\twhere company.id not in \n"
					+ "\t\t(\n"
					+ "\t\t\tselect company.id\n"
					+ "\t\t\t\tfrom company\n"
					+ "\t\t\t\tinner join payment on company.id = payment.company_id\n"
					+ "\t\t\t\twhere payment.date >= $1::date\n"
					+ "\t\t)\n"
					+ "\t\tgroup by company.id\n"
					+ "\t\torder by 1"
					+ "$$ LANGUAGE SQL";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE VIEW query_13_3 AS\n"
					+ "\tselect category::text, count(*) as stats from\n"
					+ "\t\t(select case\n"
					+ "\t\t\t\twhen age(current_date, birthdate) <= '20 years'::interval then 'Юношеский'\n"
					+ "\t\t\t\twhen age(current_date, birthdate) > '60 years'::interval then 'Пожилой'\n"
					+ "\t\t\t\telse 'Средний' end\n"
					+ "\t\t\tfrom employee) as category\n"
					+ "\t\tgroup by category\n"
					+ "\t\torder by stats desc";
			stmt.executeUpdate(execMe);

			/*execMe  = "CREATE VIEW query_ AS\n"
					+ "\t";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE VIEW query_ AS\n"
					+ "\t";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE VIEW query_ AS\n"
					+ "\t";
			stmt.executeUpdate(execMe);*/
		}
	}

	private static void setupTriggers(Connection connection) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			String execMe;
			execMe  = "create function setPaymentDateToday() returns trigger as $$\n"
					+ "begin\n"
					+ "new.date = current_date;\n"
					+ "return new;\n"
					+ "end;\n"
					+ "$$ LANGUAGE 'plpgsql';";
			stmt.executeUpdate(execMe);

			execMe  = "create trigger setPaymentDateToday before insert on payment "
					+ "for each row execute procedure setPaymentDateToday()";
			stmt.executeUpdate(execMe);
		}
	}
}
