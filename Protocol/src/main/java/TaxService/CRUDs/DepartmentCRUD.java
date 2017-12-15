package TaxService.CRUDs;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

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
		Deptype deptype = deptypeCRUD.getRandom();

		BigDecimal startyear = new BigDecimal(1960 + rnd.nextInt(55));

		String phone = RandomHelper.getRandomPhone();

		String street = RandomHelper.getRandomStreet();

		String house = RandomHelper.getRandomHouse();

		return new Department(name, deptype, startyear, phone, city, street, house);
	}
}
