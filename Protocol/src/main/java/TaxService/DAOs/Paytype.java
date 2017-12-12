package TaxService.DAOs;

public class Paytype extends AbstractDAO
{
    private static final long serialVersionID = 666000123210008L;

    public long id;
    public String name;

    public Paytype(String name)
    {
        this.name = name;
    }

    public Paytype()
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
        return "Paytype{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
