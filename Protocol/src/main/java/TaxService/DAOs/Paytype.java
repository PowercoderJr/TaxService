package TaxService.DAOs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Paytype extends AbstractRefDAO
{
    static
    {
        try
        {
            readEvenIfLazy.put(Paytype.class, new Field[] {Paytype.class.getField("id"),
                                                           Paytype.class.getField("name")});
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    public static final void init(){}

    public Paytype(String name)
    {
        super(name);
    }

    public Paytype()
    {
        super();
    }
}
