package TaxService.DAOs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Post extends AbstractRefDAO
{
    static
    {
        try
        {
            readEvenIfLazy.put(Post.class, new Field[] {Post.class.getField("id"),
                                                        Post.class.getField("name")});
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    public static final void init(){}

    public Post(String name)
    {
        super(name);
    }

    public Post()
    {
        super();
    }
}
