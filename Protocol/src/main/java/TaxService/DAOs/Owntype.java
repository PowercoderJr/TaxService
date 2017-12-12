package TaxService.DAOs;

public class Owntype extends AbstractDAO
{
    private static final long serialVersionID = 666000123210009L;

    public long id;
    public String name;

    public Owntype(String name)
    {
        this.name = name;
    }

    public Owntype()
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
        return "Owntype{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
