package TaxService.SceneControllers;

import TaxService.*;
import TaxService.Netty.ClientAgent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.codec.digest.DigestUtils;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;

import static TaxService.PhraseBook.PORT;

public class AuthController
{
	@FXML
	public GridPane root;
	@FXML
	public TextField loginField;
	@FXML
	public PasswordField passField;
	@FXML
	public Label statusLabel;

	private Callback onAuth;
	private Callback onExceptionReceived;

	public void initialize()
	{
		onExceptionReceived = o -> Platform.runLater(() ->
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Ошибка");
			alert.setHeaderText("При выполнении операции произошла ошибка");
			alert.setContentText(o.toString());
			alert.showAndWait();
		});
		ClientAgent.subscribeExceptionReceived(onExceptionReceived);

		onAuth = o ->
		{
			final String accessResult = o.toString();
			Platform.runLater(() ->
			{
				switch (accessResult)
				{
					case PhraseBook.ACCESS_RESULT_SUCCESS:
						try
						{
							//https://stackoverflow.com/questions/13246211/javafx-how-to-get-stage-from-controller-during-initialization
							FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/MainScene/interface.fxml"));
							Parent mainSceneFXML = loader.load();
							Stage mainStage = new Stage();
							mainStage.setTitle("База данных налоговой инспекции");
							mainStage.setX((Toolkit.getDefaultToolkit().getScreenSize().width - ClientMain.DEFAULT_WINDOW_WIDTH) / 2);
							mainStage.setY((Toolkit.getDefaultToolkit().getScreenSize().height - ClientMain.DEFAULT_WINDOW_HEIGHT) / 2);
							mainStage.setOnCloseRequest(event -> ((MainController)loader.getController()).shutdown());
							Scene mainScene = new Scene(mainSceneFXML);
							mainScene.getStylesheets().add("/MainScene/style.css");
							mainStage.setScene(mainScene);
							((Stage)root.getScene().getWindow()).close();
							mainStage.show();
							ClientAgent.unsubscribeAuth(onAuth);
							ClientAgent.unsubscribeExceptionReceived(onExceptionReceived);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						break;
					case PhraseBook.ACCESS_RESULT_INVALID_LOGIN_PASSWORD:
					case PhraseBook.ACCESS_RESULT_ALREADY_LOGGED:
					default:
						statusLabel.setText(accessResult.equals(PhraseBook.ACCESS_RESULT_ALREADY_LOGGED) ?
								"Пользователь уже авторизован с другого компьютера" : "Доступ запрещён");
						statusLabel.setVisible(true);
						ClientAgent.getInstance().close();
						break;
				}
			});
		};
		ClientAgent.subscribeAuth(onAuth);
	}

	public void auth(ActionEvent actionEvent) throws IOException
	{
		if (!ClientAgent.doesInstanceExist())
		{
			statusLabel.setVisible(false);
			//Imagine it is encrypted!
			String msg = PhraseBook.AUTH + PhraseBook.SEPARATOR +
					loginField.getText() + PhraseBook.SEPARATOR +
					passField.getText();
				//DigestUtils.sha256Hex(DigestUtils.sha256Hex(passField.getText()));
			try
			{
				ClientAgent.buildInstance(InetAddress.getLocalHost(), PORT);
				ClientAgent.getInstance().setLogin(loginField.getText());
				ClientAgent.getInstance().send(msg);
			}
			catch (InvocationTargetException e)
			{
				statusLabel.setText("Соединение не установлено");
				statusLabel.setVisible(true);
			}
		}
	}
}
