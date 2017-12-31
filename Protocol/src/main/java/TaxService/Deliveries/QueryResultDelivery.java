package TaxService.Deliveries;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryResultDelivery extends AbstractDelivery<ArrayList>
{
	private String header;
	private Date date;

	public QueryResultDelivery(List<ArrayList> content, String header, Date date)
	{
		super(ArrayList.class, content);
		this.header = header;
		this.date = date;
	}

	public String getHeader()
	{
		return header;
	}

	public Date getDate()
	{
		return date;
	}

	@Override
	public String toString()
	{
		return "QueryResultDelivery with " + content.size() + " rows";
	}
}
