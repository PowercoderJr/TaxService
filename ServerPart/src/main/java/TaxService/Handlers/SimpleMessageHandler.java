package TaxService.Handlers;

import TaxService.Dictionary;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

public class SimpleMessageHandler extends AbstractHandler<String>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		String[] tokens = msg.split("\\" + Dictionary.SEPARATOR);
		switch (tokens[0])
		{
			case Dictionary.AUTH:
				boolean acceessed = ServerAgent.getInstance().signIn(tokens[1], tokens[2]);
				ctx.channel().writeAndFlush(Dictionary.ACCESS + Dictionary.SEPARATOR + (acceessed ? Dictionary.YES : Dictionary.NO));
				break;
		}
	}
}

