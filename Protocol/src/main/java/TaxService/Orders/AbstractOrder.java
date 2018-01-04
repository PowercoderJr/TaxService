package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

import java.io.Serializable;

public abstract class AbstractOrder<T extends AbstractDAO> implements Serializable
{
	protected static final long serialVersionUID = -8256861204339672530L;
	protected Class<T> itemClazz;

	public AbstractOrder(Class<T> itemClazz)
	{
		this.itemClazz = itemClazz;
	}

	public Class<T> getItemClazz()
	{
		return itemClazz;
	}
}
