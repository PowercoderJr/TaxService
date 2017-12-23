package TaxService.DAOs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Owntype extends AbstractRefDAO
{
    static
    {
        try
        {
            readEvenIfLazy.put(Owntype.class, new Field[] {Owntype.class.getField("id"),
                                                           Owntype.class.getField("name")});
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    public static final void init(){}

    public Owntype(String name)
    {
        super(name);
    }

    public Owntype()
    {
        super();
    }
}
