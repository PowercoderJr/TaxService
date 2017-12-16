package TaxService.CRUDs;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import TaxService.DAOs.Deptype;
import TaxService.RandomHelper;
import TaxService.DAOs.Department;

public class DepartmentCRUD extends AbstractRandomableCRUD<Department>
{
	public DepartmentCRUD(Connection connection)
	{
		super(connection, Department.class);
	}

	@Override
	protected Department generateRandomBean() throws SQLException
	{
		String city = RandomHelper.getRandomCity();

		String name = "Отделение №" + (rnd.nextInt(90) + 1) + " города " + city;

		DeptypeCRUD deptypeCRUD = new DeptypeCRUD(connection);
		Deptype deptype = deptypeCRUD.readRandom();

		BigDecimal startyear = new BigDecimal(1960 + rnd.nextInt(55));

		String phone = RandomHelper.getRandomPhone();

		String street = RandomHelper.getRandomStreet();

		String house = RandomHelper.getRandomHouse();

		return new Department(name, deptype, startyear, phone, city, street, house);
	}

	@Override
	public Department readLazy(long id) throws SQLException
	{
		try (Statement stmt = connection.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT id, name FROM department WHERE id = " + id);
			if (rs.next())
			{
				Department result = new Department();
				result.id = rs.getLong(1);
				result.name = rs.getString(2);
				return result;
			}
			else
				return null;
		}
	}
}
