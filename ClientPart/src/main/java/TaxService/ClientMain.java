package TaxService;

import TaxService.Netty.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientMain extends Application
{
    public static final int PORT = 10650;
    public static final int DEFAULT_SCREEN_WIDTH = 1280;
    public static final int DEFAULT_SCREEN_HEIGHT = 720;
    public static SceneManager sceneManager;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        sceneManager = new SceneManager(primaryStage);

        Parent root = FXMLLoader.load(getClass().getResource("/MainScene/interface.fxml"));
        primaryStage.setTitle("Вас заарештовано!");
        ManagedScene mainScene = new ManagedScene(root, DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT, sceneManager);
        mainScene.getStylesheets().add("/MainScene/style.css");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        //launch(args);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        EventLoopGroup group = new NioEventLoopGroup();
        try
        {
            Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new ClientInitializer());
            Channel channel = bootstrap.connect(InetAddress.getLocalHost(), PORT).sync().channel();

            Socket socket = new Socket(InetAddress.getLocalHost(), PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String msg;
            do
            {
                msg = in.readLine();
                System.out.println("Me: " + msg);
                channel.write("through channel: " + msg);
                out.writeUTF("through socket: " + msg);
            } while (!msg.equals("stop"));
        }
        catch (InterruptedException | IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            group.shutdownGracefully();
        }
    }
}
