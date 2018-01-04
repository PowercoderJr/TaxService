package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class DeleteOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private String filter;

	public DeleteOrder(Class<T> itemClazz, String filter)
	{
		super(itemClazz);
		this.filter = filter;
	}

	public String getFilter()
	{
		return filter;
	}

	@Override
	public String toString()
	{
		return "DeleteOrder for " + itemClazz.getSimpleName() + filter;
	}
}
