package TaxService.Handlers;

import TaxService.CRUDs.EmployeeCRUD;
import TaxService.DAOs.Department;
import TaxService.Deliveries.QueryResultDelivery;
import TaxService.ServerAgent;
import io.netty.channel.ChannelHandlerContext;
import javafx.util.Pair;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
                if (ServerAgent.getInstance().getConnections().get(tokens[1]) != null)
                {
                    Pair<Connection, Long> updatedPair = new Pair<>(ServerAgent.getInstance().getConnections()
                            .get(tokens[1]).getKey(), System.currentTimeMillis());
                    ServerAgent.getInstance().getConnections().put(tokens[1], updatedPair);
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
                }
                ServerAgent.connectionsMutex.unlock();
                break;
            case BYE:
                ServerAgent.connectionsMutex.lock();
                try
                {
                    Map<String, Pair<Connection, Long>> connections = ServerAgent.getInstance().getConnections();
                    if (connections.get(tokens[1]) != null)
                    {
                        connections.get(tokens[1]).getKey().close();
                        connections.remove(tokens[1]);
                    }
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
            case QUERY:
                Pair<Connection, Long> pair = ServerAgent.getInstance().getConnections().get(tokens[1]);
                if (pair != null)
                {
                    Connection connection = pair.getKey();
                    try (Statement stmt = connection.createStatement())
                    {
                        ResultSet rs;
                        String header = null;
                        List<ArrayList> list = new ArrayList<>();
                        int nCol;
                        switch (tokens[2])
                        {
                            case "_1_1":
                                EmployeeCRUD employeeCRUD = new EmployeeCRUD(connection);
                                header = "Платежи, которые оформил(а) " + employeeCRUD.read(Long.parseLong(tokens[3]), true);
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2] + "(" + tokens[3] + ")");
                                nCol = 7;

                                ArrayList<String> colNames = new ArrayList<>(nCol);
                                colNames.add("ID платежа");
                                colNames.add("Тип платежа");
                                colNames.add("Сумма");
                                colNames.add("Дата");
                                colNames.add("ID плательщика");
                                colNames.add("Плательщик");
                                colNames.add("Телефон плательщика");
                                list.add(colNames);

                                while (rs.next())
                                {
                                    ArrayList<String> sublist = new ArrayList<>(nCol);
                                    for (int i = 1; i <= nCol; ++i)
                                        sublist.add(rs.getObject(i).toString());
                                    list.add(sublist);
                                }
                                break;
                        }
                        ctx.channel().writeAndFlush(new QueryResultDelivery(list, header, Date.valueOf(LocalDate.now())));
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                        ctx.channel().writeAndFlush(ERROR + SEPARATOR + e.getMessage());
                    }
                }
                break;
        }
    }
}
