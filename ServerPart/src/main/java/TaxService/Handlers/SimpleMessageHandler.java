package TaxService.Handlers;

import TaxService.Dictionary;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

public class SimpleMessageHandler extends AbstractHandler<String>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
	{
		super.channelRead0(ctx, msg);
		String[] tokens = msg.split("\\" + Dictionary.SEPARATOR);
		switch (tokens[0])
		{
			case Dictionary.AUTH:
				boolean acceessed = ServerAgent.getInstance().signIn(tokens[1], tokens[2]);
				ctx.channel().writeAndFlush(Dictionary.ACCESS + Dictionary.SEPARATOR + (acceessed ? Dictionary.YES : Dictionary.NO));
				break;
			/*case Dictionary.SELECT:
				switch (tokens[1])
				{
					case "1":
						ctx.channel().writeAndFlush(new Department("Кековское", new Deptype("Районное"), new BigDecimal(2000), "+380914564564", "Danger", "Синяя", "13"));
						break;
					case "2":
						ctx.channel().writeAndFlush(new Employee("Az1", "Az2", "Az3", new Department(), Date.valueOf(LocalDate.of(2013, 12,13)), new Post("Master"), 12345, new Education("Super")));
				}*/
		}
	}
}

