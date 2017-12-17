package TaxService.Handlers;

import TaxService.Deliveries.HundredDelivery;
import TaxService.Orders.ReadHundredOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.sql.ResultSet;
import java.util.List;

public class ReadHundredOrderHandler extends AbstractHandler<ReadHundredOrder<? extends AbstractDAO>>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ReadHundredOrder<? extends AbstractDAO> msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		int total = ServerAgent.getInstance().count(msg.getItemClazz(), msg.getSendersLogin());
		int hundred = Math.min(msg.getHundred(), (total - 1) / 100 + 1);
		if (hundred < 1) hundred = 1;
		int first = Math.min((hundred - 1) * 100 + 1, total);
		int last = Math.min(first + 99, total);
		List list = ServerAgent.getInstance().readHundred(msg.getItemClazz(), msg.getSendersLogin(), hundred);

		ctx.writeAndFlush(new HundredDelivery<>(msg.getItemClazz(), list, first, last, total));
	}
}
