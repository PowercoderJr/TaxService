package TaxService.Handlers;

import TaxService.DAOs.AbstractDAO;
import TaxService.Orders.DeleteOrder;
import TaxService.Orders.UpdateOrder;
import TaxService.PhraseBook;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;

public class UpdateOrderHandler<T extends AbstractDAO> extends AbstractHandler<UpdateOrder<T>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, UpdateOrder<T> msg)
	{
		super.channelRead0(ctx, msg);
		try
		{
			int updated = ServerAgent.getInstance().update(msg.getItemClazz(), msg.getSendersLogin(), msg.getFilter(), msg.getNewValues());
			ctx.channel().writeAndFlush(PhraseBook.NOTIFICATION + PhraseBook.SEPARATOR + "Изменено строк: " + updated);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			ctx.channel().writeAndFlush(PhraseBook.ERROR + PhraseBook.SEPARATOR + e.getMessage());
		}
	}
}
