package TaxService.DAOs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Deptype extends AbstractRefDAO
{
	static
	{
		try
		{
			readEvenIfLazy.put(Deptype.class, new Field[] {Deptype.class.getField("id"),
														   Deptype.class.getField("name")});
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}

	public static final void init(){}

	public Deptype(String name)
	{
		super(name);
	}

	public Deptype()
	{
		super();
	}
}
