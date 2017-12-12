package TaxService.Netty.Handlers;

import TaxService.Netty.ClientAgent;
import TaxService.PhraseBook;
import io.netty.channel.ChannelHandlerContext;

public class SimpleMessageHandler extends AbstractHandler<String>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		String[] tokens = msg.split("\\" + PhraseBook.SEPARATOR);
		switch (tokens[0])
		{
			case PhraseBook.ACCESS:
				ClientAgent.publishAuth(tokens[1].equals(PhraseBook.YES));
				break;
		}
	}
}
