package TaxService;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.DAOs.AbstractDAO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static TaxService.PhraseBook.PORT;

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

	//https://docs.oracle.com/cd/E13222_01/wls/docs81/ConsoleHelp/jdbc_connection_pools.html ?
	private Map<String, Connection> connections;
	private NioEventLoopGroup acceptorGroup;
	private NioEventLoopGroup handlerGroup;
	private ChannelFuture future;

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
		connections = new HashMap<String, Connection>(CONNECTIONS_AVAILABLE);
	}

	// TODO: 01.11.2017 Закрыть как полагается
	@Override
	public void close()
	{
		try
		{
			for (Connection connection : connections.values())
				connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		/*try
		{
			future.channel().closeFuture().sync();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}*/
		future.channel().closeFuture();
		acceptorGroup.shutdownGracefully();
		handlerGroup.shutdownGracefully();
	}

	public void create(AbstractDAO dao, String sendersLogin)
	{
		AbstractCRUD crud = getCrudForClass(dao.getClass(), sendersLogin);
		if (crud != null)
			try
			{
				crud.create(dao);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
	}

	public List readPortion(Class<? extends AbstractDAO> clazz, String sendersLogin, int portion)
	{
		AbstractCRUD instance = getCrudForClass(clazz, sendersLogin);
		List result = null;
		try
		{
			result = instance.readPortion(portion, false);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public void delete(Class<? extends AbstractDAO> clazz, String sendersLogin, long id) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, sendersLogin);
		if (crud != null)
			crud.delete(id);
	}

	public void delete(AbstractDAO dao, String sendersLogin) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(dao.getClass(), sendersLogin);
		if (crud != null)
			crud.delete(dao.getId());
	}

	public int count(Class<? extends AbstractDAO> clazz, String sendersLogin) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, sendersLogin);
		if (crud != null)
			return crud.count();
		else
			return -1;
	}

	public ResultSet executeCustomQuery(Class<? extends AbstractDAO> clazz, String sendersLogin, String query) throws SQLException
	{
		AbstractCRUD crud = getCrudForClass(clazz, sendersLogin);
		if (crud != null)
			return crud.executeCustomQuery(query);
		else
			return null;
	}

	public AbstractCRUD getCrudForClass(Class<? extends AbstractDAO> clazz, Connection connection)
	{
		AbstractCRUD instance = null;
		try
		{
			Class crudClass = Class.forName("TaxService.CRUDs." + clazz.getSimpleName() + "CRUD");
			//TODO: обработать исключение, если по ключу не найдено подключенние
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
		return getCrudForClass(clazz, connections.get(sendersLogin));
	}

	public Map<String, Connection> getConnections()
	{
		return connections;
	}
}
