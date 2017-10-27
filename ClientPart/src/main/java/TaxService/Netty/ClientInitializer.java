package TaxService.Netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ClientInitializer extends ChannelInitializer<SocketChannel>
{
	@Override
	protected void initChannel(SocketChannel ch) throws Exception
	{
		System.out.println("Something happened on client");
		ChannelPipeline pipeline = ch.pipeline();
		//pipeline.addLast(LineBasedFrameDecoder.class.getName(), new LineBasedFrameDecoder(256));
		pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
		//pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
		pipeline.addLast(ClientHandler.class.getName(), new ClientHandler());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		super.exceptionCaught(ctx, cause);
	}
}
