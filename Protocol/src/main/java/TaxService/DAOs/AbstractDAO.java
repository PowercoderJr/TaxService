package TaxService.DAOs;

import java.io.Serializable;

public class AbstractDAO implements Serializable
{
	protected static final long serialVersionUID = -6857245162865301490L;
	public long id;

	public AbstractDAO()
	{

	}

	public long getId()
	{
		return id;
	}
}
