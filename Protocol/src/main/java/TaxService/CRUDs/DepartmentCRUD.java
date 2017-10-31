package TaxService.CRUDs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.TypedQuery;

import TaxService.DAO.Deptype;
import TaxService.RandomHelper;
import org.hibernate.SessionFactory;
import TaxService.DAO.Department;

public class DepartmentCRUD extends AbstractRandomableCRUD<Department>
{
	public DepartmentCRUD(SessionFactory factory)
	{
		super(factory, Department.class);
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
