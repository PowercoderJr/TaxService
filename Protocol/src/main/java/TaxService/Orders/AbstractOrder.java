package TaxService.Orders;

public abstract class AbstractOrder<T>
{
	protected Class<T> clazz;

	public AbstractOrder(Class clazz)
	{
		this.clazz = clazz;
	}
}
