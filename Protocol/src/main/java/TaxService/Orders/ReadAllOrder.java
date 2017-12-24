package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class ReadAllOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	public enum Purposes {REFRESH_CB}

	private boolean isLazy;
	private Purposes purpose;
	private String filter;

	public ReadAllOrder(Class itemClazz, String sendersLogin, boolean isLazy, Purposes purpose, String filter)
	{
		super(itemClazz, sendersLogin);
		this.isLazy = isLazy;
		this.purpose = purpose;
		this.filter = filter;
	}

	public boolean isLazy()
	{
		return isLazy;
	}

	public Purposes getPurpose()
	{
		return purpose;
	}

	public String getFilter()
	{
		return filter;
	}

	@Override
	public String toString()
	{
		return "ReadAllOrder (" + (isLazy ? "LAZY" : "EAGER") + ") for " + itemClazz.getSimpleName() + " - " + purpose;
	}
}
