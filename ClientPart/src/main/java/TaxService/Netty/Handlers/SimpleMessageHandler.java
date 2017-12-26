package TaxService.Netty.Handlers;

import TaxService.Netty.ClientAgent;
import io.netty.channel.ChannelHandlerContext;
import javafx.util.Pair;

import java.sql.Connection;
import java.util.Arrays;
import java.util.stream.Collectors;

import static TaxService.PhraseBook.*;

public class SimpleMessageHandler extends AbstractHandler<String>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		String[] tokens = msg.split("\\" + SEPARATOR);
		switch (tokens[0])
		{
			case ACCESS:
				ClientAgent.publishAuth(tokens[1]);
				break;
			case ERROR:
				String original = Arrays.stream(tokens).collect(Collectors.joining(String.valueOf(SEPARATOR)));
				ClientAgent.publishExceptionReceived(original.substring(ERROR.length() +
						(String.valueOf(SEPARATOR)).length()));
				break;
			case NOTIFICATION:
				ClientAgent.publishNotificationReceived(tokens[1]);
				break;
			case PING:
				if (ClientAgent.doesInstanceExist())
				{
					ClientAgent.getInstance().setLastPingReceived(System.currentTimeMillis());
					new Thread(() ->
					{
						try
						{
							Thread.sleep(PING_FREQUENCY_MILLIS / 2);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						if (ClientAgent.doesInstanceExist())
							ctx.channel().writeAndFlush(PING + SEPARATOR + ClientAgent.getInstance().getLogin());
					}).start();
				}
				break;
		}
	}
}
