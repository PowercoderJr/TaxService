package TaxService.Netty.Handlers;

import TaxService.Deliveries.TableContentDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.DAOs.AbstractDAO;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class TableContentReceiver extends AbstractHandler<TableContentDelivery<? extends AbstractDAO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TableContentDelivery<? extends AbstractDAO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		ClientAgent.publishTableContentReceived(msg);
	}
}
