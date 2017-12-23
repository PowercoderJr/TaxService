package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class ReadPortionOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private int portion;
	private boolean isLazy;

	public ReadPortionOrder(Class itemClazz, String sendersLogin, int portion, boolean isLazy)
	{
		super(itemClazz, sendersLogin);
		this.portion = portion;
		this.isLazy = isLazy;
	}

	public int getPortion()
	{
		return portion;
	}

	public boolean isLazy()
	{
		return isLazy;
	}

	@Override
	public String toString()
	{
		return "ReadPortionOrder (" + (isLazy ? "LAZY" : "EAGER") + ") for " + itemClazz.getSimpleName() + " - " + portion;
	}
}
