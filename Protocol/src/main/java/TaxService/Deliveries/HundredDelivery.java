package TaxService.Deliveries;

import TaxService.DAOs.AbstractDAO;

import java.util.List;

public class HundredDelivery<T extends AbstractDAO> extends AbstractDelivery<T>
{
	int first, last, total;

	public HundredDelivery(Class<T> contentClazz, List<T> content, int first, int last, int total)
	{
		super(contentClazz, content);
		this.first = first;
		this.last = last;
		this.total = total;
	}

	public int getFirst()
	{
		return first;
	}

	public int getLast()
	{
		return last;
	}

	public int getTotal()
	{
		return total;
	}

	@Override
	public String toString()
	{
		return "HundredDelivery from " + contentClazz.getSimpleName() + "(" + first + " - " + last + ")";
	}
}
