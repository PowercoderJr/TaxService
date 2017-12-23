package TaxService.DAOs;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractDAO implements Serializable
{
	protected static final long serialVersionUID = -6857245162865301490L;
	protected static final Map<Class<? extends AbstractDAO>, Field[]> readEvenIfLazy = new HashMap<>();
	public long id;

	public static Field[] getReadEvenIfLazy(Class<? extends AbstractDAO> clazz)
	{
		return readEvenIfLazy.get(clazz);
	}

	public long getId()
	{
		return id;
	}
}
