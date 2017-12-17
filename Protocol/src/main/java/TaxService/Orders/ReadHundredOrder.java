package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class ReadHundredOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private int hundred;

	public ReadHundredOrder(Class itemClazz, String sendersLogin, int hundred)
	{
		super(itemClazz, sendersLogin);
		this.hundred = hundred;
	}

	public int getHundred()
	{
		return hundred;
	}

	@Override
	public String toString()
	{
		return "ReadHundredOrder for " + itemClazz.getSimpleName() + " - " + hundred;
	}
}
