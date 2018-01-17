package TaxService.Handlers;

import TaxService.DAOs.AbstractDAO;
import TaxService.Orders.CreateEmployeePlusAccountOrder;
import TaxService.Orders.CreateOrder;
import TaxService.PhraseBook;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;

public class CreateEmployeePlusAccountOrderHandler extends AbstractHandler<CreateEmployeePlusAccountOrder>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CreateEmployeePlusAccountOrder msg)
	{
		super.channelRead0(ctx, msg);
		try
		{
			int created = ServerAgent.getInstance().createEmployeePlusAccount(msg.getEmployee(), msg.getAccount(), ctx.channel().id());
			ctx.channel().writeAndFlush(PhraseBook.NOTIFICATION + PhraseBook.SEPARATOR + "Добавлено строк: " + created);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			ctx.channel().writeAndFlush(PhraseBook.ERROR + PhraseBook.SEPARATOR + e.getMessage());
		}
	}
}
