package TaxService.Handlers;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.DAOs.AbstractDAO;
import TaxService.Deliveries.AllDelivery;
import TaxService.Deliveries.PortionDelivery;
import TaxService.Orders.ReadAllOrder;
import TaxService.Orders.ReadPortionOrder;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class ReadAllOrderHandler<T extends AbstractDAO> extends AbstractHandler<ReadAllOrder<T>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ReadAllOrder<T> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		List list = ServerAgent.getInstance().readAll(msg.getItemClazz(), msg.getSendersLogin(), msg.isLazy());

		ctx.writeAndFlush(new AllDelivery<T>(msg.getItemClazz(), list, ReadAllOrder.Purposes.REFRESH_CB));
	}
}
