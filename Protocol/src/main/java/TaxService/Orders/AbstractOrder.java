package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;

import java.io.Serializable;

public abstract class AbstractOrder<T extends AbstractDAO> implements Serializable
{
	private static final long serialVersionUID = 88005553540L;
	protected Class<T> itemClazz;
	protected String sendersLogin;

	public AbstractOrder(Class<T> itemClazz, String sendersLogin)
	{
		this.itemClazz = itemClazz;
		this.sendersLogin = sendersLogin;
	}

	public Class<T> getItemClazz()
	{
		return itemClazz;
	}

	public String getSendersLogin()
	{
		return sendersLogin;
	}
}
