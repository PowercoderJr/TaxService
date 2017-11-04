package TaxService.CRUDs;

import TaxService.DAO.Paytype;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class PaytypeCRUD extends AbstractCRUD<Paytype>
{
	public PaytypeCRUD(SessionFactory factory)
	{
		super(factory, Paytype.class);
	}
}
