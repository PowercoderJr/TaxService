package TaxService.Handlers;

import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static TaxService.PhraseBook.*;

public class SimpleMessageHandler extends AbstractHandler<String>
{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
    {
        super.channelRead0(ctx, msg);
        String[] tokens = msg.split("\\" + SEPARATOR);
        switch (tokens[0])
        {
            case AUTH:
                String accessResult = ACCESS_RESULT_INVALID_LOGIN_PASSWORD;
                Connection newConnection;
                try
                {
                    ServerAgent.connectionsMutex.lock();
                    Map<String, Pair<Connection, Long>> connections = ServerAgent.getInstance().getConnections();
                    Pair<Connection, Long> oldPair = connections.get(tokens[1]);
                    if (oldPair == null || !oldPair.getKey().isValid(10))
                    {
                        if (oldPair != null)
                        {
                            oldPair.getKey().close();
                            connections.remove(tokens[1]);
                        }
                        newConnection = DriverManager.getConnection("jdbc:postgresql://localhost/TaxService", tokens[1], tokens[2]);
                        connections.put(tokens[1], new Pair<>(newConnection, System.currentTimeMillis()));
                        accessResult = ACCESS_RESULT_SUCCESS;
                    }
                    else
                        accessResult = ACCESS_RESULT_ALREADY_LOGGED;
                }
                catch (SQLException e)
                {
                    if (!e.getMessage().contains("password authentication failed"))
                    {
                        e.printStackTrace();
                        ctx.channel().writeAndFlush(ERROR + SEPARATOR + e.getMessage());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    ServerAgent.connectionsMutex.unlock();
                }
                ctx.channel().writeAndFlush(ACCESS + SEPARATOR + accessResult);
                break;
            case PING:
                ServerAgent.connectionsMutex.lock();
                Pair<Connection, Long> updatedPair = new Pair<>(ServerAgent.getInstance().getConnections().get(tokens[1]).getKey(),
                        System.currentTimeMillis());
                ServerAgent.getInstance().getConnections().put(tokens[1], updatedPair);
                ServerAgent.connectionsMutex.unlock();
                new Thread(() ->
                {
                    try
                    {
                        Thread.sleep(PING_FREQUENCY_MILLIS / 2);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ctx.channel().writeAndFlush(PING + SEPARATOR);
                }).start();
                break;
            case BYE:
                ServerAgent.connectionsMutex.lock();
                try
                {
                    Map<String, Pair<Connection, Long>> connections = ServerAgent.getInstance().getConnections();
                    connections.get(tokens[1]).getKey().close();
                    connections.remove(tokens[1]);
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    ServerAgent.connectionsMutex.unlock();
                }
                System.out.println(tokens[1] + " disconnected");
                break;
        }
    }
}
