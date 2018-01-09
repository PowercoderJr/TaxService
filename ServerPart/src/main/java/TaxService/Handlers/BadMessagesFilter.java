package TaxService.Handlers;

import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static TaxService.PhraseBook.*;

//Для тестов
public class BadMessagesFilter extends SimpleChannelInboundHandler<Object>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		boolean alright = false;

		try
		{
			Connection connection;
			Pair<Connection, Long> pair = ServerAgent.getInstance().getConnections().get(ctx.channel().id());
			if (pair != null)
				connection = pair.getKey();
			else
			{
				//Assume it's login message
				String[] tokens = msg.toString().split("\\" + SEPARATOR);
				connection = DriverManager.getConnection("jdbc:postgresql://localhost/TaxService", tokens[1], tokens[2]);
			}

			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT CURRENT_USER");
			rs.next();
			if (rs.getString(1).equals("postgres"))
				alright = true;
			else
			{
				rs = stmt.executeQuery("SELECT blocked FROM account WHERE login = CURRENT_USER");
				if (rs.next())
					alright = rs.getString(1).equals("f");
			}
		}
		catch (Exception ignore)
		{}

		if (alright)
		{
			System.out.println("Message successfully received from " + ctx.channel().remoteAddress());
			ctx.fireChannelRead(msg);
		}
		else
		{
			System.out.println("Message discarded because sender isn't registered or blocked on database server!");
			ctx.channel().writeAndFlush(ACCESS + SEPARATOR + ACCESS_RESULT_FORBIDDEN + SEPARATOR + "-");
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
