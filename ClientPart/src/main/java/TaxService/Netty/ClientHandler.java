package TaxService.Netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception
	{
		System.out.println("Server: " + s);
	}
}
