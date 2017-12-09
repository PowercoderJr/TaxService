package TaxService.Netty;

import TaxService.Callback;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import sun.awt.Mutex;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;

public class ClientAgent implements Closeable
{
	//STATIC SECTION
	private static ClientAgent instance = null;

	private static Mutex authSubscribersMutex = new Mutex();
	public static ArrayList<Callback> authSubscribers = new ArrayList<>();
	private static Mutex selectSubscribersMutex = new Mutex();
	public static ArrayList<Callback> selectSubscribers = new ArrayList<>();

	public static ClientAgent getInstance()
	{
		return instance;
	}

	public static void buildInstance(InetAddress inetAddress, int port) throws InvocationTargetException
	{
		if (instance != null)
			instance.close();

		instance = new ClientAgent(inetAddress, port);
		try
		{
			if (instance.future.isSuccess())
				instance.future.sync();
			else
			{
				instance.close();
				throw new InvocationTargetException(null);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public static void subscribeAuth(Callback s)
	{
		authSubscribersMutex.lock();
		authSubscribers.add(s);
		authSubscribersMutex.unlock();
	}

	public static void unsubscribeAuth(Callback s)
	{
		authSubscribersMutex.lock();
		authSubscribers.remove(s);
		authSubscribersMutex.unlock();
	}

	public static void publishAuth(boolean accessed)
	{
		authSubscribersMutex.lock();
		for (Callback s : authSubscribers)
			s.callback(accessed);
		authSubscribersMutex.unlock();
	}

	public static void subscribeSelect(Callback s)
	{
		selectSubscribersMutex.lock();
		selectSubscribers.add(s);
		selectSubscribersMutex.unlock();
	}

	public static void unsubscribeSelect(Callback s)
	{
		selectSubscribersMutex.lock();
		selectSubscribers.remove(s);
		selectSubscribersMutex.unlock();
	}

	public static void publishSelect(Object result)
	{
		selectSubscribersMutex.lock();
		for (Callback s : selectSubscribers)
			s.callback(result);
		selectSubscribersMutex.unlock();
	}

	//NON-STATIC SECTION
	private ChannelFuture future;
	private EventLoopGroup group;

	private ClientAgent(InetAddress inetAddress, int port)
	{
		group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap().group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ClientInitializer());
		try
		{
			future = bootstrap.connect(inetAddress, port).await();
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
		//future.channel().closeFuture();
		if (group != null)
			group.shutdownGracefully();
		if (instance != null)
			instance = null;
	}

	protected void finalize()
	{
		close();
	}
}
