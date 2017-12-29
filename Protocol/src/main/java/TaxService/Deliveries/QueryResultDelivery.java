package TaxService.Deliveries;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class QueryResultDelivery extends AbstractDelivery<ArrayList>
{

	public QueryResultDelivery(Class<ArrayList> contentClazz, List<ArrayList> content)
	{
		super(contentClazz, content);
	}

	@Override
	public String toString()
	{
		return "QueryResultDelivery with " + content.size() + " rows";
	}
}
