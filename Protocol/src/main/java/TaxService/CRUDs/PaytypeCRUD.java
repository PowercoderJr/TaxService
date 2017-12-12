package TaxService.CRUDs;

import TaxService.DAOs.Paytype;

import java.sql.Connection;

public class PaytypeCRUD extends AbstractCRUD<Paytype>
{
	public PaytypeCRUD(Connection connection)
	{
		super(connection, Paytype.class);
	}
}
