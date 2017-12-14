package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class ReadHundredOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private static final long serialVersionUID = 88005553543L;
	int hundred;

	public ReadHundredOrder(Class itemClazz, String sendersLogin, int hundred)
	{
		super(itemClazz, sendersLogin);
		this.hundred = hundred;
	}

	public int getHundred()
	{
		return hundred;
	}
}
