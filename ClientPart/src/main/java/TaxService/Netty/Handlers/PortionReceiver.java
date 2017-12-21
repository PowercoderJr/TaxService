package TaxService.Netty.Handlers;

import TaxService.Deliveries.PortionDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.DAOs.AbstractDAO;
import io.netty.channel.ChannelHandlerContext;

public class PortionReceiver extends AbstractHandler<PortionDelivery<? extends AbstractDAO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PortionDelivery<? extends AbstractDAO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		ClientAgent.publishPortionReceived(msg);
	}
}
