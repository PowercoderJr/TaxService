package TaxService.CRUDs;

import TaxService.DAOs.Deptype;

import java.sql.Connection;

public class DeptypeCRUD extends AbstractRefCRUD<Deptype>
{
	public DeptypeCRUD(Connection connection, Connection superConnection)
	{
		super(connection, superConnection, Deptype.class);
	}
}
