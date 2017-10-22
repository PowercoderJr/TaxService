package TaxService;

import TaxService.CRUDs.AbstractCRUD;
import org.hibernate.SessionFactory;

public class StrangeThingCRUD extends AbstractCRUD<StrangeThing>
{
	public StrangeThingCRUD(SessionFactory factory)
	{
		super(factory, StrangeThing.class);
	}
}
