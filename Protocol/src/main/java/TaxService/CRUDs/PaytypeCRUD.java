package TaxService.CRUDs;

import TaxService.DAOs.Paytype;

import java.sql.Connection;

public class PaytypeCRUD extends AbstractRefCRUD<Paytype>
{
	public PaytypeCRUD(Connection connection, Connection superConnection)
	{
		super(connection, superConnection, Paytype.class);
	}
}
