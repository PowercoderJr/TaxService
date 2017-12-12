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

	public boolean signIn(String login, String digest)
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
	}

	public void create(AbstractDAO dao)
	{
		AbstractCRUD crud = getCrudForClass(dao.getClass());
		if (crud != null)
			crud.create(dao);
	}

	public List readHundred(Class clazz, int hundred)
	{
		AbstractCRUD instance = getCrudForClass(clazz);
		return instance.readHundred(hundred);
	}

	public void delete(AbstractDAO dao)
	{
		AbstractCRUD crud = getCrudForClass(dao.getClass());
		if (crud != null)
			crud.delete(dao);
	}

	private AbstractCRUD getCrudForClass(Class clazz)
	{
		AbstractCRUD instance = null;
		try
		{
			Class crudClass = Class.forName("TaxService.CRUDs." + clazz.getSimpleName() + "CRUD");
			instance = (AbstractCRUD) crudClass.getDeclaredConstructor(SessionFactory.class).newInstance(sessionFactory);
		}
		catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return instance;
	}

	public java.util.Dictionary<String, Connection> getConnections()
	{
		return connections;
	}
}
