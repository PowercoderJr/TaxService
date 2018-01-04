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
			int created = ServerAgent.getInstance().create(msg.getObject(), ctx.channel().id());
			ctx.channel().writeAndFlush(PhraseBook.NOTIFICATION + PhraseBook.SEPARATOR + "Добавлено строк: " + created);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			ctx.channel().writeAndFlush(PhraseBook.ERROR + PhraseBook.SEPARATOR + e.getMessage());
		}
	}
}
