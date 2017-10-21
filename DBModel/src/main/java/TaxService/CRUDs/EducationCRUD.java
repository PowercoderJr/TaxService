package TaxService.CRUDs;

import TaxService.DAO.Education;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class EducationCRUD extends AbstractCRUD<Education>
{
	public EducationCRUD(SessionFactory factory)
	{
		super(factory, Education.class);
	}
}
