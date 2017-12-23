package TaxService.Handlers;

import TaxService.Orders.CreateOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

public class CreateOrderHandler<T extends AbstractDAO> extends AbstractHandler<CreateOrder<T>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CreateOrder<T> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		ServerAgent.getInstance().create(msg.getObject(), msg.getSendersLogin());
	}


}
