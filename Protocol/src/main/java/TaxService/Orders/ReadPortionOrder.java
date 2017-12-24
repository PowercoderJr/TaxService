package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class ReadPortionOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private int portion;
	private boolean isLazy;
	private String filter;

	public ReadPortionOrder(Class itemClazz, String sendersLogin, int portion, boolean isLazy, String filter)
	{
		super(itemClazz, sendersLogin);
		this.portion = portion;
		this.isLazy = isLazy;
		this.filter = filter;
	}

	public int getPortion()
	{
		return portion;
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
		return "ReadPortionOrder (" + (isLazy ? "LAZY" : "EAGER") + ") for " + itemClazz.getSimpleName() + " - " + portion;
	}
}
