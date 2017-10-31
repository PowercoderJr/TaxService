package TaxService;

import TaxService.Netty.ClientAgent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ClientMain extends Application
{
    public static final int DEFAULT_SCREEN_WIDTH = 1280;
    public static final int DEFAULT_SCREEN_HEIGHT = 720;
    public static SceneManager sceneManager;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        sceneManager = new SceneManager(primaryStage);

        Parent auth = FXMLLoader.load(getClass().getResource("/AuthScene/interface.fxml"));
        primaryStage.setTitle("Авторизация");
        ManagedScene mainScene = new ManagedScene(auth, 400, 250, sceneManager);
        mainScene.getStylesheets().add("/AuthScene/style.css");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
        /*BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        EventLoopGroup group = new NioEventLoopGroup();
        System.out.println(group.isShutdown() + " " + group.isTerminated() + " " + group.isShuttingDown());
        try
        {
            Bootstrap bootstrap = new Bootstrap().group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientInitializer());
            ChannelFuture future = bootstrap.connect(InetAddress.getLocalHost(), 10650).sync();
            System.out.println(group.isShutdown() + " " + group.isTerminated() + " " + group.isShuttingDown());

            String msg;
            //do            {
                msg = in.readLine();
                System.out.println("Me: " + msg);
                future.channel().writeAndFlush("through channel: " + msg);
                future.sync();
            //} while (!msg.equals("stop"));
        }
        catch (InterruptedException | IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println(group.isShutdown() + " " + group.isTerminated() + " " + group.isShuttingDown());
            group.shutdownGracefully();
            System.out.println(group.isShutdown() + " " + group.isTerminated() + " " + group.isShuttingDown());
        }*/
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
        ClientAgent.getInstance().close();
    }
}
