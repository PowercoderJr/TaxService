package TaxService;

import TaxService.Handlers.AbstractHandler;
import TaxService.Handlers.CreateOrderHandler;
import TaxService.Handlers.ObjectHandler;
import TaxService.Handlers.SimpleMessageHandler;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel>
{
	@Override
	public void initChannel(SocketChannel ch) throws Exception
	{
		System.out.println("Something happened on server");
		ChannelPipeline pipeline = ch.pipeline();
		//pipeline.addLast(LineBasedFrameDecoder.class.getName(), new LineBasedFrameDecoder(256));
		pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast("encoder", new ObjectEncoder());
		pipeline.addLast(new SimpleMessageHandler());
		pipeline.addLast(new CreateOrderHandler());
		pipeline.addLast(new ObjectHandler());
	}
}
