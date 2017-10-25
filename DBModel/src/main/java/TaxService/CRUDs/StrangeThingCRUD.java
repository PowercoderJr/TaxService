package TaxService.CRUDs;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.DAO.StrangeThing;
import org.hibernate.SessionFactory;

public class StrangeThingCRUD extends AbstractCRUD<StrangeThing>
{
	public StrangeThingCRUD(SessionFactory factory)
	{
		super(factory, StrangeThing.class);
	}
}
