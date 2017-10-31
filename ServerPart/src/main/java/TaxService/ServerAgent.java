package TaxService;

import TaxService.CRUDs.*;
import TaxService.DAO.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.TypedQuery;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import static TaxService.Dictionary.PORT;

public class ServerAgent implements Closeable
{
	private static ServerAgent instance = null;

	public static ServerAgent getInstance()
	{
		if (instance == null)
			instance = new ServerAgent();
		return instance;
	}

	private SessionFactory sessionFactory;
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
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	// TODO: 01.11.2017 Закрыть как полагается
	@Override
	public void close() throws IOException
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

	public boolean signup(String login, String digest)
	{
		boolean accessed = false;
		try (Session session = sessionFactory.openSession())
		{
			List<StrangeThing> rl = session.createQuery("SELECT a FROM StrangeThing a WHERE login='" + login+"'", StrangeThing.class).getResultList();
			if (!rl.isEmpty())
				accessed = login.equals(rl.get(0).getLogin()) && digest.equals(rl.get(0).getDigest());
		}
		return accessed;
	}
}
