package TaxService.Netty.Handlers;

import TaxService.Deliveries.HundredDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.DAOs.AbstractDAO;
import io.netty.channel.ChannelHandlerContext;

public class HundredReceiver extends AbstractHandler<HundredDelivery<? extends AbstractDAO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HundredDelivery<? extends AbstractDAO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		ClientAgent.publishHundredReceived(msg);
	}
}
