package TaxService.CRUDs;

import TaxService.DAO.Owntype;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class OwntypeCRUD extends AbstractCRUD<Owntype>
{
	public OwntypeCRUD(SessionFactory factory)
	{
		super(factory, Owntype.class);
	}
}
