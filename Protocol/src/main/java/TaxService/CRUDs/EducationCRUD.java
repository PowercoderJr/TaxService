package TaxService.CRUDs;

import TaxService.DAOs.Education;
import org.hibernate.SessionFactory;

public class EducationCRUD extends AbstractCRUD<Education>
{
	public EducationCRUD(SessionFactory factory)
	{
		super(factory, Education.class);
	}
}
