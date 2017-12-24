package TaxService.Handlers;

import TaxService.Orders.CreateOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.PhraseBook;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;

public class CreateOrderHandler<T extends AbstractDAO> extends AbstractHandler<CreateOrder<T>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CreateOrder<T> msg)
	{
		super.channelRead0(ctx, msg);
		try
		{
			ServerAgent.getInstance().create(msg.getObject(), msg.getSendersLogin());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			ctx.channel().writeAndFlush(PhraseBook.ERROR + PhraseBook.SEPARATOR + e.getMessage());
		}
	}
}
