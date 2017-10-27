package TaxService;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ServerInitializer extends ChannelInitializer<SocketChannel>
{
	@Override
	public void initChannel(SocketChannel ch) throws Exception
	{
		System.out.println("Something happened on server");
		ChannelPipeline pipeline = ch.pipeline();
		//pipeline.addLast(LineBasedFrameDecoder.class.getName(), new LineBasedFrameDecoder(256));
		pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
		//pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
		pipeline.addLast(ServerHandler.class.getName(), new ServerHandler());
	}
}
