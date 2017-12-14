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
import java.util.List;

public class ClientAgent implements Closeable
{
	//STATIC SECTION
	private static ClientAgent instance = null;

	private static Mutex authSubsMutex = new Mutex();
	public static ArrayList<Callback> authSubs = new ArrayList<>();
	private static Mutex tcReceivedSubsMutex = new Mutex();
	public static ArrayList<Callback> tcReceivedSubs = new ArrayList<>();

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
		authSubsMutex.lock();
		authSubs.add(s);
		authSubsMutex.unlock();
	}

	public static void unsubscribeAuth(Callback s)
	{
		authSubsMutex.lock();
		authSubs.remove(s);
		authSubsMutex.unlock();
	}

	public static void publishAuth(boolean accessed)
	{
		authSubsMutex.lock();
		for (Callback s : authSubs)
			s.callback(accessed);
		authSubsMutex.unlock();
	}

	public static void subscribeTableContentReceived(Callback s)
	{
		tcReceivedSubsMutex.lock();
		tcReceivedSubs.add(s);
		tcReceivedSubsMutex.unlock();
	}

	public static void unsubscribeTableContentReceived(Callback s)
	{
		tcReceivedSubsMutex.lock();
		tcReceivedSubs.remove(s);
		tcReceivedSubsMutex.unlock();
	}

	public static void publishTableContentReceived(List content)
	{
		tcReceivedSubsMutex.lock();
		for (Callback s : tcReceivedSubs)
			s.callback(content);
		tcReceivedSubsMutex.unlock();
	}

	//NON-STATIC SECTION
	private ChannelFuture future;
	private EventLoopGroup group;
	private String login;

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

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	protected void finalize()
	{
		close();
	}
}
