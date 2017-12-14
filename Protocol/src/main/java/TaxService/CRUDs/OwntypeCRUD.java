package TaxService.CRUDs;

import TaxService.DAOs.Owntype;

import java.sql.Connection;

public class OwntypeCRUD extends AbstractRefCRUD<Owntype>
{
	public OwntypeCRUD(Connection connection)
	{
		super(connection, Owntype.class);
	}
}
