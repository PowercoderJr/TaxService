package TaxService.DAOs;

public class Deptype extends AbstractDAO
{
    private static final long serialVersionID = 666000123210005L;

    public long id;
    public String name;

	public Deptype(String name)
	{
		this.name = name;
	}

	public Deptype()
	{
		;
	}

	public long getId()
	{
		return id;
	}

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
		return "Deptype{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}
