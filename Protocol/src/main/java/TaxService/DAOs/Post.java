package TaxService.DAOs;

public class Post extends AbstractDAO
{
    private static final long serialVersionID = 666000123210006L;

    public long id;
    public String name;

    public Post(String name)
    {
        this.name = name;
    }

    public Post()
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
        return "Post{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
