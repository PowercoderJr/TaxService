package TaxService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ServerHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		System.out.println(ctx.channel().remoteAddress() + ": " + msg);
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
				case Dictionary.SIGN_IN:
					String login = content.substring(0, content.indexOf(Dictionary.SEPARATOR));
					String digest = content.substring(content.indexOf(Dictionary.SEPARATOR) + 1);
					boolean acceessed = ServerAgent.getInstance().signup(login, digest);
					ctx.channel().writeAndFlush(Dictionary.ACCESS + Dictionary.SEPARATOR + (acceessed ? Dictionary.YES : Dictionary.NO));
					break;
			}
		}
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

