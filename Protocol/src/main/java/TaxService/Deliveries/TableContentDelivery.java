package TaxService.Deliveries;

import TaxService.DAOs.AbstractDAO;

import java.util.List;

public class TableContentDelivery<T extends AbstractDAO> extends AbstractDelivery<T>
{
	public TableContentDelivery(Class<T> contentClazz, List<T> content)
	{
		super(contentClazz, content);
	}
}
