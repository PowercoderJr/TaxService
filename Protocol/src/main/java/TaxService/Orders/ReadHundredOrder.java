package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class ReadHundredOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private static final long serialVersionUID = 88005553543L;
	int hundred;

	public ReadHundredOrder(Class itemClazz, int hundred)
	{
		super(itemClazz);
		this.hundred = hundred;
	}

	public int getHundred()
	{
		return hundred;
	}
}
