package TaxService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<String>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception
	{
		System.out.println(ctx.channel().remoteAddress() + ": " + s);
		ctx.channel().write("OKAY");
	}
}
