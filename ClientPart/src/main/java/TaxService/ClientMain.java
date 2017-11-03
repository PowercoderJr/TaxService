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
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
        if (ClientAgent.getInstance() != null)
            ClientAgent.getInstance().close();
    }
}
