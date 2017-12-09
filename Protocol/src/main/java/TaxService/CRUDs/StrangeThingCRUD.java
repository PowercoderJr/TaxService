package TaxService.CRUDs;

import TaxService.DAOs.StrangeThing;
import org.hibernate.SessionFactory;

public class StrangeThingCRUD extends AbstractCRUD<StrangeThing>
{
	public StrangeThingCRUD(SessionFactory factory)
	{
		super(factory, StrangeThing.class);
	}
}
