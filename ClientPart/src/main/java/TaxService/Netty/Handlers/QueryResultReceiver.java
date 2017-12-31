package TaxService.Netty.Handlers;

import TaxService.Deliveries.QueryResultDelivery;
import TaxService.Netty.ClientAgent;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class QueryResultReceiver extends AbstractHandler<QueryResultDelivery>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, QueryResultDelivery msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		ClientAgent.publishQueryResultReceived(msg);
	}
}
