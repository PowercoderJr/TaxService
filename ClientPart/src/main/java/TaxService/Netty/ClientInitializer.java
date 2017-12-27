package TaxService.Netty;

import TaxService.Netty.Handlers.AllReceiver;
import TaxService.Netty.Handlers.QueryResultReceiver;
import TaxService.Netty.Handlers.SimpleMessageHandler;
import TaxService.Netty.Handlers.PortionReceiver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel>
{
	@Override
	protected void initChannel(SocketChannel ch) throws Exception
	{
		System.out.println("Something happened on client");
		ChannelPipeline pipeline = ch.pipeline();
		//pipeline.addLast(LineBasedFrameDecoder.class.getName(), new LineBasedFrameDecoder(256));
		pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast("encoder", new ObjectEncoder());
		pipeline.addLast(new SimpleMessageHandler());
		pipeline.addLast(new PortionReceiver());
		pipeline.addLast(new AllReceiver());
		pipeline.addLast(new QueryResultReceiver());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		super.exceptionCaught(ctx, cause);
	}
}
