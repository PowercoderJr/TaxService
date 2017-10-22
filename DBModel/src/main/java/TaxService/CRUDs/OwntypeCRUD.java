package TaxService.CRUDs;

import TaxService.DAO.Owntype;
import org.hibernate.SessionFactory;

public class OwntypeCRUD extends AbstractCRUD<Owntype>
{
	public OwntypeCRUD(SessionFactory factory)
	{
		super(factory, Owntype.class);
	}
}
