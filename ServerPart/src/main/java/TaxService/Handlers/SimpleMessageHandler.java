package TaxService.Handlers;

import TaxService.CRUDs.EmployeeCRUD;
import TaxService.CRUDs.OwntypeCRUD;
import TaxService.CRUDs.PostCRUD;
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
                        ArrayList<String> colNames = new ArrayList<>();
                        int nCol;
                        switch (tokens[2])
                        {
                            case "_1_1":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2] + "(" + tokens[3] + ")");

                                colNames.add("ID платежа");
                                colNames.add("Тип платежа");
                                colNames.add("Сумма");
                                colNames.add("Дата");
                                colNames.add("ID плательщика");
                                colNames.add("Плательщик");
                                colNames.add("Телефон плательщика");
                                nCol = colNames.size();
                                list.add(colNames);

                                while (rs.next())
                                {
                                    ArrayList<String> sublist = new ArrayList<>(nCol);
                                    for (int i = 1; i <= nCol; ++i)
                                        sublist.add(rs.getObject(i).toString());
                                    list.add(sublist);
                                }
                                EmployeeCRUD employeeCRUD = new EmployeeCRUD(connection);
                                header = "Платежи, которые оформил(а) " + employeeCRUD
                                        .read(Long.parseLong(tokens[3]), true) + "   - " + (list.size() - 1) + " зап.";
                                break;
                            }
                            case "_1_2":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2] + "('" + tokens[3] + "')");

                                colNames.add("ID платежа");
                                colNames.add("Тип платежа");
                                colNames.add("Сумма");
                                colNames.add("Дата");
                                colNames.add("ID сотрудника");
                                colNames.add("Фамилия (сотрудник)");
                                colNames.add("Имя (сотрудник)");
                                colNames.add("Отчество (сотрудник)");
                                colNames.add("ID плательщика");
                                colNames.add("Плательщик");
                                colNames.add("Телефон плательщика");
                                nCol = colNames.size();
                                list.add(colNames);

                                while (rs.next())
                                {
                                    ArrayList<String> sublist = new ArrayList<>(nCol);
                                    for (int i = 1; i <= nCol; ++i)
                                        sublist.add(rs.getObject(i).toString());
                                    list.add(sublist);
                                }
                                header = "Платежи, оформленные, начиная с " + ServerAgent.df
                                        .format(Date.valueOf(tokens[3])) + "   - " + (list.size() - 1) + " зап.";
                                break;
                            }
                            case "_1_3":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2] + "(" + tokens[3] + ",'" + tokens[4] + "')");

                                colNames.add("ID платежа");
                                colNames.add("Тип платежа");
                                colNames.add("Сумма");
                                colNames.add("Дата");
                                colNames.add("ID плательщика");
                                colNames.add("Плательщик");
                                colNames.add("Телефон плательщика");
                                nCol = colNames.size();
                                list.add(colNames);

                                while (rs.next())
                                {
                                    ArrayList<String> sublist = new ArrayList<>(nCol);
                                    for (int i = 1; i <= nCol; ++i)
                                        sublist.add(rs.getObject(i).toString());
                                    sublist.set(3, ServerAgent.df.format(Date.valueOf(sublist.get(3))));
                                    list.add(sublist);
                                }
                                EmployeeCRUD employeeCRUD = new EmployeeCRUD(connection);
                                header = "Платежи, которые оформил(а) " + employeeCRUD
                                        .read(Long.parseLong(tokens[3]), true) + ", начиная с " + ServerAgent.df
                                        .format(Date.valueOf(tokens[4])) + "   - " + (list.size() - 1) + " зап.";
                                break;
                            }
                            case "_2_1":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2]);

                                colNames.add("ID");
                                colNames.add("Название");
                                colNames.add("Тип отделения");
                                colNames.add("Город");
                                colNames.add("Фамилия");
                                colNames.add("Имя");
                                colNames.add("Отчество");
                                colNames.add("Долность");
                                colNames.add("Зарплата");
                                nCol = colNames.size();
                                list.add(colNames);

                                while (rs.next())
                                {
                                    ArrayList<String> sublist = new ArrayList<>(nCol);
                                    for (int i = 1; i <= nCol; ++i)
                                        sublist.add(rs.getObject(i).toString());
                                    list.add(sublist);
                                }
                                header = "Список сотрудников каждого отделения" + "   - " + (list.size() - 1) + " зап.";
                                break;
                            }
                            case "_3":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2]);

                                colNames.add("ID");
                                colNames.add("Название");
                                colNames.add("Дата");
                                nCol = colNames.size();
                                list.add(colNames);

                                while (rs.next())
                                {
                                    ArrayList<String> sublist = new ArrayList<>(nCol);
                                    for (int i = 1; i <= nCol; ++i)
                                        sublist.add(rs.getObject(i) == null ? "" : rs.getObject(i).toString());
                                    list.add(sublist);
                                }
                                header = "Дата последнего платежа каждого предприятя";
                                break;
                            }
                            case "_6":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2]);

                                colNames.add("Значение");
                                nCol = colNames.size();
                                list.add(colNames);

                                rs.next();
                                ArrayList<String> sublist = new ArrayList<>(nCol);
                                String tmp = rs.getObject(1).toString();
                                sublist.add(tmp.substring(0, tmp.indexOf('.') + 3));
                                list.add(sublist);
                                header = "Средний размер штата предприятий в базе данных";
                                break;
                            }
                            case "_7":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2]);

                                colNames.add("Образование");
                                colNames.add("Должность");
                                colNames.add("Количество");
                                nCol = colNames.size();
                                list.add(colNames);

                                while (rs.next())
                                {
                                    ArrayList<String> sublist = new ArrayList<>(nCol);
                                    for (int i = 1; i <= nCol; ++i)
                                        sublist.add(rs.getObject(i) == null ? "" : rs.getObject(i).toString());
                                    list.add(sublist);
                                }
                                header = "Количество сотрудников налоговой инспекции по категориям степени образования и занимаемых должностей";
                                break;
                            }
                            case "_8_1":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2] + "(" + tokens[3] + ")");

                                colNames.add("Значение");
                                nCol = colNames.size();
                                list.add(colNames);

                                rs.next();
                                ArrayList<String> sublist = new ArrayList<>(nCol);
                                String tmp = rs.getObject(1).toString();
                                sublist.add(tmp.substring(0, tmp.indexOf('.') + 3));
                                list.add(sublist);
                                PostCRUD postCRUD = new PostCRUD(connection);
                                header = "Средний размер зарплаты среди должности " + postCRUD.read(Long.parseLong(tokens[3]), true);
                                break;
                            }
                            case "_8_2":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2] + "('" + tokens[3] + "')");

                                colNames.add("Значение");
                                nCol = colNames.size();
                                list.add(colNames);

                                rs.next();
                                ArrayList<String> sublist = new ArrayList<>(nCol);
                                sublist.add(rs.getObject(1).toString());
                                list.add(sublist);
                                header = "Количество отделений налоговой инспекции, телефонный оператор которых имеет код " + tokens[3];
                                break;
                            }
                            case "_9":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2] + "(" + tokens[3] + ")");

                                colNames.add("ID");
                                colNames.add("Название");
                                colNames.add("Сумма платежей");
                                nCol = colNames.size();
                                list.add(colNames);

                                while (rs.next())
                                {
                                    ArrayList<String> sublist = new ArrayList<>(nCol);
                                    for (int i = 1; i <= nCol; ++i)
                                        sublist.add(rs.getObject(i).toString());
                                    sublist.set(2, sublist.get(2).substring(0, sublist.get(2).indexOf('.') + 3));
                                    list.add(sublist);
                                }
                                header = "Предприятия, которые оформили платежей на сумму меньше, чем " + tokens[3];
                                break;
                            }
                            case "_10":
                            {
                                rs = stmt.executeQuery("select * from " + QUERY + tokens[2] + "(" + tokens[3] + "," + tokens[4] + ")");

                                colNames.add("ID");
                                colNames.add("Название");
                                colNames.add("Сумма платежей");
                                nCol = colNames.size();
                                list.add(colNames);

                                while (rs.next())
                                {
                                    ArrayList<String> sublist = new ArrayList<>(nCol);
                                    for (int i = 1; i <= nCol; ++i)
                                        sublist.add(rs.getObject(i).toString());
                                    sublist.set(2, sublist.get(2).substring(0, sublist.get(2).indexOf('.') + 3));
                                    list.add(sublist);
                                }
                                OwntypeCRUD owntypeCRUD = new OwntypeCRUD(connection);
                                header = "Предприятия типа \"" + owntypeCRUD.read(Long.parseLong(tokens[3]), true) +
                                        "\", которые оформили платежей на сумму меньше, чем " + tokens[4];
                                break;
                            }
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
