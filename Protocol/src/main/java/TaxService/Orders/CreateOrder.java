package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class CreateOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private static final long serialVersionUID = 88005553541L;
	protected T object;

	public CreateOrder(Class itemClazz, String sendersLogin, T object)
	{
		super(itemClazz, sendersLogin);
		this.object = object;
		Class test = object.getClass();
	}

	public T getObject()
	{
		return object;
	}
}
