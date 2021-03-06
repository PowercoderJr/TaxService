package TaxService;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.DAOs.AbstractDAO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javafx.util.Pair;
import sun.awt.Mutex;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static TaxService.PhraseBook.*;

public class ServerAgent implements Closeable
{
	public static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	private static ServerAgent instance = null;
	private static final int CONNECTIONS_AVAILABLE = 10;

	public static ServerAgent getInstance()
	{
		if (instance == null)
			instance = new ServerAgent();
		return instance;
	}

	public static Mutex connectionsMutex = new Mutex();
	private Map<ChannelId, Pair<Connection, Long>> connections;
	private Connection superConnection;

	private NioEventLoopGroup acceptorGroup;
	private NioEventLoopGroup handlerGroup;
	private ChannelFuture future;
	private volatile boolean pingLoop;

	private ServerAgent()
	{
		//https://metabroadcast.com/blog/java-socket-programming-with-netty
		acceptorGroup = new NioEventLoopGroup(2);
		handlerGroup = new NioEventLoopGroup(CONNECTIONS_AVAILABLE);
		try
		{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(acceptorGroup, handlerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 5)
					.childHandler(new ServerInitializer())
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			future = bootstrap.localAddress(PORT).bind().sync();
			superConnection = DriverManager.getConnection("jdbc:postgresql://localhost/TaxService", "postgres", "userpass");
		}
		catch (InterruptedException | SQLException e)
		{
			e.printStackTrace();
		}
		connections = new HashMap<>(CONNECTIONS_AVAILABLE);

		pingLoop = true;
		new Thread(() ->
		{
			try
			{
				while (pingLoop)
				{
					Thread.sleep(CONNECTION_TIMEOUT_MILLIS);
					List<ChannelId> toDisconnect = new ArrayList<>();
					connectionsMutex.lock();
					connections.forEach((channelId, pair) ->
					{
						if (System.currentTimeMillis() - pair.getValue() > CONNECTION_TIMEOUT_MILLIS)
						{
							try
							{
								pair.getKey().close();
							}
							catch (SQLException e)
							{
								e.printStackTrace();
							}
							toDisconnect.add(channelId);
						}
					});
					toDisconnect.forEach(channelId ->
					{
						connections.remove(channelId);
						System.out.println(channelId.asLongText() + " disconnected");
					});
					connectionsMutex.unlock();
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	// TODO: 01.11.2017 Закрыть как полагается
	@Override
	public void close()
	{
		try
		{
			connectionsMutex.lock();
			for (Pair<Connection, Long> pair : connections.values())
				pair.getKey().close();
			connections.clear();
			superConnection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			connectionsMutex.unlock();
		}
		/*try
		{
			future.channel().closeFuture().sync();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}*/
		pingLoop = false;
		future.channel().closeFuture();
		acceptorGroup.shutdownGracefully();
		handlerGroup.shutdownGracefully();
	}

	public int create(AbstractDAO dao, ChannelId channelId) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(dao.getClass(), channelId);
		if (crud != null)
			return crud.create(dao);
		return 0;
	}

	public <T extends AbstractDAO> List readPortion(Class<T> clazz, ChannelId channelId, int portion, boolean isLazy, String filter) throws SQLException
	{
		AbstractCRUD instance = getCrudForClass(clazz, channelId);
		return instance.readPortion(portion, isLazy, filter);
	}

	public <T extends AbstractDAO> List readAll(Class<T> clazz, ChannelId channelId, boolean isLazy, String filter) throws SQLException
	{
		AbstractCRUD instance = getCrudForClass(clazz, channelId);
		return instance.readAll(isLazy, filter);
	}

	public <T extends AbstractDAO> int update(Class<T> clazz, ChannelId channelId, String filter, String newValues) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, channelId);
		if (crud != null)
			return crud.update(filter, newValues);
		return 0;
	}

	public <T extends AbstractDAO> int delete(Class<T> clazz, ChannelId channelId, long id) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, channelId);
		if (crud != null)
			return crud.delete(id, null);
		return 0;
	}

	public int delete(AbstractDAO dao, ChannelId channelId) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(dao.getClass(), channelId);
		if (crud != null)
			return crud.delete(dao.getId(), null);
		return 0;
	}

	public <T extends AbstractDAO> int delete(Class<T> clazz, ChannelId channelId, String filter) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, channelId);
		if (crud != null)
			return crud.delete(filter);
		return 0;
	}

	public <T extends AbstractDAO> int count(Class<T> clazz, ChannelId channelId, String filter) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, channelId);
		if (crud != null)
			return crud.count(filter);
		return 0;
	}

	public <T extends AbstractDAO> AbstractCRUD getCrudForClass(Class<T> clazz, Connection connection, Connection superConnection)
	{
		AbstractCRUD instance = null;
		if (connection != null)
			try
			{
				Class crudClass = Class.forName("TaxService.CRUDs." + clazz.getSimpleName() + "CRUD");
				instance = (AbstractCRUD) crudClass.getDeclaredConstructor(Connection.class, Connection.class).newInstance(connection, superConnection);
			}
			catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		return instance;
	}

	public <T extends AbstractDAO> AbstractCRUD getCrudForClass(Class<T> clazz, ChannelId channelId)
	{
		return getCrudForClass(clazz, connections.get(channelId).getKey(), superConnection);
	}

	public Map<ChannelId, Pair<Connection, Long>> getConnections()
	{
		return connections;
	}

	public Connection getSuperConnection()
	{
		return superConnection;
	}
}
