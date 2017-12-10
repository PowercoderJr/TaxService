package TaxService.Handlers;

import TaxService.POJO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractHandler<T> extends SimpleChannelInboundHandler<T>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception
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
