package TaxService;

import TaxService.Netty.ClientAgent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ClientMain extends Application
{
    public static final int DEFAULT_WINDOW_WIDTH = 1280;
    public static final int DEFAULT_WINDOW_HEIGHT = 720;
    public static SceneManager sceneManager;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setResizable(false);
        sceneManager = new SceneManager(primaryStage);
        Parent authSceneFXML = FXMLLoader.load(getClass().getResource("/AuthScene/interface.fxml"));
        primaryStage.setTitle("Авторизация");
        ManagedScene authScene = new ManagedScene(authSceneFXML, 400, 250, sceneManager);
        authScene.getStylesheets().add("/AuthScene/style.css");
        primaryStage.setScene(authScene);
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
