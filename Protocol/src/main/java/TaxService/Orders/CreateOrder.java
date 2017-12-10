package TaxService.Orders;

import TaxService.POJO;

public class CreateOrder<T extends POJO> extends AbstractOrder<T>
{
	private static final long serialVersionUID = 88005553541L;
	public CreateOrder(T object, Class itemClazz)
	{
		super(object, itemClazz);
	}
}
