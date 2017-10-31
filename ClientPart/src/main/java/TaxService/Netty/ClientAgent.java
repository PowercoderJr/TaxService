package TaxService.Netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static TaxService.Dictionary.PORT;


public class ClientAgent implements Closeable
{
	private static ClientAgent instance = null;

	public static ClientAgent getInstance()
	{
		if (instance == null)
			try
			{
				instance = new ClientAgent(InetAddress.getLocalHost(), PORT);
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
		return instance;
	}

	private ChannelFuture future;
	private EventLoopGroup group;

	private ClientAgent(InetAddress inetAddress, int port)
	{
		try
		{
			group = new NioEventLoopGroup();
			Bootstrap bootstrap = new Bootstrap().group(group)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ClientInitializer());
			future = bootstrap.connect(inetAddress, port).sync();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void send(Object msg)
	{
		try
		{
			future.channel().writeAndFlush(msg);
			future.sync();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		group.shutdownGracefully();
	}

	protected void finalize()
	{
		close();
	}
}
