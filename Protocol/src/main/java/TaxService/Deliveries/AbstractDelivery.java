package TaxService.Deliveries;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractDelivery<T> implements Serializable
{
	protected static final long serialVersionUID = -4594850142078035413L;
	protected Class<T> contentClazz;
	protected List<T> content;

	public AbstractDelivery(Class<T> contentClazz, List<T> content)
	{
		this.contentClazz = contentClazz;
		this.content = content;
	}

	public Class<T> getContentClazz()
	{
		return contentClazz;
	}

	public List<T> getContent()
	{
		return content;
	}
}
