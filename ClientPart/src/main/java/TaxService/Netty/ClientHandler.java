package TaxService.Netty;

import TaxService.POJO;
import TaxService.Dictionary;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		System.out.println("Server: " + msg);
		handle(ctx, msg);
	}

	private void handle(ChannelHandlerContext ctx, Object msg)
	{
		if (msg instanceof String)
		{
			String codegramm = ((String) msg).substring(0, ((String) msg).indexOf(Dictionary.SEPARATOR));
			String content = ((String) msg).substring(((String) msg).indexOf(Dictionary.SEPARATOR) + 1);

			switch (codegramm)
			{
				case Dictionary.ACCESS:
					ClientAgent.publishAuth(content.equals(Dictionary.YES));
					break;
			}
		}
		else if (msg instanceof POJO)
		{
			ClientAgent.publishSelect(msg);
		}
	}
}
