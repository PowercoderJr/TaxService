package TaxService;

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

        Parent root = FXMLLoader.load(getClass().getResource("/MainScene/interface.fxml"));
        primaryStage.setTitle("Вас заарештовано!");
        ManagedScene mainScene = new ManagedScene(root, DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT, sceneManager);
        mainScene.getStylesheets().add("/MainScene/style.css");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
