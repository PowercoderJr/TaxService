package TaxService.Handlers;

import TaxService.DAOs.AbstractDAO;
import TaxService.Orders.DeleteOrder;
import TaxService.PhraseBook;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;

public class DeleteOrderHandler<T extends AbstractDAO> extends AbstractHandler<DeleteOrder<T>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DeleteOrder<T> msg)
	{
		super.channelRead0(ctx, msg);
		try
		{
			int deleted = ServerAgent.getInstance().delete(msg.getItemClazz(), ctx.channel().id(), msg.getFilter());
			ctx.channel().writeAndFlush(PhraseBook.NOTIFICATION + PhraseBook.SEPARATOR + "Удалено строк: " + deleted);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			ctx.channel().writeAndFlush(PhraseBook.ERROR + PhraseBook.SEPARATOR + e.getMessage());
		}
	}
}
