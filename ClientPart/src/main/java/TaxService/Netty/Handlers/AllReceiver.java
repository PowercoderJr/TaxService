package TaxService.Netty.Handlers;

import TaxService.DAOs.AbstractDAO;
import TaxService.Deliveries.AllDelivery;
import TaxService.Deliveries.PortionDelivery;
import TaxService.Netty.ClientAgent;
import io.netty.channel.ChannelHandlerContext;

public class AllReceiver extends AbstractHandler<AllDelivery<? extends AbstractDAO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AllDelivery<? extends AbstractDAO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		ClientAgent.publishAllReceived(msg);
	}
}
