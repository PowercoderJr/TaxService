package TaxService.Netty.Handlers;

import TaxService.Netty.ClientAgent;
import TaxService.DAOs.AbstractDAO;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class TableContentReceiver extends AbstractHandler<List<? extends AbstractDAO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, List<? extends AbstractDAO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		ClientAgent.publishTableContentReceived(msg);
	}
}
