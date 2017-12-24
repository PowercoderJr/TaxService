package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class CreateOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private T object;

	public CreateOrder(Class<T> itemClazz, String sendersLogin, T object)
	{
		super(itemClazz, sendersLogin);
		this.object = object;
	}

	public T getObject()
	{
		return object;
	}

	@Override
	public String toString()
	{
		return "CreateOrder for " + itemClazz.getSimpleName() + " - " + object;
	}
}
