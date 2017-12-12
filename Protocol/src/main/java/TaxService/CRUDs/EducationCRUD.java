package TaxService.CRUDs;

import TaxService.DAOs.Education;

import java.sql.Connection;

public class EducationCRUD extends AbstractCRUD<Education>
{
	public EducationCRUD(Connection connection)
	{
		super(connection, Education.class);
	}
}
