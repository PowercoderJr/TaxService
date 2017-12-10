package TaxService.Handlers;

import TaxService.Orders.CreateOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

public class CreateOrderHandler extends AbstractHandler<CreateOrder<? extends AbstractDAO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CreateOrder<? extends AbstractDAO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		ServerAgent.getInstance().create(msg.getObject());
	}


}
