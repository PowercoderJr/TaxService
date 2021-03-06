package TaxService.Handlers;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.Deliveries.PortionDelivery;
import TaxService.Orders.ReadPortionOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.PhraseBook;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;
import java.util.List;

public class ReadPortionOrderHandler<T extends AbstractDAO> extends AbstractHandler<ReadPortionOrder<T>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ReadPortionOrder<T> msg)
	{
		super.channelRead0(ctx, msg);
		try
		{
			int total = ServerAgent.getInstance().count(msg.getItemClazz(), ctx.channel().id(), msg.getFilter());
			int portion = Math.min(msg.getPortion(), (total - 1) / AbstractCRUD.PORTION_SIZE + 1);
			if (portion < 1)
				portion = 1;
			int first = Math.min((portion - 1) * AbstractCRUD.PORTION_SIZE + 1, total);
			int last = Math.min(first + AbstractCRUD.PORTION_SIZE - 1, total);
			List<T> list = ServerAgent.getInstance().readPortion(msg.getItemClazz(), ctx.channel().id(), portion, msg.isLazy(), msg.getFilter());

			ctx.writeAndFlush(new PortionDelivery<>(msg.getItemClazz(), list, first, last, total));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			ctx.channel().writeAndFlush(PhraseBook.ERROR + PhraseBook.SEPARATOR + e.getMessage());
		}
	}
}
