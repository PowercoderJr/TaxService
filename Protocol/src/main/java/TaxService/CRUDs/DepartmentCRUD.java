package TaxService.CRUDs;

import java.math.BigDecimal;
import java.sql.Connection;

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
	protected Department generateRandomBean()
	{
		String name = "Уникальное название";

		DeptypeCRUD deptypeCRUD = new DeptypeCRUD(factory);
		Deptype deptype = deptypeCRUD.getRandom();
		deptypeCRUD.disconnect();

		BigDecimal startyear = new BigDecimal(1960 + (int)(Math.random() * 55));

		String phone = RandomHelper.getRandomPhone();

		String city = RandomHelper.getRandomCity();

		String street = RandomHelper.getRandomStreet();

		String house = RandomHelper.getRandomHouse();

		return new Department(name, deptype, startyear, phone, city, street, house);
	}
}
