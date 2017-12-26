package TaxService;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.DAOs.AbstractDAO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javafx.util.Pair;
import sun.awt.Mutex;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static TaxService.PhraseBook.*;

public class ServerAgent implements Closeable
{
	private static ServerAgent instance = null;
	private static final int CONNECTIONS_AVAILABLE = 10;

	public static ServerAgent getInstance()
	{
		if (instance == null)
			instance = new ServerAgent();
		return instance;
	}

	public static Mutex connectionsMutex = new Mutex();
	private Map<String, Pair<Connection, Long>> connections;

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
		}
		catch (InterruptedException e)
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
					//System.out.println("Ping check");
					Thread.sleep(CONNECTION_TIMEOUT_MILLIS);
					List<String> toDisconnect = new ArrayList<>();
					connectionsMutex.lock();
					connections.forEach((login, pair) ->
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
							toDisconnect.add(login);
						}
					});
					toDisconnect.forEach(login ->
					{
						connections.remove(login);
						System.out.println(login + " disconnected");
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

	public int create(AbstractDAO dao, String sendersLogin) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(dao.getClass(), sendersLogin);
		if (crud != null)
			return crud.create(dao);
		return 0;
	}

	public List readPortion(Class<? extends AbstractDAO> clazz, String sendersLogin, int portion, boolean isLazy, String filter) throws SQLException
	{
		AbstractCRUD instance = getCrudForClass(clazz, sendersLogin);
		return instance.readPortion(portion, isLazy, filter);
	}

	public List readAll(Class<? extends AbstractDAO> clazz, String sendersLogin, boolean isLazy, String filter) throws SQLException
	{
		AbstractCRUD instance = getCrudForClass(clazz, sendersLogin);
		return instance.readAll(isLazy, filter);
	}

	public int update(Class<? extends AbstractDAO> clazz, String sendersLogin, String filter, String newValues) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, sendersLogin);
		if (crud != null)
			return crud.update(filter, newValues);
		return 0;
	}

	public int delete(Class<? extends AbstractDAO> clazz, String sendersLogin, long id) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, sendersLogin);
		if (crud != null)
			return crud.delete(id);
		return 0;
	}

	public int delete(AbstractDAO dao, String sendersLogin) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(dao.getClass(), sendersLogin);
		if (crud != null)
			return crud.delete(dao.getId());
		return 0;
	}

	public int delete(Class<? extends AbstractDAO> clazz, String sendersLogin, String filter) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, sendersLogin);
		if (crud != null)
			return crud.delete(filter);
		return 0;
	}

	public int count(Class<? extends AbstractDAO> clazz, String sendersLogin, String filter) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, sendersLogin);
		if (crud != null)
			return crud.count(filter);
		return 0;
	}

	/*public ResultSet executeCustomQuery(Class<? extends AbstractDAO> clazz, String sendersLogin, String query) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, sendersLogin);
		if (crud != null)
			return crud.executeCustomQuery(query);
		else
			return null;
	}*/

	public AbstractCRUD getCrudForClass(Class<? extends AbstractDAO> clazz, Connection connection)
	{
		AbstractCRUD instance = null;
		if (connection != null)
			try
			{
				Class crudClass = Class.forName("TaxService.CRUDs." + clazz.getSimpleName() + "CRUD");
				instance = (AbstractCRUD) crudClass.getDeclaredConstructor(Connection.class).newInstance(connection);
			}
			catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		return instance;
	}

	public AbstractCRUD getCrudForClass(Class<? extends AbstractDAO> clazz, String sendersLogin)
	{
		return getCrudForClass(clazz, connections.get(sendersLogin).getKey());
	}

	public Map<String, Pair<Connection, Long>> getConnections()
	{
		return connections;
	}
}
