package TaxService.Deliveries;

import java.util.List;

public class QueryResultDelivery extends AbstractDelivery<List>
{

	public QueryResultDelivery(Class<List> contentClazz, List<List> content)
	{
		super(contentClazz, content);
	}

	@Override
	public String toString()
	{
		return "QueryResultDelivery with " + content.size() + " rows";
	}
}
