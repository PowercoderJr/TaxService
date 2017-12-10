package TaxService.Handlers;

import TaxService.Orders.ReadHundredOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class ReadHundredOrderHandler extends AbstractHandler<ReadHundredOrder<? extends AbstractDAO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ReadHundredOrder<? extends AbstractDAO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		List list = ServerAgent.getInstance().readHundred(msg.getItemClazz(), msg.getHundred());
		ctx.writeAndFlush(list);
	}
}
