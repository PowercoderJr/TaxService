package TaxService.CRUDs;

import TaxService.DAOs.Deptype;

import java.sql.Connection;

public class DeptypeCRUD extends AbstractCRUD<Deptype>
{
	public DeptypeCRUD(Connection connection)
	{
		super(connection, Deptype.class);
	}
}
