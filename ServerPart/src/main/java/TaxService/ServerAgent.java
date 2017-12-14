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
import java.sql.SQLException;
import java.util.List;

import static TaxService.PhraseBook.PORT;

public class ServerAgent implements Closeable
{
	private static ServerAgent instance = null;

	public static ServerAgent getInstance()
	{
		if (instance == null)
			instance = new ServerAgent();
		return instance;
	}

	//https://docs.oracle.com/cd/E13222_01/wls/docs81/ConsoleHelp/jdbc_connection_pools.html ?
	private java.util.Dictionary <String, Connection> connections;
	private NioEventLoopGroup acceptorGroup;
	private NioEventLoopGroup handlerGroup;
	private ChannelFuture future;

	private ServerAgent()
	{
		//https://metabroadcast.com/blog/java-socket-programming-with-netty
		acceptorGroup = new NioEventLoopGroup(2);
		handlerGroup = new NioEventLoopGroup(10);
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
		//sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	// TODO: 01.11.2017 Закрыть как полагается
	@Override
	public void close()
	{
		try
		{
			future.channel().closeFuture().sync();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		acceptorGroup.shutdownGracefully();
		handlerGroup.shutdownGracefully();
	}

	/*public boolean signIn(String login, String digest)
	{
		boolean accessed;
		try (Session session = sessionFactory.openSession())
		{
			StrangeThing result = session.createNativeQuery("SELECT * FROM StrangeThing WHERE login='" + login + "'", StrangeThing.class).getSingleResult();
			accessed = result != null && login.equals(result.getLogin()) && digest.equals(result.getDigest());
		}
		catch (Exception e)
		{
			accessed = false;
		}
		return accessed;
	}*/

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

	public List readHundred(Class clazz, String sendersLogin, int hundred)
	{
		AbstractCRUD instance = getCrudForClass(clazz, sendersLogin);
		List result = null;
		try
		{
			result = instance.readHundred(hundred);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public void delete(Class<AbstractDAO> clazz, String sendersLogin, long id) throws SQLException
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

	public AbstractCRUD getCrudForClass(Class clazz, Connection connection)
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

	public AbstractCRUD getCrudForClass(Class clazz, String sendersLogin)
	{
		return getCrudForClass(clazz, connections.get(sendersLogin));
	}

	public java.util.Dictionary<String, Connection> getConnections()
	{
		return connections;
	}
}
