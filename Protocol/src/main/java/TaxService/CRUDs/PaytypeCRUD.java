package TaxService.CRUDs;

import TaxService.DAOs.Paytype;
import org.hibernate.SessionFactory;

public class PaytypeCRUD extends AbstractCRUD<Paytype>
{
	public PaytypeCRUD(SessionFactory factory)
	{
		super(factory, Paytype.class);
	}
}
