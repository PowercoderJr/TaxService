package TaxService;

import TaxService.DAOs.AbstractDAO;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils
{
	private Utils(){}

	public static String fieldNamesToString(Stream<Field> input)
	{
		return input
				.map(item -> AbstractDAO.class.isAssignableFrom(item.getType()) ? item.getName() + "_id" : item.getName())
				.collect(Collectors.joining(", "));
	}

	public static String fieldValuesToString(Stream<Field> input, AbstractDAO dao)
	{
		return input.map(item ->
		{
			String result = "";
			try
			{
				if (AbstractDAO.class.isAssignableFrom(item.getType()))
					result = item.get(dao) == null ? "0" : String.valueOf(((AbstractDAO) item.get(dao)).getId());
				else
					result = String.valueOf(item.get(dao));
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return "'" + result + "'";
		}).collect(Collectors.joining(", "));
	}
}
