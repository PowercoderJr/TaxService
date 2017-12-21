package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

public class ReadPortionOrder<T extends AbstractDAO> extends AbstractOrder<T>
{
	private int portion;

	public ReadPortionOrder(Class itemClazz, String sendersLogin, int portion)
	{
		super(itemClazz, sendersLogin);
		this.portion = portion;
	}

	public int getPortion()
	{
		return portion;
	}

	@Override
	public String toString()
	{
		return "ReadPortionOrder for " + itemClazz.getSimpleName() + " - " + portion;
	}
}
