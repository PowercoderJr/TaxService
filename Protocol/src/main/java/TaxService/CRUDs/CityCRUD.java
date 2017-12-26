package TaxService.CRUDs;

import TaxService.DAOs.City;

import java.sql.Connection;

public class CityCRUD extends AbstractRefCRUD<City>
{
	public CityCRUD(Connection connection)
	{
		super(connection, City.class);
	}
}
