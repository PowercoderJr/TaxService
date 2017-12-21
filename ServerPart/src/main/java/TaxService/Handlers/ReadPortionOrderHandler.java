package TaxService.Handlers;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.Deliveries.PortionDelivery;
import TaxService.Orders.ReadPortionOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class ReadPortionOrderHandler extends AbstractHandler<ReadPortionOrder<? extends AbstractDAO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ReadPortionOrder<? extends AbstractDAO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		int total = ServerAgent.getInstance().count(msg.getItemClazz(), msg.getSendersLogin());
		int portion = Math.min(msg.getPortion(), (total - 1) / AbstractCRUD.PORTION_SIZE + 1);
		if (portion < 1) portion = 1;
		int first = Math.min((portion - 1) * AbstractCRUD.PORTION_SIZE + 1, total);
		int last = Math.min(first + AbstractCRUD.PORTION_SIZE - 1, total);
		List list = ServerAgent.getInstance().readPortion(msg.getItemClazz(), msg.getSendersLogin(), portion);

		ctx.writeAndFlush(new PortionDelivery<>(msg.getItemClazz(), list, first, last, total));
	}
}
