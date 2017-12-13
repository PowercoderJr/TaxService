package TaxService.DAOs;

public abstract class AbstractRefDAO extends AbstractDAO
{
	public AbstractRefDAO(String name)
	{
		super();
		this.name = name;
	}

	public String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}