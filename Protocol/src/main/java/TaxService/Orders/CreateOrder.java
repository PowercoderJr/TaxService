package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class CreateOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private static final long serialVersionUID = 88005553541L;
	protected T object;

	public CreateOrder(Class itemClazz, T object)
	{
		super(itemClazz);
		this.object = object;
	}

	public T getObject()
	{
		return object;
	}
}
