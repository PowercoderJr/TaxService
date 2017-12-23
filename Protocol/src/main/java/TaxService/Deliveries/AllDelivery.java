package TaxService.Deliveries;

import TaxService.DAOs.AbstractDAO;
import TaxService.Orders.ReadAllOrder;

import java.util.List;

public class AllDelivery<T extends AbstractDAO> extends AbstractDelivery<T>
{
	private ReadAllOrder.Purposes purpose;

	public AllDelivery(Class<T> contentClazz, List<T> content, ReadAllOrder.Purposes purpose)
	{
		super(contentClazz, content);
		this.purpose = purpose;
	}

	public ReadAllOrder.Purposes getPurpose()
	{
		return purpose;
	}

	@Override
	public String toString()
	{
		return "AllDelivery for " + contentClazz.getSimpleName() + " - " + purpose;
	}
}
