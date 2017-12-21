package TaxService.Netty;

import TaxService.Callback;
import TaxService.DAOs.AbstractDAO;
import TaxService.Deliveries.PortionDelivery;
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

	private static Mutex authSubsMutex = new Mutex();
	public static ArrayList<Callback> authSubs = new ArrayList<>();
	private static Mutex portionReceivedSubsMutex = new Mutex();
	public static ArrayList<Callback> portionReceivedSubs = new ArrayList<>();

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

	public static void subscribePortionReceived(Callback s)
	{
		portionReceivedSubsMutex.lock();
		portionReceivedSubs.add(s);
		portionReceivedSubsMutex.unlock();
	}

	public static void unsubscribePortionReceived(Callback s)
	{
		portionReceivedSubsMutex.lock();
		portionReceivedSubs.remove(s);
		portionReceivedSubsMutex.unlock();
	}

	public static void publishPortionReceived(PortionDelivery<? extends AbstractDAO> delivery)
	{
		portionReceivedSubsMutex.lock();
		for (Callback s : portionReceivedSubs)
			s.callback(delivery);
		portionReceivedSubsMutex.unlock();
	}

	//NON-STATIC SECTION
	private ChannelFuture future;
	private EventLoopGroup group;
	private String login;
	private Class<? extends AbstractDAO> currTable;

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

	public Class<? extends AbstractDAO> getCurrTable()
	{
		return currTable;
	}

	public void setCurrTable(Class<? extends AbstractDAO> currTable)
	{
		this.currTable = currTable;
	}

	protected void finalize()
	{
		close();
	}
}
