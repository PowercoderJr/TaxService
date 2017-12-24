package TaxService.Netty.Handlers;

import TaxService.Netty.ClientAgent;
import TaxService.PhraseBook;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.stream.Collectors;

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
			case PhraseBook.ERROR:
				String original = Arrays.stream(tokens).collect(Collectors.joining("" + PhraseBook.SEPARATOR));
				ClientAgent.publishExceptionReceived(original.substring(PhraseBook.ERROR.length() +
						("" + PhraseBook.SEPARATOR).length()));
				break;
		}
	}
}
