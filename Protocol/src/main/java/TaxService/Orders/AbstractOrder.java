package TaxService.Orders;

import TaxService.POJO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOrder<T extends POJO> implements Serializable
{
	private static final long serialVersionUID = 88005553540L;
	protected T object;
	protected Class<T> itemClazz;

	public AbstractOrder(T object, Class itemClazz)
	{
		this.object = object;
		this.itemClazz = itemClazz;
		//this.itemClazz = object.getClass();
	}

	public T getObject()
	{
		return object;
	}

	/*private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();

		/*List tmp = new ArrayList();
		tmp.add(object);
		tmp.add(itemClazz);
		out.writeObject(tmp);*
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		/*List tmp = (List)in.readObject();
		object = (T)tmp.get(0);
		itemClazz = (Class<T>)tmp.get(1);*
	}*/
}
