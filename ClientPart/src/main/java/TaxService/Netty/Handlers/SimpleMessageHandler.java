package TaxService.Netty.Handlers;

import TaxService.Netty.ClientAgent;
import TaxService.Dictionary;
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
			case Dictionary.ACCESS:
				ClientAgent.publishAuth(tokens[1].equals(Dictionary.YES));
				break;
		}
	}
}
