package TaxService.DAOs;

import java.lang.reflect.Field;

public class City extends AbstractRefDAO
{
    static
    {
        try
        {
            readEvenIfLazy.put(City.class, new Field[] {City.class.getField("id"),
														City.class.getField("name")});
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    public static final void init(){}

    public City(String name)
    {
        super(name);
    }

    public City()
    {
        super();
    }
}
