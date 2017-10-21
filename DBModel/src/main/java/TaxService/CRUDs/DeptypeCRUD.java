package TaxService.CRUDs;

import TaxService.DAO.Deptype;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class DeptypeCRUD extends AbstractCRUD<Deptype>
{
	public DeptypeCRUD(SessionFactory factory)
	{
		super(factory, Deptype.class);
	}
}
