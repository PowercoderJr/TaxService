package TaxService.CRUDs;

import TaxService.DAOs.Deptype;
import org.hibernate.SessionFactory;

public class DeptypeCRUD extends AbstractCRUD<Deptype>
{
	public DeptypeCRUD(SessionFactory factory)
	{
		super(factory, Deptype.class);
	}
}
