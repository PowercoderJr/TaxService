package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class UpdateOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private String filter;
	private String newValues;

	public UpdateOrder(Class<T> itemClazz, String filter, String newValues)
	{
		super(itemClazz);
		this.filter = filter;
		this.newValues = newValues;
	}

	public String getFilter()
	{
		return filter;
	}

	public String getNewValues()
	{
		return newValues;
	}

	@Override
	public String toString()
	{
		return "UpdateOrder for " + itemClazz.getSimpleName() + filter + " - " + newValues;
	}
}
