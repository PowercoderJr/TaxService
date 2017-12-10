package TaxService.Handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

//Для тестов
public class ObjectHandler extends SimpleChannelInboundHandler<Object>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		System.out.println(ctx.channel().remoteAddress() + ": " + msg.toString());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
