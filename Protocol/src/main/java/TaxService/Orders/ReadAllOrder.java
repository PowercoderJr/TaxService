package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class ReadAllOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private boolean isLazy;
	private String filter;

	public ReadAllOrder(Class itemClazz, boolean isLazy, String filter)
	{
		super(itemClazz);
		this.isLazy = isLazy;
		this.filter = filter;
	}

	public boolean isLazy()
	{
		return isLazy;
	}

	public String getFilter()
	{
		return filter;
	}

	@Override
	public String toString()
	{
		return "ReadAllOrder (" + (isLazy ? "LAZY" : "EAGER") + ") for " + itemClazz.getSimpleName();
	}
}
