package TaxService.CRUDs;

import TaxService.DAOs.Post;

import java.sql.Connection;

public class PostCRUD extends AbstractRefCRUD<Post>
{
	public PostCRUD(Connection connection, Connection superConnection)
	{
		super(connection, superConnection, Post.class);
	}
}
