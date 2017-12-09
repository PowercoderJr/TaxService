package TaxService;

import TaxService.DAOs.Department;
import TaxService.DAOs.Deptype;
import TaxService.DAOs.Education;
import TaxService.DAOs.Employee;
import TaxService.Orders.AbstractOrder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class ServerHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		System.out.println(ctx.channel().remoteAddress() + ": " + msg);
		handle(ctx, msg);
	}

	private void handle(ChannelHandlerContext ctx, Object msg)
	{
		if (msg instanceof AbstractOrder)
		{
			;
		}
		else if (msg instanceof String)
		{
			String codegramm = ((String) msg).substring(0, ((String) msg).indexOf(Dictionary.SEPARATOR));
			String content = ((String) msg).substring(((String) msg).indexOf(Dictionary.SEPARATOR) + 1);

			switch (codegramm)
			{
				case Dictionary.AUTH:
					String login = content.substring(0, content.indexOf(Dictionary.SEPARATOR));
					String digest = content.substring(content.indexOf(Dictionary.SEPARATOR) + 1);
					boolean acceessed = ServerAgent.getInstance().signIn(login, digest);
					ctx.channel().writeAndFlush(Dictionary.ACCESS + Dictionary.SEPARATOR + (acceessed ? Dictionary.YES : Dictionary.NO));
					break;
				case Dictionary.SELECT:
					switch (content)
					{
						case "1":
							ctx.channel().writeAndFlush(new Department("Кековское", new Deptype("Районное"), new BigDecimal(2000), "+380914564564", "Danger", "Синяя", "13"));
							break;
						case "2":
							ctx.channel().writeAndFlush(new Employee("Az1", "Az2", "Az3", new Department(), Date.valueOf(LocalDate.of(2013, 12,13)), "Master", 12345, new Education("Super")));
					}
			}
		}

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}

