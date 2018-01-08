package TaxService.Netty;

import TaxService.Callback;
import TaxService.DAOs.AbstractDAO;
import TaxService.Deliveries.PortionDelivery;
import TaxService.Deliveries.AllDelivery;
import TaxService.Deliveries.QueryResultDelivery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.util.Pair;
import sun.awt.Mutex;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static TaxService.PhraseBook.CONNECTION_TIMEOUT_MILLIS;
import static TaxService.PhraseBook.PING;
import static TaxService.PhraseBook.SEPARATOR;

public class ClientAgent implements Closeable
{
	//STATIC SECTION
	private static ClientAgent instance = null;
	public static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

	private static Mutex authSubsMutex = new Mutex();
	private static ArrayList<Callback> authSubs = new ArrayList<>();
	private static Mutex portionReceivedSubsMutex = new Mutex();
	private static ArrayList<Callback> portionReceivedSubs = new ArrayList<>();
	private static Mutex allReceivedSubsMutex = new Mutex();
	private static ArrayList<Callback> allReceivedSubs = new ArrayList<>();
	private static Mutex exceptionReceivedSubsMutex = new Mutex();
	private static ArrayList<Callback> exceptionReceivedSubs = new ArrayList<>();
	private static Mutex notificationReceivedSubsMutex = new Mutex();
	private static ArrayList<Callback> notificationReceivedSubs = new ArrayList<>();
	private static Mutex connectionLostSubsMutex = new Mutex();
	private static ArrayList<Callback> connectionLostSubs = new ArrayList<>();
	private static Mutex queryResultReceivedSubsMutex = new Mutex();
	private static ArrayList<Callback> queryResultReceivedSubs = new ArrayList<>();

	public static ClientAgent getInstance()
	{
		return instance;
	}
	public static boolean doesInstanceExist()
	{
		return instance != null;
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

	public static void publishAuth(Pair<String, String> result)
	{
		authSubsMutex.lock();
		for (Callback s : authSubs)
			s.callback(result);
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

	public static void subscribeAllReceived(Callback s)
	{
		allReceivedSubsMutex.lock();
		allReceivedSubs.add(s);
		allReceivedSubsMutex.unlock();
	}

	public static void unsubscribeAllReceived(Callback s)
	{
		allReceivedSubsMutex.lock();
		allReceivedSubs.remove(s);
		allReceivedSubsMutex.unlock();
	}

	public static void publishAllReceived(AllDelivery<? extends AbstractDAO> delivery)
	{
		allReceivedSubsMutex.lock();
		for (Callback s : allReceivedSubs)
			s.callback(delivery);
		allReceivedSubsMutex.unlock();
	}

	public static void subscribeExceptionReceived(Callback s)
	{
		exceptionReceivedSubsMutex.lock();
		exceptionReceivedSubs.add(s);
		exceptionReceivedSubsMutex.unlock();
	}

	public static void unsubscribeExceptionReceived(Callback s)
	{
		exceptionReceivedSubsMutex.lock();
		exceptionReceivedSubs.remove(s);
		exceptionReceivedSubsMutex.unlock();
	}

	public static void publishExceptionReceived(String msg)
	{
		exceptionReceivedSubsMutex.lock();
		for (Callback s : exceptionReceivedSubs)
			s.callback(msg);
		exceptionReceivedSubsMutex.unlock();
	}

	public static void subscribeNotificationReceived(Callback s)
	{
		notificationReceivedSubsMutex.lock();
		notificationReceivedSubs.add(s);
		notificationReceivedSubsMutex.unlock();
	}

	public static void unsubscribeNotificationReceived(Callback s)
	{
		notificationReceivedSubsMutex.lock();
		notificationReceivedSubs.remove(s);
		notificationReceivedSubsMutex.unlock();
	}

	public static void publishNotificationReceived(String msg)
	{
		notificationReceivedSubsMutex.lock();
		for (Callback s : notificationReceivedSubs)
			s.callback(msg);
		notificationReceivedSubsMutex.unlock();
	}

	public static void subscribeConnectionLost(Callback s)
	{
		connectionLostSubsMutex.lock();
		connectionLostSubs.add(s);
		connectionLostSubsMutex.unlock();
	}

	public static void unsubscribeConnectionLost(Callback s)
	{
		connectionLostSubsMutex.lock();
		connectionLostSubs.remove(s);
		connectionLostSubsMutex.unlock();
	}

	public static void publishConnectionLost(String msg)
	{
		connectionLostSubsMutex.lock();
		for (Callback s : connectionLostSubs)
			s.callback(msg);
		connectionLostSubsMutex.unlock();
	}

	public static void subscribeQueryResultReceived(Callback s)
	{
		queryResultReceivedSubsMutex.lock();
		queryResultReceivedSubs.add(s);
		queryResultReceivedSubsMutex.unlock();
	}

	public static void unsubscribeQueryResultReceived(Callback s)
	{
		queryResultReceivedSubsMutex.lock();
		queryResultReceivedSubs.remove(s);
		queryResultReceivedSubsMutex.unlock();
	}

	public static void publishQueryResultReceived(QueryResultDelivery msg)
	{
		queryResultReceivedSubsMutex.lock();
		for (Callback s : queryResultReceivedSubs)
			s.callback(msg);
		queryResultReceivedSubsMutex.unlock();
	}

	//NON-STATIC SECTION
	private volatile boolean pingLoop;
	private long lastPingReceived;

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
		if (group != null)
			group.shutdownGracefully();
		if (instance != null)
			instance = null;
	}

	public long getLastPingReceived()
	{
		return lastPingReceived;
	}

	public void setLastPingReceived(long lastPingReceived)
	{
		this.lastPingReceived = lastPingReceived;
	}

	public void startPinging()
	{
		send(PING + SEPARATOR);
		pingLoop = true;
		new Thread(() ->
		{
			try
			{
				while (pingLoop)
				{
					//System.out.println("Ping check");
					Thread.sleep(CONNECTION_TIMEOUT_MILLIS / 2);
					if (pingLoop)
					{
						Thread.sleep(CONNECTION_TIMEOUT_MILLIS / 2);
						if (System.currentTimeMillis() - lastPingReceived > CONNECTION_TIMEOUT_MILLIS)
						{
							stopPinging();
							publishConnectionLost(null);
						}
					}
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	public void stopPinging()
	{
		pingLoop = false;
	}
}
