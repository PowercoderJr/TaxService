package TaxService;

import TaxService.CRUDs.*;
import TaxService.DAOs.*;
import org.reflections.Reflections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
				DepartmentCRUD departmentCRUD = new DepartmentCRUD(superConnection, superConnection);
				departmentCRUD.insertRandomBeans(100);
				System.out.println("Departments generation completed in " + (System.currentTimeMillis() - timeZero) + " ms");

				timeZero = System.currentTimeMillis();
				EmployeeCRUD employeeCRUD = new EmployeeCRUD(superConnection, superConnection);
				employeeCRUD.insertRandomBeans(1000);
				System.out.println("Employees generation completed in " + (System.currentTimeMillis() - timeZero) + " ms");

				timeZero = System.currentTimeMillis();
				CompanyCRUD companyCRUD = new CompanyCRUD(superConnection, superConnection);
				companyCRUD.insertRandomBeans(100);
				System.out.println("Companies generation completed in " + (System.currentTimeMillis() - timeZero) + " ms");

				timeZero = System.currentTimeMillis();
				PaymentCRUD paymentCRUD = new PaymentCRUD(superConnection, superConnection);
				paymentCRUD.insertRandomBeans(10000);
				System.out.println("Payments generation completed in " + (System.currentTimeMillis() - timeZero) + " ms");

				/*departmentCRUD.delete(2);
				departmentCRUD.delete(22);
				departmentCRUD.delete(222);*/

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
				AbstractRefCRUD crud = (AbstractRefCRUD) ServerAgent.getInstance().getCrudForClass(item, connection, connection);
				crud.fillFromSource();
			}
			//Создание таблиц
			String execMe;
			execMe  = "create table department("
					+ "id serial primary key,"
					+ "name varchar(100) not null,"
					+ "deptype_id int8 not null references deptype(id) on delete restrict,"
					+ "startyear numeric(4,0) not null check (startyear <= date_part('year', current_date)),"
					+ "phone varchar(17) not null unique,"
					+ "city_id int8 not null references city(id) on delete restrict,"
					+ "street varchar(30) not null,"
					+ "house varchar(6) not null)";
			stmt.executeUpdate(execMe);

			execMe  = "create table employee("
					+ "id serial primary key,"
					+ "surname varchar(30) not null,"
					+ "name varchar(30) not null,"
					+ "patronymic varchar(30) not null,"
					+ "department_id int8 not null references department(id) on delete cascade,"
					+ "birthdate date not null check (birthdate <= current_date),"
					+ "post_id int8 not null references post(id),"
					+ "salary int4 not null check (salary > 0),"
					+ "education_id int8 not null references education(id) on delete restrict)";
			stmt.executeUpdate(execMe);

			execMe  = "create table company("
					+ "id serial primary key,"
					+ "name varchar(100) not null,"
					+ "owntype_id serial not null references owntype(id) on delete restrict,"
					+ "phone varchar(17) not null,"
					+ "startyear numeric(4,0) not null check (startyear <= date_part('year', current_date)),"
					+ "statesize int4 not null check (statesize > 0))";
			stmt.executeUpdate(execMe);

			execMe  = "create table payment("
					+ "id serial primary key,"
					+ "paytype_id int8 not null references paytype(id) on delete restrict,"
					+ "date date not null,"
					+ "amount numeric(12,2) not null check (amount > 0),"
					+ "employee_id int8 not null references employee(id) on delete restrict,"
					+ "department_id int8 not null references department(id) on delete restrict,"
					+ "company_id int8 not null references company(id) on delete restrict)";
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
					+ "RETURNS TABLE(a text, b text, c text) AS $$\n"
					+ "\tselect 'Отделение налоговой инспекции' as orgtype, name, phone\n"
					+ "\t\tfrom department\n"
					+ "\t\twhere startyear = $1\n"
					+ "\tunion\n"
					+ "\tselect 'Предприятие' as orgtype, name, phone\n"
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
					+ "\tselect category.caption, count(*) as stats from\n"
					+ "\t\t(select case\n"
					+ "\t\t\t\twhen age(current_date, birthdate) <= '20 years'::interval then 'Юношеский (до 20)'\n"
					+ "\t\t\t\twhen age(current_date, birthdate) > '60 years'::interval then 'Пожилой (от 61)'\n"
					+ "\t\t\t\telse 'Средний (от 21 до 60)' end as caption\n"
					+ "\t\t\tfrom employee) as category\n"
					+ "\t\tgroup by category.caption\n"
					+ "\t\torder by stats desc";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE VIEW query_13_4 AS\n"
					+ "\tselect company_id, company_name, payment_count from\n"
					+ "\t\t(select company.id as company_id, company.name as company_name, count(payment.id) as payment_count\n"
					+ "\t\t\tfrom company\n"
					+ "\t\t\tinner join payment on company.id = payment.company_id\n"
					+ "\t\t\tgroup by company.id) sbqr\n"
					+ "\t\twhere sbqr.payment_count = \n"
					+ "\t\t\t(select max(payment_count) from \n"
					+ "\t\t\t\t(select company.id, company.name as company_name, count(payment.id) as payment_count\n"
					+ "\t\t\t\t\tfrom company\n"
					+ "\t\t\t\t\tinner join payment on company.id = payment.company_id\n"
					+ "\t\t\t\t\tgroup by company.id) \n"
					+ "\t\t\t\tsbsbqr)";
			stmt.executeUpdate(execMe);

			stmt.executeUpdate("REVOKE ALL ON FUNCTION query_1_1 (x int), query_1_2 (x text), query_1_3 (x1 int, x2 text),"
					+ " query_8_1 (x1 int), query_8_2 (x1 text), query_9 (x1 numeric), query_10 (x1 int, x2 numeric), "
					+ "query_12 (x1 int), query_13_1 (x1 text), query_13_2 (x1 text) FROM public");

			/*execMe  = "CREATE VIEW query_ AS\n"
					+ "\t";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE VIEW query_ AS\n"
					+ "\t";
			stmt.executeUpdate(execMe);*/

			//Инструменты управления пользователями
			execMe  = "CREATE TYPE custom_role AS ENUM ('JUSTUSER', 'OPERATOR', 'ADMIN')";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE TABLE account("
					+ "id serial primary key,"
					+ "login varchar(100) not null unique,"
					+ "password varchar(100) not null,"
					+ "employee_id int8 not null unique references employee(id) on delete cascade,"
					+ "role custom_role not null,"
					+ "blocked boolean not null)";
			stmt.executeUpdate(execMe);

			execMe  = "revoke all on all tables in schema public from justuser;"
					+ "revoke all on all sequences in schema public from justuser;"
					+ "revoke all on all functions in schema public from justuser;"
					+ "revoke all on all tables in schema public from operator;"
					+ "revoke all on all sequences in schema public from operator;"
					+ "revoke all on all functions in schema public from operator;"
					+ "revoke all on all tables in schema public from admin;"
					+ "revoke all on all sequences in schema public from admin;"
					+ "revoke all on all functions in schema public from admin;";
			stmt.executeUpdate(execMe);

			execMe  = "SELECT 'DROP ROLE ' || quote_ident(rolname) FROM pg_roles WHERE rolname NOT IN ('postgres', 'pg_signal_backend');";
			ResultSet rs = stmt.executeQuery(execMe);
			List<String> queries = new ArrayList<>();
			while (rs.next())
				queries.add(rs.getString(1));
			for (String query : queries)
				stmt.executeUpdate(query);

			execMe  = "CREATE ROLE justuser;"
					+ "GRANT SELECT ON ALL TABLES IN SCHEMA public TO justuser;"
					+ "GRANT INSERT ON payment TO justuser;"
					+ "GRANT USAGE ON payment_id_seq TO justuser;";
			stmt.executeUpdate(execMe);

			execMe  = "REVOKE ALL ON FUNCTION query_1_1 (x int), query_1_2 (x text), query_1_3 (x1 int, x2 text),"
					+ " query_8_1 (x1 int), query_8_2 (x1 text), query_9 (x1 numeric), query_10 (x1 int, x2 numeric), "
					+ "query_12 (x1 int), query_13_1 (x1 text), query_13_2 (x1 text) FROM justuser";
			stmt.executeUpdate(execMe);

			execMe  = "SELECT 'REVOKE SELECT ON ' || quote_ident(schemaname) || '.' || quote_ident(viewname) || ' FROM justuser;'"
					+ " FROM pg_views WHERE schemaname = 'public';";
			rs = stmt.executeQuery(execMe);
			queries = new ArrayList<>();
			while (rs.next())
				queries.add(rs.getString(1));
			for (String query : queries)
				stmt.executeUpdate(query);

			execMe  = "CREATE ROLE operator;"
					+ "GRANT ALL ON employee, company, deptype, city, post, education, owntype, paytype TO operator;"
					+ "GRANT SELECT, INSERT ON payment TO operator;"
					+ "GRANT SELECT ON department, account TO operator;"
					+ "GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO operator;"
					+ "GRANT ALL ON ALL FUNCTIONS IN SCHEMA public TO operator;";
			stmt.executeUpdate(execMe);

			execMe  = "SELECT 'GRANT SELECT ON ' || quote_ident(schemaname) || '.' || quote_ident(viewname) || ' TO operator;'"
					+ " FROM pg_views WHERE schemaname = 'public';";
			rs = stmt.executeQuery(execMe);
			queries = new ArrayList<>();
			while (rs.next())
				queries.add(rs.getString(1));
			for (String query : queries)
				stmt.executeUpdate(query);

			execMe  = "REVOKE ALL ON FUNCTION query_1_2(text) FROM operator;";
			execMe += "REVOKE ALL ON query_2_1, query_7 FROM operator;";
			stmt.executeUpdate(execMe);

			execMe  = "CREATE ROLE admin WITH CREATEROLE;"
					+ "GRANT ALL ON ALL TABLES IN SCHEMA public TO admin;"
					+ "GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO admin;"
					+ "GRANT ALL ON ALL FUNCTIONS IN SCHEMA public TO admin;";
			stmt.executeUpdate(execMe);

			execMe  = "SELECT 'GRANT SELECT ON ' || quote_ident(schemaname) || '.' || quote_ident(viewname) || ' TO admin;'"
					+ " FROM pg_views WHERE schemaname = 'public';";
			rs = stmt.executeQuery(execMe);queries = new ArrayList<>();
			while (rs.next())
				queries.add(rs.getString(1));
			for (String query : queries)
				stmt.executeUpdate(query);

			//Установка защиты на уровне строк
			execMe  = "CREATE VIEW curr_department AS \n"
					+ "\t(SELECT department_id as id FROM employee WHERE id =\n"
					+ "\t\t(SELECT employee_id FROM account WHERE login = current_user)\n"
					+ "\t);"
					+ "GRANT SELECT ON curr_department TO justuser;"
					+ "GRANT SELECT ON curr_department TO operator;"
					+ "GRANT SELECT ON curr_department TO admin;";
			stmt.executeUpdate(execMe);

			execMe  = "ALTER TABLE employee ENABLE ROW LEVEL SECURITY;\n"
					+ "CREATE POLICY locale_policy ON employee\n"
					+ "\tTO justuser, operator\n"
					+ "\tUSING (department_id =\n"
					+ "\t\t(select id from curr_department)\n"
					+ "\t);"
					+ "CREATE POLICY locale_policy_admin ON employee\n"
					+ "\tTO admin\n"
					+ "\tUSING (true)";
			stmt.executeUpdate(execMe);

			execMe  = "ALTER TABLE payment ENABLE ROW LEVEL SECURITY;\n"
					+ "CREATE POLICY locale_policy ON payment\n"
					+ "\tTO justuser, operator\n"
					+ "\tUSING (department_id =\n"
					+ "\t\t(SELECT id FROM curr_department)\n"
					+ "\t);"
					+ "CREATE POLICY locale_policy_insert ON payment\n"
					+ "\tFOR INSERT\n"
					+ "\tTO justuser, operator\n"
					+ "\tWITH CHECK (true);"
					+ "CREATE POLICY locale_policy_admin ON payment\n"
					+ "\tTO admin\n"
					+ "\tUSING (true);";
			stmt.executeUpdate(execMe);
		}
	}

	private static void setupTriggers(Connection connection) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			String execMe;
			execMe  = "create function autofill_payment() returns trigger as $$\n"
					+ "declare\n"
					+ "\tcurrent_account account%rowtype;\n"
					+ "begin\n"
					+ "\tnew.date = current_date;\n"
					+ "\texecute 'select * from account where login = current_user' into current_account;\n"
					+ "\tnew.employee_id = current_account.employee_id;\n"
					+ "\treturn new;\n"
					+ "end;\n"
					+ "$$ LANGUAGE 'plpgsql';";
			stmt.executeUpdate(execMe);

			execMe  = "create trigger autofill_payment before insert on payment "
					+ "for each row execute procedure autofill_payment()";
			stmt.executeUpdate(execMe);

			execMe  = "create function block_user_instead_of_deleting() returns trigger as $$\n"
					+ "begin\n"
					+ "\told.blocked = true;\n"
					+ "\texecute format('ALTER USER %s WITH NOLOGIN', old.login);\n"
					+ "\treturn null;\n"
					+ "end;\n"
					+ "$$ LANGUAGE 'plpgsql';";
			stmt.executeUpdate(execMe);

			execMe  = "create trigger block_user_instead_of_deleting before delete on account "
					+ "for each row execute procedure block_user_instead_of_deleting()";
			stmt.executeUpdate(execMe);

			execMe  = "create function prevent_deleting_current_employee() returns trigger as $$\n"
					+ "declare\n"
					+ "\tcurrent_account account%rowtype;\n"
					+ "begin\n"
					+ "\texecute 'select * from account where login = current_user' into current_account;\n"
					+ "\tif old.id = current_account.employee_id then\n"
					+ "\t\traise exception 'Employee with ID % was not deleted because his owner are you!', old.id "
					+ "using hint = 'However, the rest of the query was executed successfully';\n"
					+ "\t\treturn null;\n"
					+ "\telse\n"
					+ "\t\treturn old;\n"
					+ "\tend if;\n"
					+ "end;\n"
					+ "$$ LANGUAGE 'plpgsql';";
			stmt.executeUpdate(execMe);

			execMe  = "create trigger prevent_deleting_current_employee before delete on employee "
					+ "for each row execute procedure prevent_deleting_current_employee()";
			stmt.executeUpdate(execMe);

			execMe  = "create function create_new_user() returns trigger as $$\n"
					+ "begin\n"
					+ "\texecute format('CREATE USER %s WITH PASSWORD $pass$%s$pass$ IN ROLE %s', new.login, new.password, new.role);\n"
					+ "\tnew.password = '********';\n"
					+ "\tif new.role = 'ADMIN' then\n"
					+ "\t\texecute format('ALTER USER %s WITH CREATEROLE', new.login);\n"
					+ "\tend if;\n"
					+ "\treturn new;\n"
					+ "end;\n"
					+ "$$ LANGUAGE 'plpgsql';";
			stmt.executeUpdate(execMe);

			execMe  = "create trigger create_new_user before insert on account "
					+ "for each row execute procedure create_new_user()";
			stmt.executeUpdate(execMe);

			//Disconnect blocked user if it active with: pg_stat_activity + pg_terminate_backend() would be nice,
			//but it requires superuser rights whereas blocking doesn't
			execMe  = "create function update_user() returns trigger as $$\n"
					+ "begin\n"
					+ "\t if TG_OP = 'UPDATE' AND old.login = current_user then\n"
					+ "\t\traise exception 'You can not edit your account by yourself';\n"
					+ "\telse"
					+ "\t\tif TG_OP = 'UPDATE' AND new.role <> old.role then\n"
					+ "\t\t\tnew.login = old.login;\n"
					+ "\t\t\tnew.employee_id = old.employee_id;\n"
					+ "\t\t\texecute format('REVOKE %s FROM %s', old.role, old.login);\n"
					+ "\t\t\texecute format('GRANT %s TO %s', new.role, old.login);\n"
					+ "\t\tend if;\n"
					+ "\t\tif new.blocked then\n"
					+ "\t\t\texecute format('ALTER USER %s WITH NOLOGIN', new.login);\n"
					+ "\t\telse\n"
					+ "\t\t\texecute format('ALTER USER %s WITH LOGIN', new.login);\n"
					+ "\t\tend if;\n"
					+ "\tend if;\n"
					+ "\treturn new;\n"
					+ "end;\n"
					+ "$$ LANGUAGE 'plpgsql';";
			stmt.executeUpdate(execMe);

			execMe  = "create trigger update_user after insert or update on account "
					+ "for each row execute procedure update_user()";
			stmt.executeUpdate(execMe);
		}
	}
}
