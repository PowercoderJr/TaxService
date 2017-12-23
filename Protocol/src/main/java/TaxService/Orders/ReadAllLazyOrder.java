package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class ReadAllLazyOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	public ReadAllLazyOrder(Class itemClazz, String sendersLogin)
	{
		super(itemClazz, sendersLogin);
	}

	@Override
	public String toString()
	{
		return "ReadAllLazyOrder for " + itemClazz.getSimpleName();
	}
}
