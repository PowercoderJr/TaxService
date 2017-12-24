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
		List<T> list = ServerAgent.getInstance().readAll(msg.getItemClazz(), msg.getSendersLogin(), msg.isLazy(), msg.getFilter());

		ctx.writeAndFlush(new AllDelivery<>(msg.getItemClazz(), list, msg.getPurpose()));
	}
}
