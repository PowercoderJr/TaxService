package TaxService.Handlers;

import TaxService.PhraseBook;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class SimpleMessageHandler extends AbstractHandler<String>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
	{
		super.channelRead0(ctx, msg);
		String[] tokens = msg.split("\\" + PhraseBook.SEPARATOR);
		switch (tokens[0])
		{
			case PhraseBook.AUTH:
				boolean acceessed = false;
				Connection newConnection;
				try
				{
					Map<String, Connection> connections = ServerAgent.getInstance().getConnections();
					Connection oldConnection = connections.get(tokens[1]);
					//TODO: пересмотреть участок после определения порядка авторизации и деавторизации
					if (oldConnection == null || !oldConnection.isValid(10))
					{
						if (oldConnection != null)
						{
							oldConnection.close();
							connections.remove(tokens[1]);
						}
						newConnection = DriverManager.getConnection("jdbc:postgresql://localhost/TaxService", tokens[1], tokens[2]);
						connections.put(tokens[1], newConnection);
						acceessed = true;
					}
				}
				catch (SQLException e)
				{
					if (!e.getMessage().contains("password authentication failed"))
					{
						e.printStackTrace();
						ctx.channel().writeAndFlush(PhraseBook.ERROR + PhraseBook.SEPARATOR + e.getMessage());
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				ctx.channel().writeAndFlush(PhraseBook.ACCESS + PhraseBook.SEPARATOR + (acceessed ? PhraseBook.YES : PhraseBook.NO));
				break;
		}
	}
}

