package TaxService;

import TaxService.DAOs.*;
import TaxService.Netty.ClientAgent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ClientMain extends Application
{
    public static final int DEFAULT_WINDOW_WIDTH = 1280;
    public static final int DEFAULT_WINDOW_HEIGHT = 720;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent authSceneFXML = FXMLLoader.load(getClass().getResource("/AuthScene/interface.fxml"));
        primaryStage.setTitle("Авторизация");
        Scene authScene = new Scene(authSceneFXML);
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
