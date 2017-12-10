package TaxService.Handlers;

import TaxService.Orders.CreateOrder;
import TaxService.POJO;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

public class CreateOrderHandler extends AbstractHandler<CreateOrder<? extends POJO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CreateOrder<? extends POJO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		ServerAgent.getInstance().create(msg.getObject());
	}


}
