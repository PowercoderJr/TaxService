package TaxService.DAOs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Education extends AbstractRefDAO
{
	static
	{
		try
		{
			List<Field> list = new ArrayList<>();
			list.add(Education.class.getField("id"));
			list.add(Education.class.getField("name"));
			readEvenIfLazy.put(Education.class, new Field[] {Education.class.getField("id"),
															 Education.class.getField("name")});
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}

	public static final void init(){}

	public Education(String name)
	{
		super(name);
	}

	public Education()
	{
		super();
	}
}
