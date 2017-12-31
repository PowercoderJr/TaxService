package TaxService.Handlers;

import TaxService.DAOs.AbstractDAO;
import TaxService.Deliveries.AllDelivery;
import TaxService.Orders.ReadAllOrder;
import TaxService.PhraseBook;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;
import java.util.List;

public class ReadAllOrderHandler<T extends AbstractDAO> extends AbstractHandler<ReadAllOrder<T>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ReadAllOrder<T> msg)
	{
		super.channelRead0(ctx, msg);
		try
		{
			List<T> list = ServerAgent.getInstance().readAll(msg.getItemClazz(), msg.getSendersLogin(), msg.isLazy(), msg.getFilter());

			ctx.writeAndFlush(new AllDelivery<>(msg.getItemClazz(), list));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			ctx.channel().writeAndFlush(PhraseBook.ERROR + PhraseBook.SEPARATOR + e.getMessage());
		}
	}
}
