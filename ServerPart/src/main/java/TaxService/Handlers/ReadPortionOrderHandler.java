package TaxService.Handlers;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.Deliveries.PortionDelivery;
import TaxService.Orders.ReadPortionOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class ReadPortionOrderHandler<T extends AbstractDAO> extends AbstractHandler<ReadPortionOrder<T>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ReadPortionOrder<T> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		int total = ServerAgent.getInstance().count(msg.getItemClazz(), msg.getSendersLogin());
		int portion = Math.min(msg.getPortion(), (total - 1) / AbstractCRUD.PORTION_SIZE + 1);
		if (portion < 1) portion = 1;
		int first = Math.min((portion - 1) * AbstractCRUD.PORTION_SIZE + 1, total);
		int last = Math.min(first + AbstractCRUD.PORTION_SIZE - 1, total);
		List list = ServerAgent.getInstance().readPortion(msg.getItemClazz(), msg.getSendersLogin(), portion, msg.isLazy());

		ctx.writeAndFlush(new PortionDelivery<T>(msg.getItemClazz(), list, first, last, total));
	}
}
