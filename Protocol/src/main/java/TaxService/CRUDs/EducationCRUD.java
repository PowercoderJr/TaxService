package TaxService.CRUDs;

import TaxService.DAOs.Education;

import java.sql.Connection;

public class EducationCRUD extends AbstractRefCRUD<Education>
{
	public EducationCRUD(Connection connection)
	{
		super(connection, Education.class);
	}
}
