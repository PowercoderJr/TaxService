package TaxService.Deliveries;

import TaxService.DAOs.AbstractDAO;
import TaxService.Orders.ReadAllOrder;

import java.util.List;

public class AllDelivery<T extends AbstractDAO> extends AbstractDelivery<T>
{
	public AllDelivery(Class<T> contentClazz, List<T> content)
	{
		super(contentClazz, content);
	}

	@Override
	public String toString()
	{
		return "AllDelivery for " + contentClazz.getSimpleName();
	}
}
