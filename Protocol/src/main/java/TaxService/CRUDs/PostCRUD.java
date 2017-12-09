package TaxService.CRUDs;

import TaxService.DAOs.Education;
import TaxService.DAOs.Post;
import org.hibernate.SessionFactory;

public class PostCRUD extends AbstractCRUD<Post>
{
	public PostCRUD(SessionFactory factory)
	{
		super(factory, Post.class);
	}
}
