package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

import java.io.Serializable;

public abstract class AbstractOrder<T extends AbstractDAO> implements Serializable
{
	private static final long serialVersionUID = 88005553540L;
	protected Class<T> itemClazz;

	public AbstractOrder(Class itemClazz)
	{
		this.itemClazz = itemClazz;
	}

	public Class<T> getItemClazz()
	{
		return itemClazz;
	}
}
