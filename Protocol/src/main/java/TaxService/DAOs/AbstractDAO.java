package TaxService.DAOs;

import java.io.Serializable;

public class AbstractDAO implements Serializable
{
	private static final long serialVersionUID = 789456123456L;

	public long id;
	public long getId()
	{
		return id;
	}

	public AbstractDAO()
	{
		;
	}
}
