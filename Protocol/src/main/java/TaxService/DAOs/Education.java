package TaxService.DAOs;

public class Education extends AbstractDAO
{
    private static final long serialVersionID = 666000123210007L;

    public long id;
    public String name;

    public Education(String name)
    {
        this.name = name;
    }

    public Education()
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
        return "Education{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
